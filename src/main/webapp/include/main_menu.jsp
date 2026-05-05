<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String paramValue = null;

    boolean is_signup = false;
    paramValue = request.getParameter("page_name");
    if(paramValue != null) {
      is_signup = "signup".equals(paramValue.toString());
    }
    boolean is_partner = false;
    paramValue = request.getParameter("is_partner");
    if(paramValue != null) {
      is_partner = Boolean.parseBoolean(paramValue);
    }
%>
  <script type="text/javascript">
    checkLogin();
    window.addEventListener('DOMContentLoaded', event => {
      const _doc = window.document;
	  
<% if (is_partner) { %>
      showElement(_doc.querySelector("#a_jangbogo"));
      showElement(_doc.querySelector("#search_item"));
<% } else { %>
      hideElement(_doc.querySelector("#a_jangbogo"));
      hideElement(_doc.querySelector("#search_item"));
<% } %>
    });
  </script>
  <nav id="jb_nav" class="navbar">
    <div class="container-fluid justify-content-between">
      <div id="title_set" class="d-flex align-items-center">
        <a class="btn btn-white btn-sm" style="white-space: nowrap;" href="#" data-bs-toggle="modal" data-bs-target="#chatbotModal">
          <img id="jinie_char" src="./res/web/images/jiniebox-baby-round.png" class="img-thumbnail" alt="JINIEBOX BABY"title="JINIEBOX BABY" style="width:50px; height:50px;">
        </a>
        <div id="chatbotModal" class="modal fade" tabindex="-1" aria-labelledby="chatbotModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-dialog-end">
              <div class="modal-content">
                  <div class="modal-header">
                      <h5 class="modal-title" id="chatbotModalLabel">지니예요~</h5>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"/>
                  </div>
                  <div class="modal-body">
                      <!-- 챗봇 대화 내용 -->
                      <div id="div_messages" class="chat-messages">
                          <!-- 메시지 예시 -->
                          <div class="message bot-message">무엇을 도와드릴까요?</div>
                      </div>
                  </div>
                  <div class="modal-footer">
                      <input id="user_msg" type="text" class="form-control" placeholder="메시지를 입력하세요..." aria-label="메시지 입력">
                      <div>
                          <input class="form-check-input" type="checkbox" id="reuse_chatroom">
                          <span id="aimd_add_cont_label">세션사용</span>
                      </div>
                      <div>
                        <button id="send_msg" type="button" class="btn btn-primary">전송</button>&nbsp;
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">닫기</button>
                      </div>
                  </div>
              </div>
          </div>
        </div>
        <script src="./res/web/chatbot-polling.js"></script>
        <script type="text/javascript">
          const chatbotModal = document.querySelector("#chatbotModal");
          chatbotModal.addEventListener('shown.bs.modal', function (event) {
            //alert("shown.bs.modal");
            //startPolling(); //2025.01.18 function calling 을 감지하기 위한 코드인데 현재는 개발중이므로 잠시 주석한다
            var path = "/jbs/chatbot";
            var params = "cmd=getReuseChatroom";

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    let resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJson = JSON.parse(resMsg);
                        document.querySelector("#reuse_chatroom").checked = resJson.reuse_chatroom;
                    }
                }
            };
            sendPost(_ajax, path, params);
          });
          chatbotModal.addEventListener('hidden.bs.modal', function (event) {
            //alert("hidden.bs.modal");
            //clearPolling(); //2025.01.18 function calling 을 감지하기 위한 코드인데 현재는 개발중이므로 잠시 주석한다
            var path = "/jbs/chatbot";
            var params = "cmd=setReuseChatroom";
            params += "&reuse_chatroom="+document.querySelector("#reuse_chatroom").checked;

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    let resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJson = JSON.parse(resMsg);
                        document.querySelector("#reuse_chatroom").checked = resJson.reuse_chatroom;
                    }
                }
            };
            sendPost(_ajax, path, params);
          });
          window.document.querySelector("#send_msg").addEventListener('click', event => {
            const _doc = window.document;
	
            attachSpinnerButton(_doc.querySelector("#send_msg"));

            var path = "/jbs/chatbot";
            var params = "cmd=sendMsg";
            params += "&user_msg=" + encodeURIComponent(_doc.querySelector("#user_msg").value);
    
            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    let resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJson = JSON.parse(resMsg);
                        if (resJson.code == '000') {
                          _doc.querySelector("#div_messages").appendChild(_doc.createElement("hr"));
                          let div_msg = _doc.createElement("div");
                          div_msg.className = "message bot-message";
                          //div_msg.innerHTML = decodeURIComponent(resJson.assist_msg);
                          div_msg.innerHTML = resJson.assist_msg;
                          _doc.querySelector("#div_messages").appendChild(div_msg);
                          _doc.querySelector("#user_msg").value = "";
                        } else {
                          
                        }

                        removeSpinnerButton(_doc.querySelector("#send_msg"), "전송");
                    }
                }
            };
            sendPost(_ajax, path, params);
	        });
        </script>
        &nbsp;
        <a id="anchor_store_title" class="navbar-brand fs-1 fw-bold align-middle" href="/jbs/list_box.jsp">
          <span id="store_title" class="align-middle" title="메인화면">지니박스</span>
        </a>
      </div>
      <button id="btn_toggle_menu" class="navbar-toggler collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="navbarSupportedContent" class="collapse navbar-collapse">
        <ul id="menuitem_list" class="navbar-nav me-auto mb-2">
          <li id="li_profile" class="nav-item">
            <a class="btn btn-white btn-sm" style="white-space: nowrap;" href="/jbs/about" target="_blank">Θ About(소개)</a>
          </li>
<%if (!is_signup) {%>
          <li id="a_store_list" class="nav-item"><a class="btn btn-white btn-sm" style="white-space: nowrap;" href="/jbs/store_list.jsp">Θ Store(저장소)</a></li>
          <li id="a_jangbogo" class="nav-item"><a class="btn btn-white btn-sm" style="white-space: nowrap;" href="/jbs/store_jbg.jsp">Θ Jbg(장보고)</a></li>
          <li id="li_user" class="nav-item">
            <a class="btn btn-white btn-sm" style="white-space: nowrap;" href="/jbs/profile.jsp">Θ User(사용자)</a>
          </li>
<%}%>
          <li id="li_setting" class="nav-item dropdown">
            <a class="btn btn-white btn-sm dropdown-toggle" style="white-space: nowrap;" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">Θ App(앱설정)</a>
            <ul id="setting" class="dropdown-menu" style="margin-left: 20px;">
              <li id="li_initnoti">
                <a id='init_noti' class='btn btn-white btn-sm' style='white-space: nowrap;' onclick="javascript:initNoti()">InitNoti(알림초기화)</a>
              </li>
            </ul>
          </li>
          <li id="li_signinout" class="nav-item">
            <a class="btn btn-white btn-sm" style="white-space: nowrap;" href="/jbs/signin.jsp" id="signin_link">Θ Signin(로그인)</a>
          </li>
<%if (!is_signup) {%>
          <li class="nav-item">
            <input id="search_item" class="form-control" type="search" placeholder="Search" aria-label="Search">
          </li>
<%}%>
        </ul>
      </div>
    </div>
  </nav>
  <script type="text/javascript">
    if (window.document.querySelector("#search_item")) {
      window.document.querySelector("#search_item").addEventListener('keyup', (event) => {
        if (event.key === 'Enter') {
          alert('준비중...'); // 엔터 키를 누르면 알림창이 출력됩니다.
        }
      }, false);
    }

    window.addEventListener('beforeinstallprompt', (e) => { //beforeinstallprompt 이벤트 지원시에만 동작
      const _doc = window.document;

      // 설치 버튼 생성하여 붙이기
      var elem_ul = _doc.querySelector('#setting')
      var elem_li = _doc.querySelector('#li_inst')

      if (elem_ul && !elem_li) {
        elem_li = document.createElement('li');
        elem_li.id = "li_inst";
        elem_li.className = "nav-item";
        elem_li.innerHTML = "<a id='install_app' class='btn btn-white btn-sm' style='white-space: nowrap;' href='#'>Install(앱설치)</a>";
        var last_li = _doc.querySelector('#li_initnoti');
        elem_ul.insertBefore(elem_li, last_li);
      }	
      e.preventDefault();
      var deferredPrompt = e;
      _doc.querySelector('#install_app').addEventListener('click', (e) => {
        if (deferredPrompt) {
          // 설치 버튼 클릭시 설치 프롬프트 표시
          deferredPrompt.prompt();
          deferredPrompt.userChoice.then((choiceResult) => {
            if (choiceResult.outcome === 'accepted') {
              console.log('User accepted the A2HS prompt');
            } else {
              console.log('User dismissed the A2HS prompt');
            }
            deferredPrompt = null;
          });
        }
      });
    });

    function initNoti() {
      if (confirm('알림 정보를 초기화 할까요?')) {
        localStorage.removeItem("jbs_fcm");
        alert("알림 정보를 초기화 하였습니다.");
      }
    }
  </script>