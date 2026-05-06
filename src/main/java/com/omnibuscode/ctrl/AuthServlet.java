package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.NanumWebService;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * KEYCODE TABLE 을 이용하여 사용자를 인증한다.<br/>
 * 현재는 JINIEBOX WEB 과 BIXBY 에 적용되어 있다.
 * @author KIUNSEA
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(AuthServlet.class);

    public void init() {
        /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
    }

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
        
        String cmdHttp = req.getParameter("cmd");
        String buid = req.getParameter("buid");
        
        if (cmdHttp != null) {
            
            res.setCharacterEncoding("UTF-8");
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Cache-Control", "no-cache");
            PrintWriter out = res.getWriter();
            JSONObject resJson = new JSONObject();

            if ("reqAuthkey".equals(cmdHttp)) {
                
                String authkey = AuthManager.getInstance().getRandomAuthKey(buid);
                resJson.put("code", EnvSYS.RESCODE_SUCC); // response code
                resJson.put("authkey", authkey);
                
            } else if ("validUser".equals(cmdHttp)) {
                /**
                 * 사용자 검증
                 *  CODE 000 : 등록된 사용자
                 *  CODE 012 : 등록되지 않은 사용자
                 *  CODE 013 : 인증 실패
                 */
				try {
					if (AuthManager.getInstance().hasUserAuthed(req)) {
					    
					    //회원으로 확인된 경우
						resJson.put("code", EnvSYS.RESCODE_SUCC);
                        resJson.put("msg", "다음의 링크에서 사용자 정보를 확인하세요");
                        resJson.put("label", "사용자 정보");
						resJson.put("url", PropertiesUtil.get("SERVICE_URL") + "/profile.jsp");
						
						UserSession us = AuthManager.getInstance().getUserSession(req);
						resJson.put("is_partner", us.isPartner());
						resJson.put("sequ", us.getSeq());
						resJson.put("juid", us.getJuid());
						resJson.put("juname", us.getJuname());
						resJson.put("store_title", us.getDefStoreInfo().get("name"));
						
						//나눔정보 체크
						String seqNanum = req.getParameter("seq_nanum");
						String shareCode = req.getParameter("scd");
						if (seqNanum != null && shareCode != null) {
						    NanumWebService nanumSvc = new NanumWebService();
						    nanumSvc.addShareNanum(us.getSeq(), shareCode, null); //나눔 공유 등록
						}
			
			            if (us.hasInstantMessage()) {
			                resJson.put("instmsg", us.getInstantMessage());
			            }
						
					} else {
					    
					    //미확인 유저인 경우
						AuthInfo uAuthInfo = AuthManager.getInstance().getUserAuthInfo(req);
						
						String uvCode = null;
						if (uAuthInfo != null) {
							uvCode = uAuthInfo.getValidcode();
							resJson.put("code", uvCode);
							if (EnvSYS.RESGUEST_USER.equals(uvCode)) {
								resJson.put("msg", "회원가입이 필요합니다");
								resJson.put("label", "회원가입");
								resJson.put("url", PropertiesUtil.get("SERVICE_URL") + "/signup.jsp");
							} else if (EnvSYS.RESAUTH_FAIL.equals(uvCode)) {
								resJson.put("msg", uAuthInfo.getValidmsg());
							} else if (EnvSYS.RESNOTYET_VERIFY.equals(uvCode)) {
								resJson.put("msg", uAuthInfo.getValidmsg());
							} else {
								resJson.put("msg", "사용자 인증에 실패하였습니다");
							}
						} else {
							resJson.put("code", EnvSYS.RESCODE_FAIL);
							resJson.put("msg", EnvSYS.RESMSG_USERSESSION_FAIL);
						}
					}
				} catch (Exception e) {
					log.error(ExceptionUtil.getExceptionInfo(e));
				}
            } else if ("signout".equals(cmdHttp)) {
            	/**
            	 * 사용자 세션 제거
            	 */
            	AuthManager.getInstance().removeUserAuthInfo(req);
            	AuthManager.getInstance().removeUserSession(req);
            	res.sendRedirect("signin.jsp");
            }

            /**
             * (Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
             */
            resJson.put("buid", req.getParameter("buid"));
            resJson.put("authkey", AuthManager.getInstance().getRandomAuthKey(req.getParameter("buid")));
//            log.debug("res - " + resJson);
            
            out.println(resJson);
            out.flush();
            out.close();
        } else {
            
        	//TODO 현재 html에서 cmd가 없이도 요청이 발생하기 때문에 무조건 페이지를 이동하게 하는 동작은 다수의 오류가 발생하게 된다
//        	res.sendRedirect("/jiniebox/box_list.html");
//        	req.getRequestDispatcher("/jiniebox/box_list.html").forward(req, res);
            
        }
    }

}
