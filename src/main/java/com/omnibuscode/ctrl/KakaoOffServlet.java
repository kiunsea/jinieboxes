package com.omnibuscode.ctrl;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TODO 카카오 애플리케이션 관리에서 '연결 끊기' 가이드를 참고하여 request 에 대한 처리를 개발해야 한다.
 * 지니박스에서 Kakao 연동을 해제하기 위한 클래스
 * @author KIUNSEA
 */
@WebServlet("/kakaooff")
public class KakaoOffServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(KakaoOffServlet.class);

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
        ;
    }


}
