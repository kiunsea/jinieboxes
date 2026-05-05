package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
import com.omnibuscode.dao.InviteDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 사용자간 초대 요청을 처리하는 클래스
 * @author KIUNSEA
 */
@WebServlet("/invite")
public class InviteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogUtil.getLog(InviteServlet.class);
    private Logger log = LogManager.getLogger(InviteServlet.class);
    
    private final String CMD_KEY = "grXMDW"; //초대링크의 CMD 변환용
    private final String INVITE_KEY = "NxGjGY";

    public void init() {
        PropertiesUtil.USER_PROPERTIES_PATH = this.getServletContext().getRealPath("/")
                + "WEB-INF/classes/res/JINIEBOX.PROPERTIES";
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

        JSONObject resObj = null;
        
        String cmdHttp = req.getParameter("cmd");
        String secretCmd = req.getParameter(this.CMD_KEY);

        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    if ("list".equals(cmdHttp)) { // 자신이 생성한 전체 초대장 조회
                        resObj = this.list(req);
                    } else if ("generate".equals(cmdHttp)) {
                        resObj = this.generate(req); // 초대코드, 초대주소 생성
                    } else if ("save".equals(cmdHttp)) {
                        resObj = this.save(req); // 초대정보 저장 (생성 또는 수정)
                    }
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            } else if (secretCmd != null) {
                // TODO 초대 URL 을 처리 (#203)
            } else {
                resObj = new JSONObject();
                AuthInfo ai = AuthManager.getInstance().getUserAuthInfo(req);
                if (ai != null) {
                    resObj.put("code", ai.getValidcode());
                    resObj.put("msg", ai.getValidmsg());
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_FAIL);
                }
            }
        } catch (Exception e) {
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
     * 저장된 초대정보 목록 반환
     * 
     * @param req
     * @return
     */
    private JSONObject list(HttpServletRequest req) {

        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            String seqOwner = us.getSeq(); // 초대장 생성자 (저장소 소유자)
            try {
                InviteDataAccessObject idao = new InviteDataAccessObject();
                JSONObject invites = idao.getInvitesJson(seqOwner, null, null);
                resObj.put("list", invites);
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
        }

        return resObj;
    }
    
    /**
     * 초대코드, 초대주소 생성
     * 
     * @param req
     * @return
     */
    private JSONObject generate(HttpServletRequest req) {
        
        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            InviteDataAccessObject idao = new InviteDataAccessObject();
            String inviteCode = JinieboxUtil.generateRandomNumber(7);
            try {
                while (idao.existCode(inviteCode)) {
                    inviteCode = JinieboxUtil.generateRandomNumber(7);
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
            
            String urlCode = JinieboxUtil.generateRandomNumber(7);
            try {
                while (idao.existCode(urlCode)) {
                    urlCode = JinieboxUtil.generateRandomNumber(7);
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
            String inviteUrl = this.generateUrl(urlCode);
            String expiryDate = null;
            try {
                expiryDate = JinieboxUtil.getNextdayString(JinieboxUtil.getTodayString(), 10);
            } catch (ParseException e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }

            resObj.put("code", inviteCode);
            resObj.put("url", inviteUrl);
            resObj.put("expire", expiryDate);
        }
        return resObj;
    }
    
    /**
     * 초대정보 저장 (생성 또는 수정)
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject save(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            String seqOwner = us.getSeq(); // 초대장 생성자 (저장소 소유자)
            String seqUser = "0"; // 공유 받을 사람
            try {
                UserDataAccessObject udao = new UserDataAccessObject();
                seqUser = udao.getSeqByJuid(req.getParameter("juid"));
                if (seqUser == null) seqUser = "0"; 
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
            String seqStore = req.getParameter("seq_store");        // 공유할 저장소
            String seqBox = req.getParameter("seq_box");            // 공유할 보관함
            String seqNanum = req.getParameter("seq_nanum");  // 공유할 보관함
            String juid = req.getParameter("juid");                     // 공유 받을 사람
            String authority = req.getParameter("authority");       // 부여할 자격 (Read, Modify)
            String inviteCode = req.getParameter("code");           // 초대code
            String inviteUrl = req.getParameter("url");                 // 초대url
            String expiryDate = req.getParameter("expire");         // 유효일자
            InviteDataAccessObject idao = new InviteDataAccessObject();

            if (seqStore != null) {
                if (!(JinieboxUtil.isEmpty(authority) 
                        || JinieboxUtil.isEmpty(inviteCode)
                        || JinieboxUtil.isEmpty(inviteUrl) 
                        || JinieboxUtil.isEmpty(expiryDate))) {
                    if (idao.existInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_STORE, seqStore)) {
                        idao.updateInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_STORE, seqStore, authority, inviteCode, inviteUrl, expiryDate);
                    } else {
                        idao.insertInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_STORE, seqStore, authority, inviteCode, inviteUrl, expiryDate);
                    }
                } else {
                    StringBuffer resMsg = new StringBuffer("정보가 부족하여 실패하였습니다");
                    resMsg.append("\n  - 공유권한 : " + authority);
                    resMsg.append("\n  - 초대코드 : " + inviteCode);
                    resMsg.append("\n  - 초대링크 : " + inviteUrl);
                    resMsg.append("\n  - 유효일자 : " + expiryDate);
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", resMsg.toString());
                    return resObj;
                }
            } else {
                //box와 nanum에 대해서 code 와 url 은 자동 생성
                JSONObject genData = this.generate(req);
                inviteCode = genData.get("code").toString();
                inviteUrl = genData.get("url").toString();
                
                if (seqNanum != null) {
                    if (idao.existInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_NANUM, seqNanum)) {
                        idao.updateInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_NANUM, seqNanum, authority, inviteCode, inviteUrl, expiryDate);
                    } else {
                        idao.insertInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_NANUM, seqNanum, authority, inviteCode, inviteUrl, expiryDate);
                    }
                } else if (seqBox != null) {
                    if (idao.existInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_BOX, seqBox)) {
                        idao.updateInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_BOX, seqBox, authority, inviteCode, inviteUrl, expiryDate);
                    } else {
                        idao.insertInvite(seqOwner, seqUser, juid, EnvSYS.CLASS_TYPE_BOX, seqBox, authority, inviteCode, inviteUrl, expiryDate);
                    }
                } else {
                    log.debug("올바르지 않은 수정 요청입니다");
                }
            }
        }

        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "저장에 성공하였습니다");
        return resObj;

    }

    /**
     * 랜덤 URL 반환
     * @param code
     * @return
     */
    private String generateUrl(String code) {

        StringBuffer urlSb = new StringBuffer(PropertiesUtil.get("SERVICE_URL") + "/invite?");
        urlSb.append(JinieboxUtil.generateRandomAlphabet(6) + "=" + JinieboxUtil.generateRandomAlphabet(10));
        urlSb.append("&" + JinieboxUtil.generateRandomAlphabet(6) + "=" + JinieboxUtil.generateRandomAlphabet(10));
        urlSb.append("&" + this.INVITE_KEY + "=" + code);
        urlSb.append("&" + JinieboxUtil.generateRandomAlphabet(6) + "=" + JinieboxUtil.generateRandomAlphabet(10));
        urlSb.append("&" + this.CMD_KEY + "=" + JinieboxUtil.generateRandomAlphabet(10));

        return urlSb.toString();
    }
    

    
}
