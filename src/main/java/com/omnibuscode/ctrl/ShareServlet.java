package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

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
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.logic.ShareService;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/share")
public class ShareServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogUtil.getLog(ShareServlet.class);
    private Logger log = LogManager.getLogger(ShareServlet.class);
    
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

        JSONObject resObj = null;
        
        String cmdHttp = req.getParameter("cmd");

        try {
        	if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    if ("list".equals(cmdHttp)) {
                        resObj = this.list(req); //자신에게 공유된 스토어 또는 박스 조회
                    } else if ("add".equals(cmdHttp)) {
                        resObj = this.add(req); //공유 등록 처리
                    }
                }else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            }  else {
            	resObj = new JSONObject();
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
     * 자신에게 공유된 스토어 또는 박스 조회
     * 
     * @param req
     * @return
     */
    private JSONObject list(HttpServletRequest req) {

        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            String seqUser = us.getSeq();
            try {
                ShareDataAccessObject sdao = new ShareDataAccessObject();
                List<JSONObject> list = sdao.list(seqUser);
                resObj.put("list", list);
            } catch (Exception e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            }
        }

        return resObj;
    }
    
	private JSONObject add(HttpServletRequest req) throws Exception {

	    JSONObject resObj = new JSONObject();
	    
//		String juid = req.getParameter("juid");
	    UserSession us = AuthManager.getInstance().getUserSession(req);
		String icode = req.getParameter("icode");
        if (us != null && icode != null) {

//            UserDataAccessObject udao = new UserDataAccessObject();
//            String seqUser = udao.getSeqByJuid(juid);
            String seqUser = us.getSeq();

            if (seqUser != null) {
                InviteDataAccessObject invDao = new InviteDataAccessObject();
                JSONObject invObj = invDao.getInvite(icode);
                if (invObj != null) {
                    Object invSeqUserObj = invObj.get("seq_user");
                    if (invSeqUserObj != null && invSeqUserObj.toString().equals(seqUser)) {
//                        ShareDataAccessObject sdao = new ShareDataAccessObject();
                        String authority = invObj.get("authority").toString();

                        String typeObj = invObj.get("type_object").toString();
                        String seqObj = invObj.get("seq_object").toString();

                        ShareService shareSvc = new ShareService();
                        resObj = shareSvc.addShare(seqUser, typeObj, seqObj, authority);
                        if (EnvSYS.RESCODE_SUCC.equals(resObj.get("code").toString())) {
                            if (invDao.delete(invObj.get("seq").toString())) {
                                resObj.put("code", EnvSYS.RESCODE_SUCC);
                                resObj.put("msg", "공유 정보가 저장되었습니다");
                            }
                        }
                    } else {
                        resObj.put("code", EnvSYS.RESCODE_FAIL);
                        resObj.put("msg", "초대코드로 사용자 정보를 찾지 못하였습니다-" + icode);
                    }
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", "초대코드가 조회되지 않습니다");
                }
            } else {
                resObj.put("code", EnvSYS.RESCODE_FAIL);
                resObj.put("msg", "사용자 정보를 찾지 못하였습니다");
            }
        } else {

            log.debug("사용자 세션 또는 icode 값이 없음-" + icode);
        }
		return resObj;
	}
    
}
