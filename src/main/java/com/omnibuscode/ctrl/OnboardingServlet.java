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

import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.FileUtil;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/onboarding")
public class OnboardingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(OnboardingServlet.class);

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

        String resContent = null;
        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                resContent = this.getScenarioJs(req, res);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/javascript;charset=UTF-8"); // MIME 타입을 JavaScript로 설정
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resContent);
        out.flush();
        out.close();
    }

    private String getScenarioJs(HttpServletRequest req, HttpServletResponse res) throws Exception {

        UserSession us = AuthManager.getInstance().getUserSession(req);
        
		// 서버에 있는 JS 파일의 경로
		String scenarioSys = req.getParameter("sys_scenario");
		String scenarioUsr = req.getParameter("usr_scenario");

		String jsFilePath = null;
		if (scenarioSys != null) {
			jsFilePath = EnvSYS.SYS_RES_PATH + "onboarding/" + scenarioSys + (us.isPartner() ? "_beta" : "") + ".js";
		} else if (scenarioUsr != null) {
			jsFilePath = EnvSYS.SYS_RES_PATH + scenarioUsr;
		}
        
        // 파일을 읽고 내용을 반환
        if (FileUtil.exists(jsFilePath)) {
            return FileUtil.readFile(jsFilePath, null).toString();
        } else {
            log.debug("[" + jsFilePath + "] 파일이 없습니다.");
        }

        return null;

    }

}
