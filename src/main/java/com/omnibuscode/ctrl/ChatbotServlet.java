package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

// import com.fasterxml.jackson.databind.JsonNode; // TODO: AI 패키지 구현 후 사용
// import com.omnibuscode.ai.ChatRoom; // TODO: AI 패키지 구현 필요
// import com.omnibuscode.ai.Chatting; // TODO: AI 패키지 구현 필요
// import com.omnibuscode.ai.ProcessFunction; // TODO: AI 패키지 구현 필요
// import com.omnibuscode.ai.manager.ChatManager; // TODO: AI 패키지 구현 필요
// import com.omnibuscode.ai.openai.Assistant; // TODO: AI 패키지 구현 필요
import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
// import com.omnibuscode.logic.chatgpt.CreateOnboardingLink; // TODO: AI 패키지 구현 후 사용
// import com.omnibuscode.util.OnboardingUtil; // TODO: AI 패키지 구현 후 사용
import com.omnibuscode.utils.ExceptionUtil;
// import com.omnibuscode.utils.JSONUtil; // TODO: AI 패키지 구현 후 사용
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 지니박스와 클로바를 연동하기 위한 클래스이다
 * @author KIUNSEA
 */
@WebServlet("/chatbot")
public class ChatbotServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(ChatbotServlet.class);

    public void init() {
        /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        
    	JSONObject resObj = new JSONObject();
    	
    	try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
            	String cmdHttp = req.getParameter("cmd");
                if (cmdHttp != null) {
                    if ("sendMsg".equals(cmdHttp)) {
                    	// TODO: AI 패키지 구현 필요 - 현재 비활성화
                    	log.warn("Chatbot 기능은 현재 사용할 수 없습니다. AI 패키지 구현이 필요합니다.");
                    	resObj.put("code", EnvSYS.RESCODE_FAIL);
                    	resObj.put("msg", "Chatbot 기능은 현재 사용할 수 없습니다.");
                    	
                    	/* AI 패키지 구현 후 활성화
                    	UserSession us = AuthManager.getInstance().getUserSession(req);
                    	String seqUser = us.getSeq();
                    	String userMsg = req.getParameter("user_msg");
                    	log.debug("#UserMsg: "+userMsg);
                    	
                    	ChatManager cm = new ChatManager();
						ChatRoom cr = null;
						String funcName = "on_jangbogo";
						
						if (us.isReuseChatRoom()) {
							cr = us.getSessChatRoom();
						}
						
						if (cr == null) {
							String assistantId = PropertiesUtil.get("OPENAI_ASSIST_ID");
							Assistant assist = new Assistant(assistantId);

							String apiKey = PropertiesUtil.get("OPENAI_API_KEY");
							assist.setApiKey(apiKey);
							
							ProcessFunction usrFunc = new CreateOnboardingLink(us);
							assist.putFunction(funcName, usrFunc); //사용자 function 등록

							cm.setAssistant(assist); //assistant 등록
							JSONObject jo = cm.createChatRoom(ChatManager.AI_NAME_OPENAI);
							cr = (ChatRoom) jo.get("instance");
							if (us.isReuseChatRoom()) {
								us.setSessChatRoom(cr);
							}
						}
						
						Chatting chat = cr.createChatting();
						JSONObject resJson = chat.sendMessage((userMsg != null ? userMsg : "")); //메세지 전송
						String resMsg = resJson.get("message").toString();
						
						JSONObject usrFuncsRst = (JSONObject) resJson.get(ChatManager.USER_FUNCTIONS_RESULT);
						if (usrFuncsRst != null) {
							JsonNode usrFuncsRstJson = JSONUtil.jsonObjectToJsonNode(usrFuncsRst);
							resMsg += usrFuncsRstJson.get(funcName).get("onboard_link").toString();
						}
						
						//html tag로 변환
						resMsg = resMsg.replaceAll("\\r\\n|\\r|\\n", "<br>");
						resMsg = OnboardingUtil.convertAsterisksToBoldTags(resMsg);
						resMsg = OnboardingUtil.removeBackslashQuotation(resMsg);
						
                    	log.debug("#AiMsg: "+resMsg);
						resObj.put("assist_msg", resMsg);
						resObj.put("code", EnvSYS.RESCODE_SUCC);
						
						if (!us.isReuseChatRoom()) {
							cm.closeChatRoom();
						}
						*/
                    } else if ("getReuseChatroom".equals(cmdHttp)) {
                    	UserSession us = AuthManager.getInstance().getUserSession(req);
                    	resObj.put("reuse_chatroom", us.isReuseChatRoom());
                    } else if ("setReuseChatroom".equals(cmdHttp)) {
                    	UserSession us = AuthManager.getInstance().getUserSession(req);
                    	String reuseChatRoom = req.getParameter("reuse_chatroom");
                    	if (reuseChatRoom != null && Boolean.parseBoolean(reuseChatRoom)) {
                    		us.setReuseChatRoom(true);
                    		resObj.put("reuse_chatroom", "true");
                    	} else {
                    		us.setReuseChatRoom(false);
                    		resObj.put("reuse_chatroom", "false");
                    	}
                    }
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            } else {
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
    	
    	// resObj는 이미 초기화되어 있으므로 null 체크 불필요
    	
    	res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resObj);
        out.flush();
        out.close();
    }

}
