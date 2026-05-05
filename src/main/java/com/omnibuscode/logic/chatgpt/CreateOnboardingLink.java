package com.omnibuscode.logic.chatgpt;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
// import com.omnibuscode.ai.ProcessFunction; // TODO: AI 패키지 구현 필요
import com.omnibuscode.base.UserSession;
import com.omnibuscode.util.OnboardingUtil;

/**
 * 사용자 온보딩 파일을 생성하고 url path 를 제공한다.
 * TODO: AI 패키지 구현 후 ProcessFunction 인터페이스 구현 필요
 */
public class CreateOnboardingLink /* implements ProcessFunction */ {

	private Logger log = LogManager.getLogger(CreateOnboardingLink.class);
	private UserSession usrSess = null;
	
	public CreateOnboardingLink(UserSession us) {
		this.usrSess = us;
	}
	
	public JSONObject getFunctionJson() {
		return null;
	}
	
	// @Override // TODO: AI 패키지 구현 후 주석 해제
	public JSONObject execFunction(String funcName, JsonNode argsJson) {
		log.debug("* execute user function - " + argsJson);
		if (this.usrSess != null) {
			String userCodePath = OnboardingUtil.createUsrOnboarding(this.usrSess.getSeq(), funcName, argsJson);
			String link = "/jbs/onboarding?usr_scenario=" + userCodePath;
			
//			try {
//				String onboardLink = "<br>- 온보딩 가이드 : <a href='#' onclick=\"userOnboarding('"
//						+ java.net.URLEncoder.encode(link, "UTF-8") + "')\">" + funcName + "</a>";
//
//				JSONObject resJson = new JSONObject();
//				resJson.put("onboard_link", onboardLink);
//				return resJson;
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			//XXX 삭제예정 2025.04.05 encoding시 web에서 decoding 안됨
			
			String onboardLink = "<br>- 온보딩 가이드 : <a href='#' onclick=\"userOnboarding('"
					+ link + "')\">" + funcName + "</a>";

			JSONObject resJson = new JSONObject();
			resJson.put("onboard_link", onboardLink);
			return resJson;
		} else {
			log.error("UserSession is Null!!");
		}
		
		return null;
	}

}
