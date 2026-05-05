package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.dao.GcpTokenDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.GcpService;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Google Cloud Platform 용 서블릿 Google Authentication 절차에서 code 발급 요청 이후
 * redirect_uri 로 전달받는 code 로 token 을 갱신한다.
 * 
 * @author KIUNSEA
 *
 */
@WebServlet("/gcp")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class GcpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(GcpServlet.class);

    public void init() {
        ;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
//        printOut(req, res);

        if (!IntegrationGate.isGoogleOAuthEnabled()) {
            IntegrationGate.writeDisabled(res, "Google");
            return;
        }

        JSONObject resJson = null;
        JSONObjectExt reqJson = null;
        try {
            reqJson = JinieboxUtil.parseRequest(req);
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        String cmdHttp = reqJson.getString("cmd");
        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                UserSession us = AuthManager.getInstance().getUserSession(req);
                if (cmdHttp != null) {
                    if ("getUriForAuth".equals(cmdHttp)) {
                        resJson = getUriForAuth(us, reqJson, res);
                    } else if ("releaseGmail".equals(cmdHttp)) {
                        resJson = releaseGmail(req, res);
                    } else if ("TEMP".equals(cmdHttp)) {
//                        resJson = this.add(req);
                    }
                } else {
                    // 2024.04.17 notice : GcpServlet 을 호출하는 것만으로 refresh token 을 갱신하게 되지만 지니박스 서비스 계정 인증이 선행되어야 하므로 현재 프로세스를 유지한다.
                    this.updateRefreshToken(req, res);
                }
            } else {
                resJson = new JSONObject();
                AuthInfo uai = AuthManager.getInstance().getUserAuthInfo(req);
                if (uai != null) {
                    resJson.put("code", uai.getValidcode());
                    resJson.put("msg", uai.getValidmsg());
                } else {
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        if (resJson == null)
            resJson = new JSONObject();
        /**
         * (Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
         */
        resJson.put("buid", req.getParameter("buid"));
        resJson.put("authkey", AuthManager.getInstance().getRandomAuthKey(req.getParameter("buid")));
//        log.debug("res - " + resJson);

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resJson);
        out.flush();
        out.close();

    }
    
    private JSONObject getUriForAuth(UserSession us, JSONObjectExt reqJson, HttpServletResponse res) throws Exception {
        JSONObject resJson = new JSONObject();
        
        int storageType = reqJson.getInt("storage_type");
        if (storageType == 1) {
            String gmail = reqJson.getString("gmail");
            String seqUser = us.getSeq();

            StoreDataAccessObject sDao = new StoreDataAccessObject();
            sDao.setStorageType(seqUser, storageType);
            sDao.setImguse(seqUser, sDao.IMG_USE_TRUE);
            
            UserDataAccessObject uDao = new UserDataAccessObject();
            uDao.setGoogleid(seqUser, gmail);
            
            us.getDefStoreInfo().put("storage_type", storageType); //세션에 반영
            us.getDefStoreInfo().put("img_use", sDao.IMG_USE_TRUE); //세션에 반영
        } else {
            return resJson;
        }
        
        String gauthUri = "https://accounts.google.com/o/oauth2/v2/auth" + "?client_id=" + SafeProps.getString("GOOGLE_CLIENT_ID")
                + "&redirect_uri=" + SafeProps.getString("GOOGLE_REDIRECT_URI")
                + "&response_type=code"
                + "&scope=https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/gmail.readonly"
                + "&access_type=offline" 
                + "&prompt=consent";
        resJson.put("gauthUri", gauthUri);
        resJson.put("code", EnvSYS.RESCODE_SUCC);
        return resJson;
    }
    
    private JSONObject releaseGmail(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject resJson = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        StoreDataAccessObject sDao = new StoreDataAccessObject();
        //db에 반영
        sDao.setImguse(seqUser, sDao.IMG_USE_FALSE);
        sDao.setStorageType(seqUser, sDao.STORAGE_TYPE_LOCAL);
        //세션에 반영
        us.getDefStoreInfo().put("img_use", sDao.IMG_USE_FALSE);
        us.getDefStoreInfo().put("storage_type", sDao.STORAGE_TYPE_LOCAL);
        
        GcpTokenDataAccessObject gcpTokenDao = new GcpTokenDataAccessObject();
        gcpTokenDao.deleteToken(seqUser);
        
        resJson.put("code", EnvSYS.RESCODE_SUCC);
        resJson.put("msg", "구글 연결이 해제되었습니다.");
        return resJson;
    }

    /**
     * 사용자의 구글 계정과 지니박스 계정을 연결하기 위해 access, refresh token 을 발급받고 저장한다.
     * 
     * @param req
     * @param res
     * @return
     */
    private void updateRefreshToken(HttpServletRequest req, HttpServletResponse res) {

        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            String seqUser = us.getSeq();
            
            GcpService gcpSvc = new GcpService();
            log.debug("구글에서 전달받은 code -> " + req.getParameter("code"));
            JSONObjectExt resJson = gcpSvc.restoreTokensWithCode(seqUser, req.getParameter("code"));
            
            String resResult = resJson.getString("result");
            String resGmail = resJson.getString("gmail");
            String resMsg = resJson.getString("msg");
            log.debug("db 저장 결과 -> " + resMsg);
            
            int storageType = -1;
            StoreDataAccessObject sDao = new StoreDataAccessObject();
            if (resResult.equals(EnvSYS.RESCODE_SUCC)) {
                storageType = sDao.STORAGE_TYPE_GOOGLE;
            } else {
                storageType = sDao.STORAGE_TYPE_LOCAL;
            }
            sDao.setStorageType(seqUser, storageType);
            sDao.setImguse(seqUser, sDao.IMG_USE_TRUE);
            
            UserDataAccessObject uDao = new UserDataAccessObject();
            uDao.setGoogleid(seqUser, resGmail);
            
            //세션에 반영
            us.getDefStoreInfo().put("storage_type", storageType);
            us.getDefStoreInfo().put("img_use", sDao.IMG_USE_TRUE);

            req.setAttribute("title", "Google authentication results");
            req.setAttribute("code", resResult);
            req.setAttribute("msg", resMsg);
            req.setAttribute("gmail", resGmail);
            getServletContext().getRequestDispatcher("/confirm_close.jsp").forward(req, res);

        } catch (ServletException | IOException | ParseException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
    }

    
    /**
     * 전달받은 파라미터들을(code 와 scope) 출력
     * 
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    private void printOut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        // request parameter 로깅
        String pName = null;
        String pValue = null;
        Enumeration<String> enums = req.getParameterNames();
        while (enums.hasMoreElements()) {
            pName = enums.nextElement().toString();
            pValue = req.getParameter(pName);
            out.println("<p>(param) " + pName + " - " + pValue + "</p>");
//            System.out.println("<p>(param) " + pName + " - " + pValue + "</p>");
        }
        out.close();
    }

}
