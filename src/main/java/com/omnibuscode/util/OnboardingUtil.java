package com.omnibuscode.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.utils.FileUtil;

/**
 * 온보딩 리소스를 처리하기 위한 유틸리티
 */
public class OnboardingUtil {

	/**
	 * 사용자 온보딩 스크립트를 생성하고 경로를 반환한다
	 * @param us
	 * @param onboardName
	 * @param argsJson
	 * @return
	 */
	public static String createUsrOnboarding(String seqUser, String onboardName, JsonNode argsJson) {
		
		String fUserCodeTmp = "onboarding/user_code.src";
		String fUserCodePath = "onboarding/users/" + seqUser + "/" + onboardName + ".js";
		
		try {
			String userCodeTmp = FileUtil.readFile(EnvSYS.SYS_RES_PATH + fUserCodeTmp, null).toString();
			
			String page = argsJson.get("page").asText();
			ObjectMapper objectMapper = new ObjectMapper();
			String steps = objectMapper.writeValueAsString(argsJson.get("steps"));
			
			String userCodeContent = userCodeTmp.replaceAll("%PAGE%", page).replaceAll("%STEPS%", steps);
			FileUtil.writeFile(EnvSYS.SYS_RES_PATH + fUserCodePath, userCodeContent, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fUserCodePath;
	}
	
	/**
	 * '**' 문자가 시작하는 부분부터 끝에 해당하는 단어에 bold 를 적용한다.
	 * @param text
	 * @return
	 */
	public static String convertAsterisksToBoldTags(String input) {
		StringBuilder result = new StringBuilder();
		boolean boldToggle = false; // Bold 상태를 추적
		int index = 0;

		while (index < input.length()) {
			// '**' 발견 시 처리
			if (index + 1 < input.length() && input.charAt(index) == '*' && input.charAt(index + 1) == '*') {
				result.append(boldToggle ? "</b>" : "<b>"); // Bold 상태에 따라 태그 추가
				boldToggle = !boldToggle; // Bold 상태 변경
				index += 2; // '**'를 건너뜀
			} else {
				result.append(input.charAt(index)); // 일반 문자 추가
				index++;
			}
		}

		return result.toString();
    }
	
	/**
	 * '\"' 단어를 제거한다.
	 * @param input
	 * @return
	 */
	public static String removeBackslashQuotation(String input) {
		StringBuilder result = new StringBuilder();
		int index = 0;

		while (index < input.length()) {
			// '\"' 발견 시 처리
			if (index + 1 < input.length() && input.charAt(index) == '\\' && input.charAt(index + 1) == '"') {
				index += 2; // '\"'를 건너뜀
			} else {
				result.append(input.charAt(index)); // 일반 문자 추가
				index++;
			}
		}

		return result.toString();
	}
}
