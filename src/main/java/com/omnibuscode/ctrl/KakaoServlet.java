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
import com.omnibuscode.auth.AuthKakaoInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.SafeProps;
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
 * 지니박스와 카카오를 연동하기 위한 클래스이다
 * @author KIUNSEA
 */
@WebServlet("/kakao")
public class KakaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(KakaoServlet.class);

    public void init() {
        /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
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

            if (!IntegrationGate.isKakaoOAuthEnabled()) {
                IntegrationGate.writeDisabled(res, "Kakao");
                return;
            }

            String kakaoCode = req.getParameter("code");
            
            if (kakaoCode != null) {
                //카카오 계정의 회원가입 또는 계정 연동을 위한 준비
                
                String token = this.getAccessToken(kakaoCode);
                
                if (token != null) {
                    JSONObject usrJson = this.getUserInfo(token);
                    if (usrJson != null) {
                        String kakaoId = usrJson.get("id").toString();
                        UserDataAccessObject userDao = new UserDataAccessObject();
                        if (userDao.existKakaoid(kakaoId)) {
                            // 이미 인증된 사용자인 경우 세션 생성하여 로그인
                            JSONObject userJson = userDao.getUserByKakaoid(kakaoId);
                            AuthInfo authUserInfo = new AuthKakaoInfo();
                            authUserInfo.setValidcode(EnvSYS.RESCODE_SUCC);
                            authUserInfo.setValidmsg("인증에 성공하였습니다");
                            AuthManager.getInstance().createUserSession(req, authUserInfo, userJson);
                            userDao.setVisited(userJson.get("seq").toString());
                            getServletContext().getRequestDispatcher("/list_box.jsp").forward(req, res);
                        } else {
                            // 계정 생성 또는 연동을 위해 회원가입 폼으로 이동
                            HttpSession sess = req.getSession(true);
                            sess.setAttribute(EnvSYS.KEY_KAKAO_USER_ID, kakaoId); // 세션에 저장
                            req.setAttribute("kakao_user", "true");
                            if (usrJson.containsKey("email")) {
                                String userEmail = usrJson.get("email").toString();
                                req.setAttribute("user_email", userEmail);
                                if (userDao.existJuid(userEmail)) {
                                    req.setAttribute("exist_juid", "true");
                                }
                            }
                            getServletContext().getRequestDispatcher("/signup.jsp").forward(req, res);
                        }
                    }
                } else {
                    // 다시 로그인 화면으로
                    res.sendRedirect("/jbs/signin.jsp");
                }
                
            } else {
                //카카오 계정의 회원가입 또는 계정 연결 처리
                
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
                                String seqUser = userJson.get("seq").toString();
                                HttpSession sess = req.getSession(true);
                                Object kakaoidObj = sess.getAttribute(EnvSYS.KEY_KAKAO_USER_ID);
                                if (kakaoidObj != null) {
                                    UserDataAccessObject userDao = new UserDataAccessObject();
                                    userDao.setKakaoid(seqUser, kakaoidObj.toString());
                                    resObj.put("code", EnvSYS.RESCODE_SUCC);
                                    resObj.put("msg", "계정 연결에 성공하였습니다");
                                } else {
                                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                                    resObj.put("msg", "kakao 정보가 없습니다");
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

    private String getAccessToken(String code) throws IOException, ParseException {
        log.debug("code : " + code);

        String access_Token = null;
        String refresh_Token = null;
        String reqURL = "https://kauth.kakao.com/oauth/token";

        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", SafeProps.getString("KAKAO_REST_API_KEY"));
        params.put("client_secret", SafeProps.getString("KAKAO_CLIENT_SECRET"));
        params.put("redirect_uri", SafeProps.getString("KAKAO_REDIRECT_URI"));
        params.put("code", code);

        String resStr = HttpURLConnectionUtil.requestPostData(reqURL, null, params);

        if (resStr != null) {
            JsonNode jn = JSONUtil.parseJsonNode(resStr);

            access_Token = jn.get("access_token").asText();
            refresh_Token = jn.get("refresh_token").asText();

            log.debug("access_token : " + access_Token);
            log.debug("refresh_token : " + refresh_Token);
        } else {
            log.debug("access_token is null~");
        }

        return access_Token;
    }

    private JSONObject getUserInfo(String access_token) throws ParseException, IOException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        Map<String, String> props = new HashMap<String, String>();
        props.put("Authorization", "Bearer " + access_token);

        String resStr = HttpURLConnectionUtil.requestPostData(reqURL, props, null);

        log.debug("response body : " + resStr);

        if (resStr != null) {
            JsonNode jn = JSONUtil.parseJsonNode(resStr);

            long id = jn.get("id").asLong();
            String email = null;
            String phone = null;
            String nickname = null;
            if (jn.get("kakao_account") != null) {
                JsonNode kajn = jn.get("kakao_account");
                if (kajn.get("has_email") != null) {
                    boolean hasEmail = kajn.get("has_email").asBoolean();
                    if (hasEmail) {
                        email = kajn.get("email").asText();
                    }
                }
                if (kajn.get("phone_number") != null) {
                    phone = kajn.get("phone_number").asText();
                }
                if (kajn.get("profile_nickname") != null) {
                    nickname = kajn.get("profile_nickname").asText();
                }
            }
            log.debug("kakao id : " + id);
            log.debug("kakao email : " + email);
            log.debug("kakao phone : " + phone);
            log.debug("kakao nickname : " + nickname);

            JSONObject usrJson = new JSONObject();
            usrJson.put("id", id);
            if (email != null) {
                usrJson.put("email", email);
            }
            if (phone != null) {
                usrJson.put("phone", phone);
            }
            if (nickname != null) {
                usrJson.put("nickname", nickname);
            }
            return usrJson;
        } else {
            return null;
        }
    }

}
