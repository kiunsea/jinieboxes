// 서버의 API 엔드포인트 URL
const serverUrl = "https://dev.jiniebox.com/jbs/test/polling";

// Polling 주기 (밀리초 단위)
const pollingInterval = 3000; // 3초

// 서버 메세지 확인 시작
async function startPolling() {
	try {
		// Fetch API로 서버에 GET 요청
		const response = await fetch(serverUrl, {
			method: "GET",
			headers: {
				"Content-Type": "application/json",
			},
		});

		if (!response.ok) {
			throw new Error(`서버 오류: ${response.status}`);
		}

		// 서버 응답 JSON 파싱
		const data = await response.json();

		// 메시지가 존재하면 처리
		if (data.hasMessages) {
			console.log("새로운 메시지:", data.messages);
			// 메시지를 화면에 표시하거나 다른 로직 추가
			//displayMessages(data.messages);
		} else {
			console.log("새로운 메시지가 없습니다.");
		}
	} catch (error) {
		console.error("메시지 확인 중 오류 발생:", error);
	} finally {
		// Polling 반복 호출
		window.document.pollingTimeoutId = setTimeout(startPolling, pollingInterval);
	}
}

// 메시지 표시 함수 (예제)
function displayMessages(messages) {
	const messageContainer = document.getElementById("messageContainer");
	messageContainer.innerHTML = ""; // 기존 메시지 초기화

	messages.forEach((message) => {
		const messageElement = document.createElement("div");
		messageElement.textContent = message.text;
		messageContainer.appendChild(messageElement);
	});
}

// 서버 메세지 확인 중지
function clearPolling() {
	clearTimeout(window.document.pollingTimeoutId);
	console.log("clearPolling!!");
}