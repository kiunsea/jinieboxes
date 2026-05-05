package com.omnibuscode.ctrl;



import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.AccountClovaDataAccessObject;
import com.omnibuscode.dao.AccountNaverDataAccessObject;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.InviteDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.util.VerifyCodeGenerator;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * @author KIUNSEA
 *
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogUtil.getLog(UserServlet.class);
    private Logger log = LogManager.getLogger(UserServlet.class);

    public void init() {;}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	//request parameter 로깅
        String pName = null;
        Enumeration<String> enums = req.getParameterNames();
        while(enums.hasMoreElements()) {
        	pName = enums.nextElement().toString();
        	log.debug("(param) "+pName+" - "+req.getParameter(pName));
        }

		doGet(req, res);
	}

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        JSONObject resObj = new JSONObject();
        
        String cmdHttp = req.getParameter("cmd");
        
        try {
            if ("checkId".equals(cmdHttp)) {
                String juid = req.getParameter("juid");
                //TODO 아이디 체크후 결과 반환
            } else if ("existEmail".equals(cmdHttp)) { // 이메일 조회
                String userEmail = req.getParameter("user_email");
                UserDataAccessObject uDao = new UserDataAccessObject();
                boolean rstCheck = uDao.existJuid(userEmail);
                resObj.put("result", rstCheck);
            } else if ("isMember".equals(cmdHttp)) { // 회원 여부 확인 및 등록 링크 반환
            	resObj = this.isMember(req);
            } else if ("regist".equals(cmdHttp)) { // 사용자 신규 등록 요청 처리
                resObj = this.regist(req);
            } else {
            	if (AuthManager.getInstance().hasUserAuthed(req)) {
	                if (cmdHttp != null) {
	                    resObj = null;
	                    if ("info".equals(cmdHttp)) { // 사용자 조회
	                        resObj = this.info(req);
	                    } else if ("setDefStore".equals(cmdHttp)) { // 기본 저장소 지정
	                        resObj = this.setDefaultStore(req);
	                    } else if ("predictiveId".equals(cmdHttp)) { // 자동완성용
	                        resObj = this.predictiveId(req);
	                    }
	                } else {
	                    resObj.put("code", EnvSYS.RESCODE_FAIL);
	                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
	                }
	            } else {	            	
	        		AuthInfo ai = AuthManager.getInstance().getUserAuthInfo(req);
	            	if (ai != null) {
	            		resObj.put("code", ai.getValidcode());
	            		resObj.put("msg", ai.getValidmsg());
	            		log.debug(ai.getValidmsg());
	            	} else {
	            		resObj.put("code", EnvSYS.RESCODE_FAIL);
	            		resObj.put("msg", EnvSYS.RESMSG_FAIL);
	            	}
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        if (resObj == null) resObj = new JSONObject();
        /**
         * (Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
         */
        resObj.put("buid", req.getParameter("buid"));
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(req.getParameter("buid")));
        
        log.debug("res - " + resObj);
        
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resObj);
        out.flush();
        out.close();
    }
    
    /**
     * 회원 여부 확인 및 등록 링크 반환
     * @param req
     * @param buid
     * @return
     * @throws Exception
     */
    private JSONObject isMember(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        
        UserDataAccessObject uDao = new UserDataAccessObject();
        String resCode, resLabel, resUrl, resMsg = null;
        
        String buid = req.getParameter("buid");
        
        boolean b_rtn = uDao.existBuid(buid);
        if (b_rtn) {
            resCode = EnvSYS.RESCODE_SUCC;
            resLabel = "마이프로필";
            resUrl = PropertiesUtil.get("SERVICE_URL")+"/profile.jsp";
            resMsg = "이미 회원으로 가입되어 있습니다 사용자 정보를 확인하세요";
        } else {
            resCode = EnvSYS.RESCODE_FAIL;
            resLabel = "회원가입";
            resUrl = PropertiesUtil.get("SERVICE_URL")+"/signup.jsp";
            resMsg = "신규등록 절차를 진행합니다 회원가입 링크를 클릭해 주세요";
        }
        
        resObj.put("code", resCode); // response code
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("label", resLabel); // link label
        resObj.put("url", resUrl);  // link url
        resObj.put("msg", resMsg);  // message
        
        return resObj;
    }
    
    /**
     * 사용자 신규 등록 요청 처리
     * @param req
     * @param buid
     * @return
     * @throws Exception
     */
	private JSONObject regist(HttpServletRequest req) throws Exception {

		JSONObject resObj = new JSONObject();

		String buid = req.getParameter("buid");
		String juid = req.getParameter("juid");
		String jupw = req.getParameter("jupw");
		String juname = req.getParameter("juname");
		String sname = req.getParameter("sname");
		String storem = req.getParameter("storem"); // 자신의 저장소 생성 여부
		String defaultBoxT = req.getParameter("def_box_add"); // 기본 박스 생성
		String lifeBoxT = req.getParameter("life_box_add"); // 생활용품 템플릿 생성
		String refrigeBoxT = req.getParameter("refrige_box_add"); // 냉장고 템플릿 생성
		String stores = req.getParameter("stores"); // 다른이로부터 저장소 초대 여부
		String icode = req.getParameter("icode"); // 초대 코드

		UserDataAccessObject userDao = new UserDataAccessObject();
		StoreDataAccessObject storeDao = new StoreDataAccessObject();
		BoxDataAccessObject boxDao = new BoxDataAccessObject();
		String seqUser = null;
		String seqStore = null;
		boolean bSuccess = false;

		try {
			if (userDao.existJuid(juid)) {
			    
			    resObj.put("code", EnvSYS.RESCODE_FAIL);
				resObj.put("msg", "신규 회원 등록에 실패하였습니다\n" + juid + "는 이미 등록된 사용자 아이디입니다"); // message
			
			} else {
			    
				if (userDao.regist(juid, jupw, juname, buid)) {
	
					JSONObject userJson = userDao.getUserByJuid(juid);
					seqUser = userJson.get("seq").toString();
	
					// 자신의 저장소 생성
					if ("TRUE".equals(storem.toUpperCase())) {
						storeDao.regist(sname, seqUser);
						seqStore = storeDao.getSeq(seqUser);
						userJson.put("seq_defstore", seqStore);
	
						if (seqUser != null && seqStore != null) {
							String details = EnvSYS.RESMSG_CREATEBOXSYS;
							if ("TRUE".equals(defaultBoxT.toUpperCase())) {
							    //등록대기 보관함 생성
							    String def_detail = "자동 수집된 아이템이 대기하는 박스입니다.\\n다른 보관함으로 이동하지 않은 아이템은 저장일 기준으로 일정 시간이 지나면 자동 삭제됩니다.";
								int seqBoxInt = boxDao.insert(EnvSYS.RESERVED_WAITINGBOX, def_detail, "-1", seqStore);
								//샘플 아이템 등록
								int seqUserInt = Integer.parseInt(seqUser);
								String itemName = "아이템 샘플";
								ItemDataAccessObject itemDao = new ItemDataAccessObject();
			                    itemDao.insertItem(seqUserInt, seqBoxInt, itemName, 1, 0, Integer.parseInt(JinieboxUtil.getTodayString()), 0);
							}
							if ("TRUE".equals(lifeBoxT.toUpperCase())) {
								boxDao.insert(EnvSYS.RESERVED_COSMETICBOX, details, null, seqStore);
								boxDao.insert(EnvSYS.RESERVED_LIFEGOODSBOX, details, null, seqStore);
								boxDao.insert(EnvSYS.RESERVED_TOYSBOX, details, null, seqStore);
							}
							if ("TRUE".equals(refrigeBoxT.toUpperCase())) {
								boxDao.insert(EnvSYS.RESERVED_FRIDGE__COMP1, details, null, seqStore);
								boxDao.insert(EnvSYS.RESERVED_FRIDGE__COMP2, details, null, seqStore);
								boxDao.insert(EnvSYS.RESERVED_FREEZER_COMP, details, null, seqStore);
								boxDao.insert(EnvSYS.RESERVED_UTILITY_COMP, details, null, seqStore);
							}
							userDao.setDefaultStoreSeq(seqUser, seqStore);
	
							//XXX 개발모드 코드가 불필요하다면 삭제한다
//                            if (PropertiesUtil.get("DEVMODE").equals("true")) {
//                                // XXX 개발환경에서는 사용자 메일 인증 절차가 불필요하기 때문에 회원가입과 함께 사용자 세션을 생성한다
//                                AuthManager.getInstance().createUserSession(req, null, userJson);
//                            }
							
						}
					}
	
					// 저장소 초대 처리
					if ("TRUE".equals(stores.toUpperCase())) {
						InviteDataAccessObject invDao = new InviteDataAccessObject();
						JSONObject inviteInfo = invDao.getInvite(icode);
						if (inviteInfo != null) {
							ShareDataAccessObject shareDao = new ShareDataAccessObject();
                            boolean addRst = shareDao.add(seqUser, EnvSYS.CLASS_TYPE_STORE,
                                    inviteInfo.get("seq_object").toString(), inviteInfo.get("authority").toString());
	
							if (addRst) {
								JSONObject userInfo = userDao.getUser(juid, jupw);
								if (userInfo.get("seq_defstore") == null) {
									userDao.setDefaultStoreSeq(seqUser, inviteInfo.get("seq_store").toString());
								}
								invDao.delete(inviteInfo.get("seq").toString());
							}
						}
					}
					
                    HttpSession sess = req.getSession(true);
                    Object kakaoidObj = sess.getAttribute(EnvSYS.KEY_KAKAO_USER_ID);
                    Object naveridObj = sess.getAttribute(EnvSYS.KEY_NAVER_USER_ID);
                    Object clovaidObj = sess.getAttribute(EnvSYS.KEY_CLOVA_USER_ID);
                    if (kakaoidObj != null) {
                        // SNS 인증으로 가입한 사용자는 이메일 인증을 생략
                        userDao.setKakaoid(seqUser, kakaoidObj.toString());
                        userDao.verifyUser(juid);
                        resObj.put("code", EnvSYS.RESCODE_SUCC);
                        resObj.put("msg", "계정 등록을 완료하였습니다.");
                        sess.removeAttribute(EnvSYS.KEY_KAKAO_USER_ID);
                    } else if (naveridObj != null) {
                        // naver 인증으로 가입한 사용자는 이메일 인증을 생략
                        AccountNaverDataAccessObject niDao = new AccountNaverDataAccessObject();
                        if (!niDao.existNaverid(naveridObj.toString(), seqUser)) {
                            niDao.addNaverid(naveridObj.toString(), seqUser);
                        }
                        if (clovaidObj != null) {
                            AccountClovaDataAccessObject acDao = new AccountClovaDataAccessObject();
                            if (!acDao.existClovaid(clovaidObj.toString(), seqUser)) {
                                acDao.addClovaid(clovaidObj.toString(), seqUser);
                            }
                            sess.removeAttribute(EnvSYS.KEY_CLOVA_USER_ID);
                        }
                        userDao.verifyUser(juid);
                        resObj.put("code", EnvSYS.RESCODE_SUCC);
                        resObj.put("msg", "계정 등록을 완료하였습니다.");
                        sess.removeAttribute(EnvSYS.KEY_NAVER_USER_ID);
                    } else {
                        // 7일간 유효한 인증 확인 메일 전송
                        String verifyCode = VerifyCodeGenerator.generateCode(0);
                        String expiryDate = null;
                        try {
                            expiryDate = JinieboxUtil.getNextdayString(JinieboxUtil.getTodayString(), 7);
                        } catch (ParseException e) {
                            log.error(ExceptionUtil.getExceptionInfo(e));
                        }
                        userDao.setVerifyCode(juid, verifyCode, expiryDate);

                        String serverName = req.getServerName();
                        int serverPort = req.getServerPort();
                        String contextPath = req.getContextPath();
                        String requestUrl = req.getRequestURL().toString();
                        String requestProtocol = requestUrl.substring(0, requestUrl.indexOf("://") + 3);
//                        String location = requestProtocol + serverName + ":" + serverPort + contextPath + "/verify";
                        String location = PropertiesUtil.get("SERVICE_URL") + "/verify";
                        location += "?cd=" + verifyCode + juid;
                        this.sendVerifyEmail(juid, location);

                        resObj.put("code", EnvSYS.RESCODE_SUCC);
                        resObj.put("msg", "회원 가입 신청이 등록되었습니다.\n" + juid + "에 인증메일이 전송되었습니다.\n'"
                                + JinieboxUtil.addDatedot(expiryDate) + "'까지 이메일 인증을 완료하셔야 계정 사용이 가능합니다."); // message
                        log.debug("사용자 인증 메일 전송 완료 - " + juid);
                    }
                    
                    bSuccess = true;
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtil.getExceptionInfo(e));
			resObj.put("code", EnvSYS.RESCODE_FAIL);
			resObj.put("msg", "신규 회원 등록에 실패하였습니다"); // message
		} finally {
			if (!bSuccess) {
				//등록 실패시 롤백
				if (seqUser != null) {
					userDao.delete(seqUser);
				}
				if (seqStore != null) {
					storeDao.delete(seqStore);
					int[] seqBoxes = boxDao.getAllSeq(seqStore);
					for (int i = 0; i < seqBoxes.length; i++) {
						boxDao.delete(seqBoxes[i] + "");
					}
				}
                if (!resObj.containsKey("code")) {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                }
                if (!resObj.containsKey("msg")) {
                    resObj.put("msg", "신규 회원 등록에 실패하였습니다"); // message
                }
			}
		}

		return resObj;
	}
	
    private JSONObject info(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            try {
            	resObj.put("juid", us.getJuid());
            	resObj.put("juname", us.getJuname());
            	
            	
            	
            	
            	StoreDataAccessObject storeDao = new StoreDataAccessObject();
            	List<JSONObject> shList = storeDao.getSharedStores(us.getSeq());
            	
                /**
                 * 2024.01.06
                 * 현재는 store 공유 처리로 정상 동작하기 때문에 아래의 box 공유에 대한 처리는 일단 주석 처리한다 
                 */
//                StoreDataAccessObject stdao = new StoreDataAccessObject();
//                BoxDataAccessObject bdao = new BoxDataAccessObject();
//                
//                JSONObject sharedInfo = null;
//                String seqStore, seqBox = null;
//                Iterator<JSONObject> shIter = shlist.iterator();
//				while (shIter.hasNext()) {
//					sharedInfo = (JSONObject) shIter.next();
//					seqStore = sharedInfo.get("seq_store").toString();
//					seqBox = sharedInfo.get("seq_box").toString();
//
//					if (seqStore == null || Integer.parseInt(seqStore) < 0) {
//						seqStore = bdao.getBox(seqBox).get("seq_store").toString();
//					}
//					if (Integer.parseInt(seqStore) > 0) {
//						sharedInfo.put("name_store", stdao.getStore(seqStore).get("name"));
//					}
//					if (Integer.parseInt(seqBox) > 0) {
//						sharedInfo.put("name_box", bdao.getBox(seqBox).get("name"));
//					}
//				}
                
                resObj.put("sharedlist", shList);
                
                
                
                
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
            
            resObj.put("code", EnvSYS.RESCODE_SUCC);
        }
        
        return resObj;
    }
    
	private JSONObject setDefaultStore(HttpServletRequest req) throws Exception {

		JSONObject resObj = new JSONObject();

		String seqStore = req.getParameter("seq_store");

		UserSession us = AuthManager.getInstance().getUserSession(req);
		if (us != null) {
			String seqUser = us.getSeq();
			UserDataAccessObject userDao = new UserDataAccessObject();
			userDao.setDefaultStoreSeq(seqUser, seqStore);
			StoreDataAccessObject storeDao = new StoreDataAccessObject();
			JSONObject storeJson = storeDao.getStore(seqStore);
			us.setDefStoreInfo(storeJson);
			resObj.put("code", EnvSYS.RESCODE_SUCC);
			resObj.put("msg", "저장에 성공하였습니다");
		}

		return resObj;
	}
    
    private JSONObject predictiveId(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        
        String inputTxt = req.getParameter("input_txt");
        
        UserDataAccessObject udao = new UserDataAccessObject();
        try {

            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us != null) {
                String juid = us.getJuid();
                resObj = udao.predictiveJuids(inputTxt, juid);
            }
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
        
        return resObj;
    }
    
    /**
     * 인증 확인 메일 전송
     * @param juid
     * @param verifyUrl
     * @throws Exception
     */
    private void sendVerifyEmail(String juid, String verifyUrl) throws Exception {
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");        // gmail은 true 고정
        p.put("mail.smtp.host", "smtp.naver.com");         // smtp 서버 주소
        p.put("mail.smtp.auth","true");                    // gmail은 true 고정
        p.put("mail.smtp.port", "587");                    // 네이버 포트
           
        Authenticator auth = new MyAuthentication();
        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);
         
        try{
            // 편지보낸시간
            msg.setSentDate(new Date());
            InternetAddress from = new InternetAddress() ;
            // 발신자 아이디
            from = new InternetAddress("jiniebox@naver.com");
            // 이메일 발신자
            msg.setFrom(from);
            // 이메일 수신자
            InternetAddress to = new InternetAddress(juid);
            msg.setRecipient(Message.RecipientType.TO, to);
            // 이메일 제목
            msg.setSubject("(JINIEBOX) 지니박스 계정 이메일 인증하기", "UTF-8");
            // 이메일 내용
			msg.setText(String.join(System.getProperty("line.separator"),
					"<p>안녕하세요 JINIEBOX 입니다.</p>\n" 
							+ "<p>계정 인증 버튼을 클릭하시면, 지니박스 계정 인증이 완료됩니다.</p>\n"
							+ "<p>7일 안에 계정을 인증하지 않으면 계정 사용 신청이 만료되어 요청하신 등록 정보가 삭제됩니다.</p>\n"
							+ "<p><a href=\""+verifyUrl+"\" style=\"display:block; margin: 0 auto; width: 280px; height: 44px; line-height: 28px; border-radius: 22px; padding: 8px 16px 7px 16px; vertical-align: middle; background-color: #0072de; color: #fff; box-sizing: border-box; text-align: center; text-decoration: none; font-family: Arial, Helvetica, 'sans-serif'; font-weight: normal;  word-wrap: break-word; word-break: break-word;\" target=\"_blank\">계정 인증<!--&ndash;&ndash;Verify account&ndash;&ndash;--> </a></p>\n"
							+ "<p>&nbsp;</p>\n" + "<p>위 버튼이 제대로 작동하지 않을 때는, 브라우저에서 아래 주소로 접속하세요.</p>\n"
							+ "<p style=\"margin: 0 0 48px 0; padding: 0; font-size: 13px; line-height: 22px; color: #909090; font-family: Arial, Helvetica, 'sans-serif'; font-weight: normal;  word-wrap: break-word; word-break: break-word; text-align: center;\">"+verifyUrl+"</p>\n"
							+ "<p style=\"margin: 0 0 24px 0; padding: 0; color: #252525; font-family: Arial, Helvetica, 'sans-serif'; font-weight: normal;  word-wrap: break-word; word-break: break-word; font-size: 12px; line-height: 16px; color: #909090;\"><!--&ndash;&ndash;Note : Do not reply to this email. Contact us with any queries by visiting our website at:&ndash;&ndash;-->참고 : 이 메일은 발신 전용이므로 회신하실 수 없습니다.<br />\r\n"
							+ "-&nbsp;<a href=\"https://jiniebox.com\" style=\"color: #0072de; text-decoration: underline; font-size: 12px; font-family: Arial, Helvetica, 'sans-serif'; font-weight: normal;  word-wrap: break-word; word-break: break-word;\" target=\"_blank\">지니박스 바로가기</a><br />\r\n"
							+ "- 문의 메일 : jinieboxes@gmail.com</p>\r\n"
							+ "<div style=\"margin: 0; padding: 0; font-size:10px; opacity:0.6; font-weight: 300; line-height: 12px; color: #252525; font-family: Arial, Helvetica, 'sans-serif'; font-weight: normal;  word-wrap: break-word; word-break: break-word;\" title=\"copyright\">Copyright &copy; 2023 JINIEBOX. All Rights Reserved.</div>"),
					"UTF-8");
			// 이메일 헤더
			msg.setHeader("content-Type", "text/html");
			// 메일보내기
			javax.mail.Transport.send(msg, msg.getAllRecipients());
             
        }catch (AddressException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }catch (MessagingException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
        
		log.debug("'" + juid + "' 사용자에게 인증 확인 메일 전송완료");
    }
    
	/**
	 * 이메일 전송을 위해 임시로 생성한 inner class
	 * @author KIUNSEA
	 *
	 */
	class MyAuthentication extends Authenticator {

		PasswordAuthentication pa;

		public MyAuthentication() {

			//TODO 보안 처리 필요(소스코드상에 비밀번호가 노출되고 있음)
			String id = "jiniebox@naver.com"; // 네이버 이메일 아이디
			String pw = "Iljs2002##"; // 네이버 비밀번호

			// ID와 비밀번호를 입력한다.
			pa = new PasswordAuthentication(id, pw);

		}

		// 시스템에서 사용하는 인증정보
		public PasswordAuthentication getPasswordAuthentication() {
			return pa;
		}
	}
}
