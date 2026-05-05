package com.omnibuscode.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * 주식회사 네이버와 자회사 클로바에서의 계정 정보를 관리하는 클래스 (싱글톤)
 * @author KIUNSEA
 *
 */
public class NaverAccountUtil {

    private Logger log = LogManager.getLogger(NaverAccountUtil.class);
    private Map<String, JSONObject> tempAuthCodeMap = null; //사용자 계정 발급 프로세스에서 사용하는 임시 인증 코드맵 {"$authcode" : {uid_naver,uid_clova,seq_user}, exp_time}}
    
	private static NaverAccountUtil instance;

	private NaverAccountUtil() {
		this.tempAuthCodeMap = new HashMap<String, JSONObject>();
		// 내부 속성 모니터 시작
		ExpiredMonitor em = new ExpiredMonitor();
		new Thread(em).start();
	}

	public static synchronized NaverAccountUtil getInstance() {
		if (instance == null) {
			instance = new NaverAccountUtil();
		}
		return instance;
	}
    
    /**
     * 계정 정보에 대해 제한된 시간안에만 접근 할 수 있는 임시 변수를 생성
     * @param uidNaver
     * @param uidClova
     * @param seqUser
     * @return 인증코드
     */
    public String createTempAuthCode(String uidNaver, String uidClova, String seqUser) {
        String authCode = null;
        do {
            authCode = VerifyCodeGenerator.generateCode(0);
        } while (this.tempAuthCodeMap.containsKey(authCode)); //이미 등록한 코드라면 다시 생성
        
        JSONObject accountInfo = new JSONObject();
        accountInfo.put("uid_naver", uidNaver);
        accountInfo.put("uid_clova", uidClova);
        accountInfo.put("seq_user", seqUser);
        accountInfo.put("exp_time", System.currentTimeMillis() + (30 * 60 * 1000)); //기본값은 30분
        this.tempAuthCodeMap.put(authCode, accountInfo);
        
        return authCode;
    }
    
    /**
     * 인증코드와 객체를 제거
     * @param authCode
     */
    public void removeTempAuthCode(String authCode) {
        this.tempAuthCodeMap.remove(authCode);
    }
    
    /**
     * @param authCode
     * @return nuid
     */
    public String getNaverUid(String authCode) {
        JSONObject accInfo = this.getAccountInfo(authCode);
        return accInfo.get("uid_clova") != null ? accInfo.get("uid_naver").toString() : null;
    }
    
    /**
     * @param authCode
     * @return cuid
     */
    public String getClovaUid(String authCode) {
        JSONObject accInfo = this.getAccountInfo(authCode);
        return accInfo.get("uid_clova") != null ? accInfo.get("uid_clova").toString() : null;
    }
    
    /**
     * 계정정보를 반환
     * @param authCode
     * @return 유효시간이 지난 경우엔 null
     */
    public JSONObject getAccountInfo(String authCode) {
        Object accInfoObj = this.tempAuthCodeMap.get(authCode);
        JSONObject accInfo = accInfoObj != null ? (JSONObject) accInfoObj : null;
        if (accInfo != null) {
            String expTime = accInfo.get("exp_time").toString();
            long expTimeL = Long.parseLong(expTime);
            if (System.currentTimeMillis() > expTimeL) {
                this.tempAuthCodeMap.remove(authCode);
                return null;
            }
        }
        return accInfo;
    }
    
    /**
     * 관리중인 계정 정보맵을 반환
     * @return
     */
    public Map<String, JSONObject> getAccountInfoMap() {
        return this.tempAuthCodeMap;
    }
    
    /**
     * 내부 속성값 변경을 감시 (만료시간을 검사하여 authcode 를 제거)
     * @author KIUNSEA
     *
     */
    private class ExpiredMonitor implements Runnable {

        @Override
        public void run() {

            while (true) {
                Map<String, JSONObject> accInfoMap = NaverAccountUtil.getInstance().getAccountInfoMap();
                Set<String> authCodes = accInfoMap.keySet();
                Iterator<String> authCodeIter = authCodes.iterator();
                while (authCodeIter.hasNext()) {
                    String authCode = authCodeIter.next().toString();
                    JSONObject accInfo = (JSONObject) accInfoMap.get(authCode);
                    if (accInfo != null) {
                        String expTime = accInfo.get("exp_time").toString();
                        long expTimeL = Long.parseLong(expTime);
                        if (System.currentTimeMillis() > expTimeL) {
                            NaverAccountUtil.getInstance().removeTempAuthCode(authCode);
                        }
                    }
                }

                try {
                    Thread.sleep(1000 * 10); // 10초마다 실행
                } catch (Exception e) {
                    log.error("[PropertyMonitor]" + e);
                }
            }
        }

    }
}
