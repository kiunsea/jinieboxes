package com.omnibuscode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.utils.ExceptionUtil;

/**
 * Ref : https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send
 * 
 * @author KIUNSEA
 *
 */
public class FcmNotificationUtil {

    private static FcmNotificationUtil instance;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(FcmNotificationUtil.class);
    private Map<String, ArrayList<FcmMessage>> notiMap = null;

    /**
     * Firebase 자격증명 파일이 존재하고 초기화에 성공한 경우에만 true.
     * false 인 경우 모든 send 계열 메서드는 no-op 으로 동작한다.
     */
    private final boolean enabled;

    private FcmNotificationUtil() {
        this.enabled = initializeFirebase();
        if (!this.enabled) {
            log.info("FCM 비활성화: 자격증명 파일이 없어 알림 기능을 건너뜁니다.");
        }
    }

    /**
     * Firebase 초기화. 성공 시 true, 자격증명 파일이 없거나 IO 오류가 나면 false.
     */
    private boolean initializeFirebase() {
        String tokenFileName = SafeProps.getString("FCM_REFRESH_TOKEN_FILE");
        if (tokenFileName == null) {
            return false;
        }
        File serviceAccountFile = new File(EnvSYS.SYS_RES_PATH, tokenFileName);
        if (!serviceAccountFile.isFile()) {
            log.warn("FCM_REFRESH_TOKEN_FILE 가 존재하지 않습니다: " + serviceAccountFile.getAbsolutePath());
            return false;
        }
        try (FileInputStream serviceAccount = new FileInputStream(serviceAccountFile)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
            return true;
        } catch (IOException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
            return false;
        }
    }

    /** FCM 사용 가능 여부. */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public static synchronized FcmNotificationUtil getInstance() {
        if (instance == null) {
            instance = new FcmNotificationUtil();
        }
        return instance;
    }
    
    /**
     * notification 을 단일 디바이스에(특정 token) 발송하는 메서드
     * 
     * @param token
     * @param title
     * @param body
     * @throws FirebaseMessagingException 
     */
    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        if (!this.enabled) {
            log.debug("FCM 비활성화 상태 - sendNotification 건너뜀 (token=" + token + ")");
            return;
        }
        try {
            // notification 객체를 생성
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setImage("https://jiniebox.com/jbs/favicon.ico")
                    .build();
            // 메시지 객체를 생성
            Message message = Message.builder().setNotification(notification).setToken(token).build();
            // FirebaseMessaging 객체를 가져옴
            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            // 메시지를 발송하고 결과를 받음
            String response = firebaseMessaging.send(message);
            // 결과를 콘솔에 출력
            log.info("[Successfully sent message] : " + response);
        } catch (FirebaseMessagingException e) {
            // 예외 처리
            log.debug(e.getMessage() + " - " + token);
            throw e;
        }
    }
    
    /**
     * notification 을 내부 저장소에 누적시킨다
     * 
     * @param token
     * @param title
     * @param body
     */
    public void addNotification(String token, String title, String body) {
        if (!this.enabled) {
            log.debug("FCM 비활성화 상태 - addNotification 건너뜀 (token=" + token + ")");
            return;
        }
        if (notiMap == null) {
            notiMap = new HashMap<String, ArrayList<FcmMessage>>();
        }

        ArrayList<FcmMessage> msgs = null;
        if (notiMap.containsKey(token)) {
            msgs = (ArrayList<FcmMessage>) notiMap.get(token);
        } else {
            msgs = new ArrayList<FcmMessage>();
        }
        FcmMessage msg = new FcmMessage();
        msg.setTitle(title);
        msg.setBody(body);

        msgs.add(msg);
        notiMap.put(token, msgs);
    }
    
    /**
     * 내부 저장소에 저장한 notification 들을 일괄 전송한다.
     * @throws FirebaseMessagingException 
     */
    public void flushNotifications() throws FirebaseMessagingException {
        if (!this.enabled) {
            return;
        }
        if (this.notiMap != null) {
            Iterator<String> keyIter = this.notiMap.keySet().iterator();
            List<String> removeList = new ArrayList<String>();
            
            String key = null;
            List<FcmMessage> msgs = null;
            Iterator<FcmMessage> msgIter = null;
            FcmMessage msg = null;
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                msgs = (List) this.notiMap.get(key);
				log.debug("[Noti Messages size] : " + msgs.size());
                msgIter = msgs.iterator();
                while (msgIter.hasNext()) {
                    msg = (FcmMessage) msgIter.next();
                    this.sendNotification(key, msg.getTitle(), msg.getBody());
                }
                removeList.add(key);
            }

            if (!removeList.isEmpty()) {
                Iterator<String> rmKeyIter = removeList.iterator();
                while (rmKeyIter.hasNext()) {
                    String rmKey = rmKeyIter.next().toString();
                    this.notiMap.remove(rmKey);
                }
            }
            
            keyIter = null;
            removeList = null;
        }
    }
    
    /**
     * notification 을 여러 디바이스에(복수 token) 발송하는 메서드
     * 
     * @param usrTokens
     * @param title
     * @param body
     * @throws FirebaseMessagingException
     */
    public void sendNotifications(List<String> usrTokens, String title, String body) throws FirebaseMessagingException {
        if (!this.enabled) {
            log.debug("FCM 비활성화 상태 - sendNotifications 건너뜀 (count=" + (usrTokens != null ? usrTokens.size() : 0) + ")");
            return;
        }
        if (usrTokens != null) {
            Iterator<String> usrTokenIter = usrTokens.iterator();
            String usrToken = null;
            while (usrTokenIter.hasNext()) {
                usrToken = usrTokenIter.next().toString();
                this.addNotification(usrToken, title, body);
            }
            this.flushNotifications();
        }
    }
    
    class FcmMessage {
        private String title = null;
        private String body = null;
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
    }
}
