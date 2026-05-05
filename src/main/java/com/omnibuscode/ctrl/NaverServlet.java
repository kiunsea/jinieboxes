package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.auth.AuthNaverInfo;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.dao.AccountClovaDataAccessObject;
import com.omnibuscode.dao.AccountNaverDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.HttpURLConnectionUtil;
import com.omnibuscode.utils.JSONUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 지니박스와 네이버를 연동하기 위한 클래스이다
 * @author KIUNSEA
 */
@WebServlet("/naver")
public class NaverServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(NaverServlet.class);

    public void init() {
        PropertiesUtil.USER_PROPERTIES_PATH = this.getServletContext().getRealPath("/")
                + "WEB-INF/classes/res/JINIEBOX.PROPERTIES";
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // request parameter 로깅
        String pName = null;
        Enumeration<String> enums = req.getParameterNames();
        while (enums.hasMoreElements()) {
            pName = enums.nextElement().toString();
            log.debug("(param) " + pName + " - " + req.getParameter(pName));
        }

        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        try {

            if (!IntegrationGate.isNaverOAuthEnabled()) {
                IntegrationGate.writeDisabled(res, "Naver");
                return;
            }

            String naverCode = req.getParameter("code");
            String clovaId = req.getParameter("state"); //clova id
            
            // check naver state : state 값이 없으면 네이버에서 "null" 문자열을 반환
            if (clovaId != null && "null".equals(clovaId.toLowerCase())) {
                clovaId = null;
            }
            
            if (naverCode != null) {
                //네이버 계정의 회원가입 또는 계정 연동을 위한 준비
                String token = this.getAccessToken(naverCode, clovaId);
                JSONObject usrJson = this.getUserInfo(token);
                if (usrJson != null) {
                    String naverId = usrJson.get("id").toString();
                    UserDataAccessObject userDao = new UserDataAccessObject();
                    AccountNaverDataAccessObject niDao = new AccountNaverDataAccessObject();
                    if (niDao.existNaverid(naverId) && clovaId == null) {
                        //이미 인증된 사용자인 경우 세션 생성하여 로그인
                        JSONObject userJson = niDao.getUserByNaverid(naverId);
                        AuthInfo authUserInfo = new AuthNaverInfo();
                        authUserInfo.setValidcode(EnvSYS.RESCODE_SUCC);
                        authUserInfo.setValidmsg("인증에 성공하였습니다");
                        AuthManager.getInstance().createUserSession(req, authUserInfo, userJson);
                        userDao.setVisited(userJson.get("seq").toString());
                        getServletContext().getRequestDispatcher("/list_box.jsp").forward(req, res);
                    } else {
                        //계정 생성 또는 연동을 위해 회원가입 폼으로 이동 (naverState != null : 클로바 연동인 경우)
                        HttpSession sess = req.getSession(true);
                        sess.setAttribute(EnvSYS.KEY_CLOVA_USER_ID, clovaId); // 세션에 저장
                        sess.setAttribute(EnvSYS.KEY_NAVER_USER_ID, naverId); // 세션에 저장
                        req.setAttribute("naver_user", "true");
                        if (clovaId != null) {
                            req.setAttribute("clova_user", "true");
                        }
                        if (usrJson.containsKey("email")) {
                            String userEmail = usrJson.get("email").toString();
                            req.setAttribute("user_email", userEmail);
                            if (userDao.existJuid(userEmail)) {
                                req.setAttribute("exist_juid", "true");
                            }
                        }
                        getServletContext().getRequestDispatcher("/signup.jsp").forward(req, res);
                    }
                } else {
                    // 다시 로그인 화면으로
                    res.sendRedirect("/jbs/signin.jsp");
                }
                
            } else {
                //네이버 계정의 회원가입 또는 계정 연결 처리
                JSONObject resObj = new JSONObject();
                
                String cmdHttp = req.getParameter("cmd");
                if (cmdHttp != null) {
                    if ("update_account".equals(cmdHttp)) {
                        String juid = req.getParameter("juid");
                        String jupw = req.getParameter("jupw");
                        if (juid != null && jupw != null) {
                            UserDataAccessObject uDao = new UserDataAccessObject();
                            JSONObject userJson = uDao.getUserByJuid(juid);
                            if (userJson != null) {
                                if (jupw.equals(userJson.get("jupw").toString())) {
                                    String seqUser = userJson.get("seq").toString();
                                    HttpSession sess = req.getSession(true);
                                    Object naveridObj = sess.getAttribute(EnvSYS.KEY_NAVER_USER_ID);
                                    if (naveridObj != null) {
                                        AccountNaverDataAccessObject niDao = new AccountNaverDataAccessObject();
                                        if (!niDao.existNaverid(naveridObj.toString(), seqUser)) {
                                            niDao.addNaverid(naveridObj.toString(), seqUser);
                                            resObj.put("code", EnvSYS.RESCODE_SUCC);
                                            resObj.put("msg", "계정 연결에 성공하였습니다");
                                        }
                                        Object clovaidObj = sess.getAttribute(EnvSYS.KEY_CLOVA_USER_ID);
                                        if (clovaidObj != null) {
                                            AccountClovaDataAccessObject acDao = new AccountClovaDataAccessObject();
                                            if (!acDao.existClovaid(clovaidObj.toString(), seqUser)) {
                                                acDao.addClovaid(clovaidObj.toString(), seqUser);
                                                resObj.put("code", EnvSYS.RESCODE_CLOSE);
                                                resObj.put("msg", "계정 연결에 성공하였습니다\n현재 창을 닫고 클로바에서 다시 호출해 주세요");
                                            }
                                            sess.removeAttribute(EnvSYS.KEY_CLOVA_USER_ID);
                                        }
                                        sess.removeAttribute(EnvSYS.KEY_NAVER_USER_ID);
                                    } else {
                                        resObj.put("code", EnvSYS.RESCODE_FAIL);
                                        resObj.put("msg", "naver 정보가 없습니다");
                                    }
                                } else {
                                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                                    resObj.put("msg", "비밀번호를 올바르게 입력해주세요");
                                }
                            } else {
                                resObj.put("code", EnvSYS.RESCODE_FAIL);
                                resObj.put("msg", "사용자 정보가 없습니다");
                            }
                        } else {
                            resObj.put("code", EnvSYS.RESCODE_FAIL);
                            resObj.put("msg", "아이디와 비밀번호를 올바르게 입력해주세요");
                        }
                    } else {
                        resObj.put("code", EnvSYS.RESCODE_FAIL);
                        resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                    }
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                }

                log.debug("res - " + resObj);

                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json;charset=UTF-8");
                res.setHeader("Cache-Control", "no-cache");
                PrintWriter out = res.getWriter();
                out.println(resObj);
                out.flush();
                out.close();
            }

        } catch (ParseException | IOException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
    }

    private String getAccessToken(String code, String naverState) throws IOException, ParseException {
        log.debug("code : " + code);

        String access_Token = null;
        String refresh_Token = null;
        String reqURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";

        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", SafeProps.getString("NAVER_CLIENT_ID"));
        params.put("client_secret", SafeProps.getString("NAVER_CLIENT_SECRET"));
        params.put("redirect_uri", SafeProps.getString("NAVER_REDIRECT_URI"));
        params.put("code", code);
        if (naverState != null) params.put("state", naverState);

        String resStr = HttpURLConnectionUtil.requestPostData(reqURL, null, params);

        if (resStr != null) {
            JsonNode jn = JSONUtil.parseJsonNode(resStr);

            if (jn.get("access_token") != null) {
                access_Token = jn.get("access_token").asText();
                refresh_Token = jn.get("refresh_token").asText();

                log.debug("access_token : " + access_Token);
                log.debug("refresh_token : " + refresh_Token);
            }
        }
        
        if (access_Token == null) {
            log.debug("access_token is null~");
        }

        return access_Token;
    }

    private JSONObject getUserInfo(String access_token) throws ParseException, IOException {

        String reqURL = "https://openapi.naver.com/v1/nid/me";

        Map<String, String> props = new HashMap<String, String>();
        props.put("Authorization", "Bearer " + access_token);

        String resStr = HttpURLConnectionUtil.requestPostData(reqURL, props, null);

        log.debug("response body : " + resStr);

        if (resStr != null) {
            JsonNode root = JSONUtil.parseJsonNode(resStr);

            String id = root.get("response").get("id").asText();
            String email = root.get("response").get("email").asText();
            log.debug("naver id : " + id);
            log.debug("naver email : " + email);

            JSONObject usrJson = new JSONObject();
            usrJson.put("id", id);
            if (email != null) {
                usrJson.put("email", email);
            }
            return usrJson;
        } else {
            return null;
        }
    }

}
