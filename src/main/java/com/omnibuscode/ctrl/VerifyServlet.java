package com.omnibuscode.ctrl;



import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.util.VerifyCodeGenerator;
import com.omnibuscode.utils.ExceptionUtil;


/**
 * 이메일 인증 처리용 클래스
 * @author KIUNSEA
 */
@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(VerifyServlet.class);

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
    	
    	int cdLength = VerifyCodeGenerator.INVITECODE_LENGTH;
        String code = req.getParameter("cd");
        String reqCode = code.substring(0, cdLength);
        String reqJuid = code.substring(cdLength);
        UserDataAccessObject uDao = new UserDataAccessObject();
        try {
			String juid = uDao.getJuidByVerifycode(reqCode);
			if (juid == null || !juid.equals(reqJuid)) {
			    resObj.put("code", EnvSYS.RESCODE_FAIL);
                resObj.put("msg", EnvSYS.RESMSG_INVREQ);
			} else {
				uDao.verifyUser(juid);
				getServletContext().getRequestDispatcher("/welcome.html").forward(req, res);
			}
		} catch (Exception e) {
			log.error(ExceptionUtil.getExceptionInfo(e));
		}
    }
}
