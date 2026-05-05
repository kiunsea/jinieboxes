package com.omnibuscode.ctrl.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test/polling")
public class PollingServletTest extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 메시지 데이터 저장 (예제용)
	private List<HashMap<String, String>> messages;

	@Override
	public void init() throws ServletException {
		super.init();
		// 예제 메시지 데이터 초기화
		messages = new ArrayList<>();
		HashMap<String, String> message1 = new HashMap<>();
		message1.put("id", "1");
		message1.put("text", "첫 번째 메시지");
		messages.add(message1);

		HashMap<String, String> message2 = new HashMap<>();
		message2.put("id", "2");
		message2.put("text", "두 번째 메시지");
		messages.add(message2);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 응답 Content-Type 설정
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		// JSON 데이터 생성
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		if (!messages.isEmpty()) {
			jsonBuilder.append("\"hasMessages\": true, \"messages\": [");
			for (int i = 0; i < messages.size(); i++) {
				HashMap<String, String> message = messages.get(i);
				jsonBuilder.append("{").append("\"id\": \"").append(message.get("id")).append("\",")
						.append("\"text\": \"").append(message.get("text")).append("\"}");
				if (i < messages.size() - 1) {
					jsonBuilder.append(",");
				}
			}
			jsonBuilder.append("]");
		} else {
			jsonBuilder.append("\"hasMessages\": false, \"messages\": []");
		}
		jsonBuilder.append("}");

		// 응답 출력
		PrintWriter out = resp.getWriter();
		out.print(jsonBuilder.toString());
		out.flush();
	}
}
