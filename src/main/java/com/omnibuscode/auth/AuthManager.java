package com.omnibuscode.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.GcpService;
import com.omnibuscode.util.KeycodeGenerator;

/**
 * JINIE 서비스 사용자 인증용 관리 클래스<br/>
 * USER HASH 테이블로 UID(사용자 아이디)와 AUTHKEY를 저장 관리한다<br/>
 * UID는 현재 인증 기능상 bixby user id 가 될 수도 있고 jinie user id 가 될 수도 있다<br/>
 * 세션에는 UserSession 인스턴스가 저장되어 있음 (key : EnvSYS.KEY_USER_SESSION)
 * @author KIUNSEA
 *
 */
public class AuthManager {

    private Logger log = LogManager.getLogger(AuthManager.class);
    private static AuthManager instance;

    private Map<String, String> authKeycode = null; // jiniebox 서비스 이용을 위한 인증키코드 테이블
    private Map<String, HashInfo> userHash = null; // 사용자 인증용 해쉬맵 (<uid, HashInfo>)

    /**
     * AuthManager 초기화
     */
    private AuthManager() {
        this.userHash = new HashMap<String, HashInfo>();
        
        //내부 속성 모니터 시작
        PropertyMonitor pm = new PropertyMonitor();
        new Thread(pm).start();
    }
    
    /**
     * 등록되는 키 정보
     * @author KIUNSEA
     *
     */
    public class HashInfo {
        private String key = null; //authkey
        private long regtime = -1; //regist time
        
        public HashInfo(String key) {
            this.key = key;
            this.regtime = System.currentTimeMillis();
        }
        
        public String getKey() {
            return key;
        }
        public long getRegtime() {
            return regtime;
        }
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
                Map<String, HashInfo> userHash = AuthManager.getInstance().getUserHash();
				if (userHash.size() > 0) {
					Iterator<String> keyIter = userHash.keySet().iterator();
					List<String> targetKeys = new ArrayList<String>();
					String key = null;
					HashInfo hi = null;
					Object keyObj = null;
					while (keyIter.hasNext()) {
						keyObj = keyIter.next();
						if (keyObj != null) {
							key = keyObj.toString();
							hi = (HashInfo) userHash.get(key);
							// 등록시간이 60분 지나면 인증 정보를 제거한다
							if (System.currentTimeMillis() - hi.getRegtime() > 600000 * 6) {
								log.debug("[" + key + "/" + hi.getRegtime() + "] 유효시간 초과");
								targetKeys.add(key);
							}
						}
					}

					if (targetKeys.size() > 0) {
						Iterator<String> keysIter = targetKeys.iterator();
						while (keysIter.hasNext()) {
							userHash.remove(keysIter.next());
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

	public void setAuthKeycode(Map<String, String> keycode) {
		log.debug("authkeycode size = " + keycode.size());
		this.authKeycode = keycode;
	}

    public Map<String, String> getAuthKeycode() {
        return this.authKeycode;
    }
    
    public Map<String, HashInfo> getUserHash() {
        return this.userHash;
    }

    /**
     * 임의의 키 하나를 반환 (키는 사용자 인증용 해시맵에 <uid, key> 로 저장됨)
     * 
     * @return
     */
    public String getRandomAuthKey(String buid) {
        Random rg = new Random();
        int rdVal = rg.nextInt(KeycodeGenerator.KEYCODE_COUNT);
        String key = (rdVal < 10) ? "0" + rdVal : "" + rdVal;
        this.userHash.put(buid, new HashInfo(key));
        return key;
    }

    /**
     * 해당 키의 코드값을 반환
     * 
     * @param key
     * @return
     */
    public String getAuthcode(String key) {
        if (key != null && this.authKeycode.containsKey(key))
            return this.authKeycode.get(key).toString();
        else
            return null;
    }

    /**
     * keycode 키를 저장
     * 
     * @param uid
     * @param key
     */
    public void putAuthkey(String uid, String key) {
        this.userHash.put(uid, new HashInfo(key));
    }

    /**
     * 사용자 인증용 해시맵에 인증키가 저장되었는지 여부
     * 
     * @param uid
     * @return
     */
    public boolean isAuthkey(String uid) {
        return this.userHash.containsKey(uid) && this.userHash.get(uid) != null;
    }

    /**
     * 저장된 keycode 키를 반환 (사용자 인증용 해시맵에서 삭제)
     * 
     * @param uid
     * @return
     */
    public String getAuthkey(String uid) {
        HashInfo hi = this.userHash.remove(uid);
        return hi != null ? hi.getKey() : null;
    }

    /**
     * * [buid, authcode] 세트 또는 [juid, jupw] 세트로 사용자를 인증한다<br/>
     * * 최초 로그인시 : 입력한 인증정보를 확인하고 사용자 세션을 생성한다<br/>
     * * 서비스 요청시 : 인증 결과를 반환한다 (사용자 세션 확인후 세션이 없다면 authcode 로 사용자를 인증한다)<br/>
     * * 검증 결과는 세션에서 EnvSYS.KEY_USER_VALID_INFO 키로 확인 가능하다<br/>
     *   - AUTHED : 인증 성공시 true<br/>
     *   - CODE 000 : 세션 로그인 또는 저장 성공<br/>
     *   - CODE 001 : 사용자 정보 없음<br/>
     *   - CODE 010 : 입력 정보 부정확<br/>
     *   - MSG : 확인 메세지<br/>
     * * 검증된 사용자에 대해서는 사용자 세션을 생성한다
	 * @param req
	 * @throws Exception
	 */
    public void validUser(HttpServletRequest req) throws Exception {

        AuthInfo authUserInfo = null;

        String buid = req.getParameter("buid");
        String authcode = req.getParameter("authcode");
        String juid = req.getParameter("juid");
        String jupw = req.getParameter("jupw");

        log.debug("(param) buid-" + buid);
        log.debug("(param) bixby authcode-" + authcode);
        log.debug("(param) juid-" + juid);
        log.debug("(param) jupw-" + jupw);

        // 세션 검사 먼저
        HttpSession sess = req.getSession(true);
        Object usObj = sess.getAttribute(EnvSYS.KEY_USER_SESSION); // saved user session
        if (usObj != null) {

            UserSession us = (UserSession) usObj;
            authUserInfo = (AuthInfo) sess.getAttribute(EnvSYS.KEY_USER_AUTH_INFO);

            if (us.getBuid() != null) {
                log.debug("저장된 세션으로 로그인함(BUID) : " + us.getBuid());
            } else if (us.getJuid() != null) {
                log.debug("저장된 세션으로 로그인함(JUID) : " + us.getJuid());
            } else {
                log.debug("저장된 세션에 아이디 정보가 없음");
            }

        } else {

            /**
             * 세션 로그인이 실패한 경우 사용자 검증
             */
            log.debug("세션 생성을 위한 사용자 정보를 확인합니다.");
            log.debug("req param - AUTHCODE(input):" + authcode + ", BUID:" + buid + ", JUID:" + juid);
            boolean validUser = false;

            UserDataAccessObject userDao = new UserDataAccessObject();
            JSONObject userJson = null;
            if (buid != null && this.isAuthkey(buid)) {
                authUserInfo = new AuthBixbyInfo(); // auth keycode 검사 (bixby 와 같은 외부 서비스와의 연동시)
                String akey = this.getAuthkey(buid);
                log.debug("hashmap - AUTH_KEY(stored):" + akey);

                if (akey != null) {
                    String acode = this.getAuthcode(akey);
                    log.debug("hashmap - AUTH_CODE(stored):" + acode);

                    if (acode != null && acode.equals(authcode) && buid != null) {
                        userJson = userDao.getUserByBuid(buid);
                        if (userJson == null) {
                            log.debug("인증 실패 : " + EnvSYS.RESGUEST_USER);
                            authUserInfo.setValidcode(EnvSYS.RESGUEST_USER);
                            authUserInfo.setValidmsg("사용자 아이디가 없습니다");
                        } else if (userJson.get("verified") != null) {
                            int uv = Integer.parseInt(userJson.get("verified").toString());
                            if (uv == 1) {
                                log.debug("인증 성공");
                                validUser = true;
                            } else if (uv < 1) {
                                log.debug("인증 실패 : " + EnvSYS.RESNOTYET_VERIFY);
                                authUserInfo.setValidcode(EnvSYS.RESNOTYET_VERIFY);
                                authUserInfo.setValidmsg("이메일 인증이 필요합니다");
                            } else {
                                log.debug("인증 실패 : " + EnvSYS.RESAUTH_FAIL);
                                authUserInfo.setValidcode(EnvSYS.RESAUTH_FAIL);
                                authUserInfo.setValidmsg("인증 정보가 올바르지 않습니다");
                            }
                        }
                    } else {
                        log.debug("인증 실패 : " + EnvSYS.RESAUTH_FAIL);
                        authUserInfo.setValidcode(EnvSYS.RESAUTH_FAIL);
                        authUserInfo.setValidmsg("인증 정보가 올바르지 않습니다");
                    }
                } else {
                    log.debug("인증 정보가 없습니다 : " + EnvSYS.RESAUTH_FAIL);
                    authUserInfo.setValidcode(EnvSYS.RESAUTH_FAIL);
                    authUserInfo.setValidmsg("인증 정보가 없습니다");
                }
            } else if (juid != null && jupw != null) {
                authUserInfo = new AuthWebInfo(); // 웹로그인시
                userJson = userDao.getUserByJuid(juid);
                if (userJson == null) {
                    log.debug("인증 실패 : " + EnvSYS.RESGUEST_USER);
                    authUserInfo.setValidcode(EnvSYS.RESGUEST_USER);
                    authUserInfo.setValidmsg("사용자 아이디가 없습니다");
                } else if (userJson.get("verified") != null) {
                    if (Integer.parseInt(userJson.get("verified").toString()) != 1) {
                        log.debug("인증 실패 : " + EnvSYS.RESNOTYET_VERIFY);
                        authUserInfo.setValidcode(EnvSYS.RESNOTYET_VERIFY);
                        authUserInfo.setValidmsg("이메일 인증이 필요합니다");
                    } else {
                        if (userJson.get("jupw").toString().equals(jupw)) {
                            log.debug("인증 성공 : " + EnvSYS.RESCODE_SUCC);
                            authUserInfo.setValidcode(EnvSYS.RESCODE_SUCC);
                            authUserInfo.setValidmsg("인증에 성공하였습니다");
                            validUser = true;
                        } else {
                            log.debug("인증 실패 : " + EnvSYS.RESAUTH_FAIL);
                            authUserInfo.setValidcode(EnvSYS.RESAUTH_FAIL);
                            authUserInfo.setValidmsg("사용자 암호가 올바르지 않습니다");
                        }
                    }
                }
            }

            /**
             * 사용자 세션 생성<br/>
             * bixby와 같은 외부 클라이언트 서비스는 auth keycode 를 주고 받으면서 validuser 를 수행해야 하고<br/>
             * 지니박스 서비스 사용자는 로그인을 통해 validuser 를 수행해야 한다
             */
            if (validUser == true) {
                this.createUserSession(req, authUserInfo, userJson);
                userDao.setVisited(userJson.get("seq").toString());
            }
        }

        sess.setAttribute(EnvSYS.KEY_USER_AUTH_INFO, authUserInfo);

        // XXX 개발모드 코드가 불필요하다면 삭제한다
//		if ("TRUE".equals(PropertiesUtil.get("DEVMODE").toString().toUpperCase())) {
//			log.debug("DEVMODE TRUE ##############################################");
//			authUserInfo.setAuthed(false); // 개발중일땐 무조건 통과시킨다
//		}
    }

	/**
	 * 사용자 인증 결과를 boolean으로 리턴<br/>
     * 호출시 this.validUser(req) 를 함께 수행하여 사용자 세션과 함께 검증 결과를 저장한다<br/>
     * 세션 조회시 파라미터 이름 -> EnvSYS.KEY_USER_AUTH_INFO, 반환되는 클래스 인스턴스는 AuthUserInfo
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public boolean hasUserAuthed(HttpServletRequest req) throws Exception {
		this.validUser(req);
		HttpSession sess = req.getSession(true);
		Object uviObj = sess.getAttribute(EnvSYS.KEY_USER_AUTH_INFO); // saved user session
		if (uviObj != null) {
			AuthInfo authInfo = (AuthInfo) uviObj;
			return authInfo.hasAuthed();
		}
		return false;
	}
	
	/**
	 * 세션에 저장된 사용자 검증 결과에 대한 정보를 반환한다
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public AuthInfo getUserAuthInfo(HttpServletRequest req) {
		HttpSession sess = req.getSession(true);
		Object aiObj = sess.getAttribute(EnvSYS.KEY_USER_AUTH_INFO);
		return aiObj != null ? (AuthInfo) aiObj : null;
	}
	
	public void removeUserAuthInfo(HttpServletRequest req) {
		HttpSession sess = req.getSession(true);
		sess.removeAttribute(EnvSYS.KEY_USER_AUTH_INFO);
	}
	
	/**
	 * 사용자 세션을 생성한다
	 * @param req
	 * @param authUserInfo
	 * @param userJson
	 * @throws Exception 
	 */
	public void createUserSession(HttpServletRequest req, AuthInfo authUserInfo, JSONObject userJson) throws Exception {
		
		HttpSession sess = req.getSession(true);
		if (authUserInfo == null) authUserInfo = new AuthUserInfo(); //예외처리
		
		if (userJson != null) {
			UserSession us = new UserSession();
			us.setSeq(userJson.get("seq") != null ? userJson.get("seq").toString() : null);
			us.setJuid(userJson.get("juid") != null ? userJson.get("juid").toString() : null);
			us.setBuid(userJson.get("buid") != null ? userJson.get("buid").toString() : null);
			us.setJuname(userJson.get("juname") != null ? userJson.get("juname").toString() : null);
			us.setInsertTime(userJson.get("insert_time") != null ? userJson.get("insert_time").toString() : null);
            us.setIsParter(
                    (userJson.get("is_partner") != null) ? 
                            Integer.parseInt(userJson.get("is_partner").toString()) : 1);
            us.setFirstVisit(
                    (userJson.get("first_visit") != null) ? 
                            Integer.parseInt(userJson.get("first_visit").toString()) : 0);
			if (userJson.get("seq_defstore") != null) {
				StoreDataAccessObject storeDao = new StoreDataAccessObject();
				
				JSONObject defStoreJson = storeDao.getStore(userJson.get("seq_defstore").toString());
				us.setDefStoreInfo(defStoreJson);
				
				JSONObject ownStoreJson = storeDao.getOwnStore(userJson.get("seq").toString());
				us.setOwnStoreInfo(ownStoreJson);
			}
			sess.setAttribute(EnvSYS.KEY_USER_SESSION, us); // 세션에 저장

			log.debug("세션에 저장됨 [buid:" + us.getBuid() + ", juid:" + us.getJuid() + "]");
			authUserInfo.setAuthed(true);
			authUserInfo.setValidcode(EnvSYS.RESCODE_SUCC);
			authUserInfo.setValidmsg("세션에 저장됨 [buid:" + us.getBuid() + ", juid:" + us.getJuid() + "]");
			
			//마지막 로그인 시간 저장
			UserDataAccessObject userDao = new UserDataAccessObject();
			userDao.setLastSignin(us.getSeq());
		} else {
			log.debug("사용자 인증 실패");
			authUserInfo.setAuthed(false);
			authUserInfo.setValidcode(EnvSYS.RESAUTH_FAIL);
			authUserInfo.setValidmsg("사용자 인증 실패(UserSession is NULL)");
		}
		sess.setAttribute(EnvSYS.KEY_USER_AUTH_INFO, authUserInfo);
	}
	
	/**
	 * 세션에 저장된 인증 사용자에 대해 사용자 정보를 반환한다<br/>
	 * valid user 를 수행한 사용자에 대해 세션에서 사용자 정보 취득이 가능하다
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public UserSession getUserSession(HttpServletRequest req) {
		HttpSession sess = req.getSession(true);
		Object usObj = sess.getAttribute(EnvSYS.KEY_USER_SESSION);
		if (usObj != null) {
		    UserSession us = (UserSession) usObj;
		    
            try {
                /**
                 * Check Google Access Token
                 */
                String gat = us.getGoogleAccessToken();
                if (gat == null) {
                    GcpService gcpSvc = new GcpService();
                    gat = gcpSvc.getAccessToken(us.getSeq());
                    us.setGoogleAccessToken(gat);
                }
            } catch (Exception e) {
                log.error("Check Google Access Token Error!!");
            }
		}
		return usObj != null ? (UserSession) usObj : null;
	}
	
	/**
	 * 사용자 세션을 삭제한다
	 * @param req
	 */
	public void removeUserSession(HttpServletRequest req) {
	    HttpSession session = req.getSession();
	    session.invalidate();
	}
	
    public static synchronized AuthManager getInstance() {
        if (instance == null) {
        	System.out.println("[AuthManager.java] Create New AuthManager !!");
            instance = new AuthManager();
        }
        return instance;
    }
}
