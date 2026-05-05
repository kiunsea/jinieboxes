package com.omnibuscode.logic.mon;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.dao.FcmTokenDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.util.FcmNotificationUtil;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 기간 경과 물품 모니터링
 * 
 * @author KIUNSEA
 *
 */
public class ExpiredItemMonitor implements Runnable {

    private Logger log = LogManager.getLogger(ExpiredItemMonitor.class);
    
    @Override
    public void run() {
        
        log.debug("# ExpiredManager Start~");
        
		while (true) {
	        ItemDataAccessObject itemDao = new ItemDataAccessObject();
	        String strToday = JinieboxUtil.getTodayString();
	        try {
	            String strCloseToday = JinieboxUtil.getNextdayString(strToday, 7);

//	            FcmTokenDataAccessObject ftDao = new FcmTokenDataAccessObject();
	            List<JSONObject> expInfos = itemDao.getExpiryInfo(strCloseToday);
	            if (expInfos != null) {
	                Iterator<JSONObject> expInfoIter = expInfos.iterator();
	                JSONObject expInfo = null;
	                
//	                List<String> usrTokens = null;
//	                Iterator<String> usrTokenIter = null;
//	                String usrToken = null;
//	                FcmNotificationUtil fcmNoti = new FcmNotificationUtil();
	                
	                Object expInfoObj = null;
	                while (expInfoIter.hasNext()) {
	                    expInfoObj = expInfoIter.next();
	                    if (expInfoObj != null) {
	                        expInfo = (JSONObject) expInfoObj;
	                        this.sendExpiredInfoMessage(expInfo.get("useq").toString(), "기간 만료 또는 기간이 임박한 아이템이 총 " + expInfo.get("cnt") + "개 있습니다.");
	                        
//	                        usrTokens = ftDao.getTokens(expInfo.get("useq").toString());
//	                        if (usrTokens != null) {
//	                            usrTokenIter = usrTokens.iterator();
//	                            while (usrTokenIter.hasNext()) {
//	                                usrToken = usrTokenIter.next().toString();
//	                                log.debug("CHECK Token - " + usrToken);
//	                                fcmNoti.addNotification(usrToken, "지니박스에서 아이템을 확인하여 주세요.",
//	                                        "기간 만료 또는 기간이 임박한 아이템이 총 " + expInfo.get("cnt") + "개 있습니다.");
//	                                try {
//	                                    fcmNoti.flushNotifications();
//	                                } catch (Exception e) {
//	                                    log.error(ExceptionUtil.getExceptionInfo(e));
//	                                    // 전송 에러가 발생하는 토큰은 삭제해 버린다.
//	                                    ftDao.deleteToken(usrToken);
//	                                    log.error("전송 에러가 발생하여 토큰 삭제 - " + usrToken);
//	                                }
//	                            }
//	                        }
	                        
	                    }
	                }
	            }
	        } catch (Exception e) {
	            log.error(ExceptionUtil.getExceptionInfo(e));
	        }
	        
	        try {
	            int cycle = 24;
	            String cycleStr = PropertiesUtil.get("FCM_EXPIRED_NOTI_CYCLE");
	            if (cycleStr != null) {
	                cycle = Integer.parseInt(cycleStr);
	            }
	            Thread.sleep(1000 * 60 * 60 * cycle); // 반영 주기(초/SEC) - 기본값은 24시간
	        } catch (Exception e) {
	            log.error(ExceptionUtil.getExceptionInfo(e));
	        }
		}
        
    }
    
    public void sendExpiredInfoMessage(String seqUser, String msg) throws Exception {
        FcmTokenDataAccessObject ftDao = new FcmTokenDataAccessObject();
        List<String> usrTokens = ftDao.getTokens(seqUser);
        if (usrTokens != null && usrTokens.size() > 0) {
            FcmNotificationUtil fcmNoti = FcmNotificationUtil.getInstance();
            
            String usrToken = null;
            Iterator<String> usrTokenIter = usrTokens.iterator();
            while (usrTokenIter.hasNext()) {
                usrToken = usrTokenIter.next().toString();
                log.debug("CHECK Token - " + usrToken);
                fcmNoti.addNotification(usrToken, "지니박스에서 아이템을 확인하여 주세요.", msg);
            }
            
            try {
                fcmNoti.flushNotifications();
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
                // 전송 에러가 발생하는 토큰은 삭제해 버린다.
                ftDao.deleteToken(usrToken);
                log.error("전송 에러가 발생하여 토큰 삭제 - " + usrToken);
            }
        }
    }
}
