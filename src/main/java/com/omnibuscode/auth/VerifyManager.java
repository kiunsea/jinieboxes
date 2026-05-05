package com.omnibuscode.auth;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;


/**
 * JINIE 서비스 사용자의 이메일 인증용 관리 클래스<br/>
 * verify code를 생성하고 db에 저장한다<br/>
 * 7일이 초과된 code에 대해서는 code를 만료시킨다
 * @author KIUNSEA
 *
 */
public class VerifyManager {

    private Logger log = LogManager.getLogger(VerifyManager.class);
    private static VerifyManager instance;

    /**
     * VerifyManager 초기화
     */
    private VerifyManager(String sysResPath, String userPropertiesPath) {
        
        EnvSYS.SYS_RES_PATH = sysResPath;
        PropertiesUtil.USER_PROPERTIES_PATH = userPropertiesPath;
        
        //내부 속성 모니터 시작
        PropertyMonitor pm = new PropertyMonitor();
        new Thread(pm).start();
        
    }
    
    /**
     * 내부 속성값 변경을 감시
     * @author KIUNSEA
     *
     */
    private class PropertyMonitor implements Runnable {

        @Override
        public void run() {

            while (true) {

            	/**
            	 * 인증기간이 만료된 사용자 정보를 삭제한다
            	 */
            	UserDataAccessObject userDao = new UserDataAccessObject();
            	StoreDataAccessObject storeDao = new StoreDataAccessObject();
        		BoxDataAccessObject boxDao = new BoxDataAccessObject();
            	try {
					List<String> seqList = userDao.getExpiredSeqList(JinieboxUtil.getTodayString());
					if (seqList != null) {
						Iterator<String> seqIter = seqList.iterator();
						String seqUser, seqStore = null;
						while (seqIter.hasNext()) {
							seqUser = seqIter.next().toString();
							JSONObject storeObj = storeDao.getOwnStore(seqUser);
							if (storeObj != null) {
								seqStore = storeObj.get("seq").toString();
								storeDao.delete(seqStore);
								int[] seqBoxes = boxDao.getAllSeq(seqStore);
								for (int i = 0; i < seqBoxes.length; i++) {
									boxDao.delete(seqBoxes[i] + "");
								}
							}
							userDao.delete(seqUser);
						}
					}
				} catch (Exception e) {
				    log.error(ExceptionUtil.getExceptionInfo(e));
				}

                try {
                    Thread.sleep(1000 * 60 * 60); // 1시간마다 실행
                } catch (Exception e) {
                    log.error("[PropertyMonitor]" + e);
                }
            }
        }

    }
	
    public static synchronized VerifyManager getInstance(String sysResPath, String userPropertiesPath) {
        if (instance == null) {
        	System.out.println("[VerifyManager.java] Create New VerifyManager !!");
            instance = new VerifyManager(sysResPath, userPropertiesPath);
        }
        return instance;
    }
}
