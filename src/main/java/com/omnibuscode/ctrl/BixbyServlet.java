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
import org.json.simple.parser.ParseException;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 지니박스와 빅스비를 연동하기 위한 클래스이다
 * @author KIUNSEA
 */
@WebServlet("/bixby")
public class BixbyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(BixbyServlet.class);

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

            JSONObject resObj = new JSONObject();
            String cmdHttp = req.getParameter("cmd");

            if (cmdHttp != null) {

                if ("update_account".equals(cmdHttp)) {
                    // 빅스비 계정 연결 처리
                    String juid = req.getParameter("juid");
                    String jupw = req.getParameter("jupw");
                    if (juid != null && jupw != null) {
                        UserDataAccessObject uDao = new UserDataAccessObject();
                        JSONObject userJson = uDao.getUserByJuid(juid);
                        if (userJson != null) {
                            String seqUser = userJson.get("seq").toString();
                            String buid = req.getParameter("buid");

                            if (buid != null) {
                                UserDataAccessObject userDao = new UserDataAccessObject();
                                userDao.setBixbyUserid(seqUser, buid);
                                resObj.put("code", EnvSYS.RESCODE_SUCC);
                                resObj.put("msg", "계정 연결에 성공하였습니다");
                            } else {
                                resObj.put("code", EnvSYS.RESCODE_FAIL);
                                resObj.put("msg", "bixby user id 정보가 없습니다");
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
    
}
