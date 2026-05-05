package com.omnibuscode.ctrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.FcmTokenDataAccessObject;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.JSONUtil;


/**
 * @author KIUNSEA
 *
 */
@WebServlet("/fcm")
public class FcmServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogUtil.getLog(UserServlet.class);
    private Logger log = LogManager.getLogger(FcmServlet.class);

    public void init() {;}

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // 요청의 인코딩 설정
        req.setCharacterEncoding("UTF-8");
        // 응답의 인코딩 설정
        res.setContentType("application/json; charset=UTF-8");
        // 요청의 본문을 읽어오는 BufferedReader 객체 생성
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        // 본문의 내용을 저장할 StringBuilder 객체 생성
        StringBuilder sb = new StringBuilder();
        // 본문의 내용을 한 줄씩 읽어서 StringBuilder에 추가
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        // StringBuilder의 내용을 json 문자열로 변환
        String jsonStr = sb.toString();
        // Data 객체의 내용을 콘솔에 출력
        log.debug("요청 본문 : "+jsonStr);

        // 응답의 본문을 쓰기 위한 PrintWriter 객체 생성
        PrintWriter out = res.getWriter();
        
        try {
            JSONObject jsonObj = JSONUtil.parseJSONObject(jsonStr);
            Object cmdObj = jsonObj.get("cmd");

            if (cmdObj != null) {
                if ("saveToken".equals(cmdObj.toString())) {
                    this.saveToken(req, jsonObj.get("token").toString());
                }
            }
            
            // 응답의 본문에 json 문자열을 쓰기
            jsonObj.put("Success", "ok");
            out.print(jsonObj.toString());
        } catch (ParseException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
        
        // PrintWriter 객체 닫기
        out.close();
        
    }
    
    private void saveToken(HttpServletRequest req, String token) throws Exception {
        UserSession usrSession = AuthManager.getInstance().getUserSession(req);

        if (usrSession != null) {
            String seqUser = usrSession.getSeq();
            FcmTokenDataAccessObject ftDao = new FcmTokenDataAccessObject();
            if (!ftDao.existToken(token, seqUser)) {
                ftDao.addToken(token, seqUser);
                log.debug("토큰 저장 완료 - " + token);
            }
        }
    }
}
