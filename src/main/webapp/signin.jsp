<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="com.omnibuscode.utils.PropertiesUtil"%>
<%@ page import="com.omnibuscode.base.SafeProps"%>
<%@ page import="com.omnibuscode.auth.AuthManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
     if (AuthManager.getInstance().hasUserAuthed(request)) {
        response.sendRedirect("list_box.jsp");
     }
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="title" value="지니박스 로그인" />
    </jsp:include>
</head>

<body>
    <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
        <symbol id="check2" viewBox="0 0 16 16">
            <path
                d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z">
            </path>
        </symbol>
        <symbol id="circle-half" viewBox="0 0 16 16">
            <path d="M8 15A7 7 0 1 0 8 1v14zm0 1A8 8 0 1 1 8 0a8 8 0 0 1 0 16z"></path>
        </symbol>
        <symbol id="moon-stars-fill" viewBox="0 0 16 16">
            <path
                d="M6 .278a.768.768 0 0 1 .08.858 7.208 7.208 0 0 0-.878 3.46c0 4.021 3.278 7.277 7.318 7.277.527 0 1.04-.055 1.533-.16a.787.787 0 0 1 .81.316.733.733 0 0 1-.031.893A8.349 8.349 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.752.752 0 0 1 6 .278z">
            </path>
            <path
                d="M10.794 3.148a.217.217 0 0 1 .412 0l.387 1.162c.173.518.579.924 1.097 1.097l1.162.387a.217.217 0 0 1 0 .412l-1.162.387a1.734 1.734 0 0 0-1.097 1.097l-.387 1.162a.217.217 0 0 1-.412 0l-.387-1.162A1.734 1.734 0 0 0 9.31 6.593l-1.162-.387a.217.217 0 0 1 0-.412l1.162-.387a1.734 1.734 0 0 0 1.097-1.097l.387-1.162zM13.863.099a.145.145 0 0 1 .274 0l.258.774c.115.346.386.617.732.732l.774.258a.145.145 0 0 1 0 .274l-.774.258a1.156 1.156 0 0 0-.732.732l-.258.774a.145.145 0 0 1-.274 0l-.258-.774a1.156 1.156 0 0 0-.732-.732l-.774-.258a.145.145 0 0 1 0-.274l.774-.258c.346-.115.617-.386.732-.732L13.863.1z">
            </path>
        </symbol>
        <symbol id="sun-fill" viewBox="0 0 16 16">
            <path
                d="M8 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM8 0a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 0zm0 13a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 13zm8-5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2a.5.5 0 0 1 .5.5zM3 8a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2A.5.5 0 0 1 3 8zm10.657-5.657a.5.5 0 0 1 0 .707l-1.414 1.415a.5.5 0 1 1-.707-.708l1.414-1.414a.5.5 0 0 1 .707 0zm-9.193 9.193a.5.5 0 0 1 0 .707L3.05 13.657a.5.5 0 0 1-.707-.707l1.414-1.414a.5.5 0 0 1 .707 0zm9.193 2.121a.5.5 0 0 1-.707 0l-1.414-1.414a.5.5 0 0 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .707zM4.464 4.465a.5.5 0 0 1-.707 0L2.343 3.05a.5.5 0 1 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .708z">
            </path>
        </symbol>
    </svg>
    <div id="quick_buttons" class="dropdown position-fixed bottom-0 end-0 mb-3 me-3 bd-mode-toggle">
        <!-- 테마 변경 버튼 -->
        <button id="bd-theme" title="테마 변경" type="button" class="btn btn-outline-primary py-2 dropdown-toggle d-flex align-items-center"
            aria-expanded="false" data-bs-toggle="dropdown" aria-label="Toggle theme (light)">
            <svg class="bi my-1 theme-icon-active" width="1em" height="1em">
                <use href="#sun-fill"></use>
            </svg>
            <span class="visually-hidden" id="bd-theme-text">Toggle theme</span>
        </button>
        <ul id="theme-select" class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="bd-theme-text">
            <li>
                <button type="button" class="dropdown-item d-flex align-items-center active" data-bs-theme-value="light"
                    aria-pressed="true">
                    <svg class="bi me-2 opacity-50 theme-icon" width="1em" height="1em">
                        <use href="#sun-fill"></use>
                    </svg>
                    Light
                    <svg class="bi ms-auto d-none" width="1em" height="1em">
                        <use href="#check2"></use>
                    </svg>
                </button>
            </li>
            <li>
                <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="dark"
                    aria-pressed="false">
                    <svg class="bi me-2 opacity-50 theme-icon" width="1em" height="1em">
                        <use href="#moon-stars-fill"></use>
                    </svg>
                    Dark
                    <svg class="bi ms-auto d-none" width="1em" height="1em">
                        <use href="#check2"></use>
                    </svg>
                </button>
            </li>
            <li>
                <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="auto"
                    aria-pressed="false">
                    <svg class="bi me-2 opacity-50 theme-icon" width="1em" height="1em">
                        <use href="#circle-half"></use>
                    </svg>
                    Auto
                    <svg class="bi ms-auto d-none" width="1em" height="1em">
                        <use href="#check2"></use>
                    </svg>
                </button>
            </li>
        </ul>
    </div>

    <nav class="navbar navbar-expand-md" id="jb_nav">
        <div class="container-fluid justify-content-between">            
            <div id="title_set" class="d-flex align-items-center">
                <a id="anchor_store_title" class="navbar-brand fs-1 fw-bold" href="https://youtu.be/U4e-JEjWz44?si=SVtUdZ_nrGUB0Jb8" target="_blank">
                    <img src="./res/web/images/jiniebox-baby-round.png" class="img-thumbnail" style="width:50px; height:50px;" alt="JINIEBOX BABY" title="소개 영상 보기">
                </a>
                <a class="navbar-brand fs-1 fw-bold" href="/jbs/list_box.jsp" target="_self">
                    <span id="store_title" class="align-middle" title="메인화면">지니박스</span>
                </a>
            </div>
            
            <div class="d-flex align-items-center">
                <button id="install_app" class="btn btn-outline-dark" hidden>앱설치</button>&nbsp;
                <a id="signin_link" type="button" class="btn btn-outline-dark" href="#collapseLogin" data-bs-toggle="collapse" role="button" aria-expanded="false" aria-controls="collapseLogin">로그인</a>
            </div>
            <script>
                function changeStyleSignin() {                    
                    var is_darktime = false;                    
                    var curr_theme = localStorage.getItem('theme');
                    if (curr_theme == "dark") {
                        is_darktime = true;
                    } else if (curr_theme == "auto") {
                        const currentHour = new Date().getHours();
                        if (currentHour >= 19 || currentHour < 6) { // Assuming 7PM to 6AM is night time
                            //dark theme
                            is_darktime = true; //2024.07.10 일부 브라우저에서는 toggle theme 가 시간에 따라 동작하지 않지만 일단 적용한다.
                        }

                        // web/color-modes.js 에서 추출한 로직으로 위에서 처리 못한 케이스에 대해 최종 방어 코드이다.
                        if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                            is_darktime = true;
                        } else {
                            is_darktime = false;
                        }
                    }

                    var btn_style = "btn btn-outline-dark";
                    if (is_darktime) {
                        btn_style = "btn btn-outline-light";
                    }
                    document.querySelector("#signin_link").className = btn_style;
                    document.querySelector("#install_app").className = btn_style;
                }

                document.querySelector("#theme-select").addEventListener('click', function (event) {
                    changeStyleSignin();
                }, false);
            </script>
        </div>
    </nav>
    
    <div class="container-fluid">
        <div class="row collapse mb-2" id="collapseLogin">

            <div class="col-sm-4 mb-1">
                <div class="card">
                    <div class="card-body">
                            <script src="https://t1.kakaocdn.net/kakao_js_sdk/2.4.0/kakao.min.js" integrity="sha384-mXVrIX2T/Kszp6Z0aEWaA8Nm7J6/ZeWXbL8UpGRjKwWe56Srd/iyNmWMBhcItAjH" crossorigin="anonymous"></script>
                            <script>
                                Kakao.init('<%=SafeProps.getString("KAKAO_JAVASCRIPT_KEY", "")%>');
                            </script>
                            <a id="kakao-login-btn" href="javascript:loginWithKakao()" class="no-underline">
                                <img src="./res/web/images/kakaotalk_sharing_btn_medium_ov.png" height="45" alt="카카오 로그인 버튼" />
                            </a>
                            <script>
                                function loginWithKakao() {
                                    Kakao.Auth.authorize({
                                        redirectUri: '<%=SafeProps.getString("KAKAO_REDIRECT_URI", "")%>',
                                        state : 'userme',
                                    });
                                }
                            </script>

                            <%
                                String clientId = SafeProps.getString("NAVER_CLIENT_ID", "");
                                String redirectURI = URLEncoder.encode(SafeProps.getString("NAVER_REDIRECT_URI", ""), "UTF-8");
                                SecureRandom random = new SecureRandom();
                                //String state = new BigInteger(130, random).toString();
                                String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                                    + "&client_id=" + clientId
                                    + "&redirect_uri=" + redirectURI;
                            %>
                            <a href="<%=apiURL%>" class="no-underline"><img height="45" src="./res/web/images/btnG_naver.png"/></a>
                    </div>
                </div>
            </div>

            <div class="col-sm-8 mb-sm-0">
                <div class="card">
                    <div class="card-body">
                        <div class="form-floating">
                            <input type="text" class="form-control" id="juid" placeholder="Id" value="">
                            <label>아이디</label>
                        </div>
                        <div class="form-floating my-3">
                            <input type="password" class="form-control" id="jupw" placeholder="Password" value="">
                            <label>암호</label>
                        </div>

                        <div class="checkbox mb-3 position-relative">
                            <label>
                                <span class="position-absolute top-50 start-0 translate-middle-y">
                                    <input type="checkbox" value="remember-me"> Remember me
                                </span>
                            </label>
                            <span class="position-absolute top-50 end-0 translate-middle-y">
                                <%-- <a href="./signup.jsp" class="link-body-emphasis link-offset-2 link-underline-info link-underline-opacity-25 link-underline-opacity-75-hover"> --%>
                                <a href="./signup.jsp" class="btn btn-outline-primary">
                                    Sign up(회원가입)
                                </a>
                            </span>
                        </div>
                        <button class="w-100 btn btn-lg btn-primary" type="button" id="btn_signin">Sign in(로그인)</button>
                    </div>
                </div>
            </div>

        </div>

        <p class="mb-1">지니박스는 가정에서 간단하게 사용할 수 있는 물류 관리 시스템입니다.</p>
        <div class="card flex-column flex-md-row mb-1" style="max-width: 100%; width: auto;">
            <img 
                src="./res/web/images/jangbogo.jpg" 
                class="img-fluid" 
                style="max-width: 340px; width: 100%;" 
                alt="Card image">
            <div class="card-body">
                <h5 class="card-title">기본기능</h5>
                <ul>
                    <li>상품 바코드 스캔으로 상품명 입력</li>
                    <li>구매 영수증으로 상품을 일괄 등록</li>
                    <li>상품의 사진 이미지를 추가</li>
                    <li>개인 저장소(구글 드라이브)에 이미지 파일을 저장</li>
                    <li>자신이 관리하는 데이터를 다른 사람과 공유하여 함께 관리</li>
                    <li>만료기간이 임박한 상품에 대해 알림 메세지 수신</li>
                    <li>네이버 클로바의 음성 명령으로 아이템 검색</li>
                    <li>나눔함 기능으로 사용자 간에 물품 공유를 활성화</li>
                </ul>
            </div>
        </div>

        <%-- <div class="card max-w-sm mb-1">
            <div class="card-body">
                <h5 class="card-title">장보고</h5>
                <p class="card-text">아이템 수집과 분류를 자동화하여 편리하게 이용해 보세요.</p>
            </div>
            <video class="card-img-bottom w-85" controls>
                <source src="about/intro.mp4" type="video/mp4">
                Your browser does not support the video tag.
            </video>
        </div> --%>

    </div>

    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script>
        window.addEventListener('load', () => {
            const _doc = window.document;

<%
    Object forwardUrl = request.getAttribute("forward_url");
    if (forwardUrl != null) {
%>
            _doc.forward_url = '<%=forwardUrl.toString()%>';
<%
    }
%>
            changeStyleSignin();
            showElement(_doc.querySelector('#install_app'));

        }, false);

        window.document.querySelector("#btn_signin").addEventListener('click', function (event) {
            const _doc = window.document;

            var val_juid = _doc.querySelector("#juid").value;
            var val_jupw = _doc.querySelector("#jupw").value;

            if (!val_juid) {
                alert("아이디를 입력해주세요");
                return -1;
            }
            if (!val_jupw) {
                alert("비밀번호를 입력해주세요");
                return -1;
            }

            var path = "/jbs/auth";
            var params = "cmd=validUser";
            params += "&juid=" + val_juid;
            params += "&jupw=" + val_jupw;
            params += "&seq_nanum=" + urlparam('seq_nanum');
            params += "&scd=" + urlparam('scd');

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        if (resJo.code == '000') {
                            //alert("로그인에 성공 하였습니다");

                            if (_doc.forward_url) {
                                window.location.href = _doc.forward_url;
                            } else if (urlparam('seq_nanum')) {
                                window.location.href = "/jbs/list_nanum.jsp?seq_nanum="+urlparam('seq_nanum');
                            } else {
                                window.location.href = "/jbs/list_box.jsp";
                            }
                        } else if (resJo.msg) {
                            alert(resJo.msg);
                        }
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        window.addEventListener('beforeinstallprompt', (e) => {
            const _doc = window.document;

            //설치 버튼 보이기
            showElement(_doc.querySelector('#install_app'));

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
                } else {
                    alert("지니박스 아이콘이 이미 있습니다.");
                    hideElement(_doc.querySelector('#install_app'));
                }
            });
        });
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>
    
</body>

</html>