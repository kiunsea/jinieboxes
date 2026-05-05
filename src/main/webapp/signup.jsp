<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="jakarta.servlet.ServletContext" %>
<%@ page import="com.omnibuscode.utils.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    PropertiesUtil.USER_PROPERTIES_PATH = this.getServletContext().getRealPath("/") + "WEB-INF/classes/res/JINIEBOX.PROPERTIES";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="jiniebox">
    <meta name="generator" content="">
    <title>지니박스 사용자 등록</title>

    <link rel="canonical" href="https://getbootstrap.com/docs/5.3/examples/dashboard/">

    <!-- Favicons -->
    <link rel="apple-touch-icon" sizes="57x57" href="./res/web/icons/apple-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="./res/web/icons/apple-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="./res/web/icons/apple-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="./res/web/icons/apple-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="./res/web/icons/apple-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="./res/web/icons/apple-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="./res/web/icons/apple-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="./res/web/icons/apple-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="./res/web/icons/apple-icon-180x180.png">
    <link rel="icon" type="image/png" sizes="192x192"  href="./res/web/icons/android-icon-192x192.png">
    <link rel="icon" type="image/png" sizes="32x32" href="./res/web/icons/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="96x96" href="./res/web/icons/favicon-96x96.png">
    <link rel="icon" type="image/png" sizes="16x16" href="./res/web/icons/favicon-16x16.png">
    <meta name="msapplication-TileColor" content="#ffffff">
    <meta name="msapplication-TileImage" content="./res/web/icons/ms-icon-144x144.png">
    <meta name="theme-color" content="#ffffff">
    <link rel="manifest" href="./manifest.json">

    <link rel="stylesheet" href="./res/web/bootstrap/bootstrap-5.3.3-dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="./res/web/bootstrap/bootstrap-icons-1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="./res/web/bootstrap/ext/vanillajs-datepicker@1.3.2/datepicker-bs5.min.css">
    <link rel="stylesheet" href="./res/web/onboarding/driver/1.0.1/driver.css"/>
    <link rel="stylesheet" href="./res/web/jiniebox.css">

    <!-- Custom styles for this template -->
    <link rel="stylesheet" href="./res/web/dashboard.css">
    <style data-emotion="css" data-s=""></style>

    <script src="./res/web/color-modes.js"></script>
    <script src="./res/web/onboarding/driver/1.0.1/driver.js.iife.js"></script>
    <script src="./res/web/onboarding/signup.js" charset="UTF-8"></script>
    <script src="./res/web/jiniebox.js"></script>
</head>

<body>
    <jsp:include page="./include/main_menu.jsp">
        <jsp:param name="page_name" value="signup" />
    </jsp:include>

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
        <!-- 온보딩 헬프 버튼 -->
		<button id="btn_onboarding" title="온보딩 시작" type="button" class="btn btn-outline-primary py-2 d-flex">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-question-square" viewBox="0 0 16 16">
                <path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z"/>
                <path d="M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286m1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94"/>
            </svg>
        </button>
        <script>
            window.document.querySelector("#btn_onboarding").addEventListener('click', () => {
                if (stepbystep) {
                    stepbystep();
                }
            }, false);
        </script>
        <!-- 테마 변경 버튼 -->
        <button id="bd-theme" title="테마 변경" type="button" class="btn btn-outline-primary py-2 dropdown-toggle d-flex align-items-center"
            aria-expanded="false" data-bs-toggle="dropdown" aria-label="Toggle theme (light)">
            <svg class="bi my-1 theme-icon-active" width="1em" height="1em">
                <use href="#sun-fill"></use>
            </svg>
            <span class="visually-hidden" id="bd-theme-text">Toggle theme</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="bd-theme-text">
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

    <div class="container-fluid">
        <div class="row">
            <main class="col-md-12 ms-sm-auto col-lg-12 px-md-4">

                <hr class="mb-3">
                <div class="mb-3">
                    <h2 id="title">신규 등록</h2>
                    <hr class="mb-4">
                    <div class="row">
                        <div id="sns-login" class="col">
                            <script src="https://t1.kakaocdn.net/kakao_js_sdk/2.4.0/kakao.min.js" integrity="sha384-mXVrIX2T/Kszp6Z0aEWaA8Nm7J6/ZeWXbL8UpGRjKwWe56Srd/iyNmWMBhcItAjH" crossorigin="anonymous"></script>
                            <script>
                                Kakao.init('<%=PropertiesUtil.get("KAKAO_JAVASCRIPT_KEY")%>');
                            </script>
                            <a id="kakao-login-btn" href="javascript:loginWithKakao()">
                                <img id="kakao-login-img" src="./res/web/images/kakaotalk_sharing_btn_medium_ov.png" height="45" alt="카카오 로그인 버튼" />
                            </a>
                            <script>
                                function loginWithKakao() {
                                    Kakao.Auth.authorize({
                                        redirectUri: '<%=PropertiesUtil.get("KAKAO_REDIRECT_URI")%>',
                                        state : 'userme',
                                    });
                                }
                            </script>
                            
                            <%
                                String clientId = PropertiesUtil.get("NAVER_CLIENT_ID");
                                String redirectURI = URLEncoder.encode(PropertiesUtil.get("NAVER_REDIRECT_URI"), "UTF-8");
                                SecureRandom random = new SecureRandom();
                                //String state = new BigInteger(130, random).toString();
                                String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                                    + "&client_id=" + clientId
                                    + "&redirect_uri=" + redirectURI;
                                //   + "&state=" + state;
                                //session.setAttribute("state", state);
                            %>
                            <a href="<%=apiURL%>"><img id="naver-login-img"  height="45" src="./res/web/images/btnG_naver.png"/></a>
                        </div>
                    </div>
                    <hr class="mb-4">
                    <div class="row">
                        <div class="col mb-2">
                            <label for="juid">지니박스 아이디(이메일)</label>
                            <input type="email" class="form-control" id="juid" placeholder="*필수" value="" required>
                        </div>
                        <div id="block_juname" class="col">
                            <label for="juname">사용자명</label>
                            <input id="juname" type="text" class="form-control" placeholder="*옵션" value="">
                        </div>
                    </div>
                    <div class="row mb-2">
                        <div class="col">
                            <span id="chk_email" style="font-size: 12px;"></span>
                        </div>
                        <div class="col"></div>
                    </div>
                    <script>
                        const validEmail = function () {// email 유효성을 검사하는 함수 validation
                            const _doc = window.document;
                            const juid = document.querySelector("#juid");
                            const pattern = /^[^·]+@[^·]+\.[a-z]{2,3}$/;   // email 값의 조건

                            if (juid.value.match(pattern)) {
                                // console.debug("validEmail check exist email~~");
                                // 이미 등록된 email 인지 확인
                                var path = "/jbs/user";
                                var params = "cmd=existEmail";
                                params += "&user_email=" + document.querySelector("#juid").value;

                                var _ajax = new XMLHttpRequest();
                                _ajax.onreadystatechange = function () {
                                    if (checkAjaxSuc(_ajax)) {
                                        var resMsg = _ajax.responseText;
                                        if (resMsg.length > 0) {
                                            //alert(resMsg);
                                            let resJo = JSON.parse(resMsg);
                                            //console.debug("resJo.result-"+resJo.result)
                                            let exist_email = resJo.result;
                                            if (exist_email) {
                                                _doc.querySelector("#title").innerHTML = "신규 등록";
                                                _doc.querySelector("#user_set").style.display = "block";
                                                _doc.querySelector("#block_juname").style.display = "block";
                                                _doc.querySelector("#block_jupw_conf").style.display = "block";
                                                _doc.querySelector("#btn_signup").hidden = false;
                                                _doc.querySelector("#btn_update").hidden = true;
                                                _doc.querySelector("#chk_email").innerHTML="* 이미 등록된 이메일입니다.";
                                                juid.className = "form-control text-warning";

                                                if (confirm("기존 계정에 연결하시나요?")) {
                                                    if (_doc.kakao_user) {
                                                        _doc.querySelector("#title").innerHTML = "카카오 계정 연결";
                                                    } else if (_doc.naver_user) {
                                                        _doc.querySelector("#title").innerHTML = "네이버 계정 연결";
                                                    } else {
                                                        _doc.querySelector("#title").innerHTML = "빅스비 계정 연결";
                                                    }
                                                    _doc.querySelector("#user_set").style.display = "none";
                                                    _doc.querySelector("#block_juname").style.display = "none";
                                                    _doc.querySelector("#block_jupw_conf").style.display = "none";
                                                    _doc.querySelector("#btn_signup").hidden = true;
                                                    _doc.querySelector("#btn_update").hidden = false;
                                                    document.querySelector("#chk_email").innerHTML="";
                                                    juid.className = "form-control";
                                                    alert("비밀번호를 입력하여 주세요");
                                                } else {
                                                    if (_doc.kakao_user) {
                                                        _doc.querySelector("#title").innerHTML = "카카오 계정으로 가입";
                                                    } else if (_doc.naver_user) {
                                                        _doc.querySelector("#title").innerHTML = "네이버 계정으로 가입";
                                                    }
                                                }
                                            } else {
                                                _doc.querySelector("#chk_email").innerHTML="";
                                                juid.className = "form-control";
                                            }
                                            _doc.valid_email = !exist_email;
                                        }
                                    }
                                };
                                sendPost(_ajax, path, params);
                            } else {
                                document.querySelector("#chk_email").innerHTML="* 이메일 형식이 올바르지 않습니다.";
                                juid.className = "form-control text-warning";
                                _doc.valid_email = false;
                            }
                        };
                        document.querySelector("#juid").addEventListener("keyup", validEmail);
                    </script>
                    <div class="row mb-3">
                        <div class="col">
                            <label for="jupw">비밀번호</label>
                            <input type="password" class="form-control" id="jupw" value="" required>
                        </div>
                        <div id="block_jupw_conf" class="col">
                            <label for="jupw_confirm">확인</label>
                            <input id="jupw_confirm" type="password" class="form-control" value="" required>
                        </div>
                    </div>
                </div>

                <div id="user_set">
                    <hr class="mb-4">
                    <div class="mb-3">
                        <input class="form-check-input" type="checkbox" id="storeM" checked disabled>
                        <label for="sname">저장소(Store)명을 입력하여 주세요</label>
                        <input type="text" class="form-control" id="sname" placeholder="예) 지니박스">
                        <div class="invalid-feedback">
                            저장소명을 입력해주세요.
                        </div>
                    </div>

                    <div class="mb-3">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" role="switch" id="defaultBoxT" checked disabled>
                            <label class="form-check-label" for="defaultBoxT">기본 보관함(Box) 템플릿 생성</label>
                        </div>
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" role="switch" id="lifeBoxT">
                            <label class="form-check-label" for="lifeBoxT">생활용품 보관함(Box) 템플릿 생성</label>
                        </div>
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" role="switch" id="refrigeBoxT">
                            <label class="form-check-label" for="refrigeBoxT">냉장고 보관함(Box) 템플릿 생성</label>
                        </div>
                    </div>

                    <hr class="mb-4">
                    <div class="mb-3">
                        <input class="form-check-input" type="checkbox" id="storeS">
                        <label for="inviteCode">공유 받은 저장소(Store) 정보</label>
                        <input type="text" class="form-control" id="inviteCode" placeholder="초대코드 입력">
                        <div class="invalid-feedback">
                            저장소명을 입력해주세요.
                        </div>
                    </div>

                    <hr class="mb-4">
                    <div class="custom-control custom-checkbox">
                        <input type="checkbox" class="custom-control-input" id="aggrement" required>
                        <label class="custom-control-label" for="aggrement">개인정보 수집 및 이용에 동의합니다.</label>
                        <button type="button" class="btn btn-link btn-font-size-sm" data-bs-toggle="modal" data-bs-target="#exampleModal">(개인정보 처리방침 내용)</button>
                        <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="exampleModalLabel">개인정보 처리방침</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <p class="card-text">[서비스 제공자]는 [이용자]의 개인정보를 수집, 이용, 보유, 제3자에게 제공하는 것과 관련된 사항을 다음과 같이 공지합니다.</p>
                                        <p class="card-text">[서비스 제공자]는 [서비스]의 이용을 위하여 [이용자]의 개인정보를 수집합니다.</p>
                                        <p class="card-text">[서비스 제공자]는 [이용자]의 개인정보를 [서비스]의 제공, [이용자]의 [서비스] 이용에 대한 통계, [서비스]의 개선 및 보안, [이용자]의 [서비스] 이용에 대한 상담 등에 이용합니다.</p>
                                        <p class="card-text">[서비스 제공자]는 [이용자]의 개인정보를 [이용자]의 동의 없이 제3자에게 제공하지 않습니다.</p>
                                        <p class="card-text">[서비스 제공자]는 [이용자]의 개인정보를 [이용자]의 동의 없이 영리 목적으로 이용하지 않습니다.</p>
                                        <p class="card-text">[서비스 제공자]는 [이용자]의 개인정보를 보호하기 위하여 최선을 다합니다.</p>
                                        <p class="card-text">[이용자]는 [서비스 제공자]에 대해 [이용자]의 개인정보의 열람, 정정, 삭제, 처리 정지, 수정, 변경, 삭제, 처리정지, 거부 등의 요구를 할 수 있습니다.</p>
                                        <p class="card-text">[이용자]는 [서비스 제공자]의 개인정보 처리방침에 대한 문의 및 기타 의견이 있는 경우 [서비스 제공자]의 웹사이트에 게시된 연락처로 문의할 수 있습니다.</p>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Close</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3"></div>
                </div>
                <button id="btn_signup" class="btn btn-primary btn-lg btn-block" type="button">등록 신청</button>
                <button id="btn_update" class="btn btn-primary btn-lg btn-block" type="button" hidden>계정 연결</button>

            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>
    
    <script type="text/javascript">
        window.addEventListener('load', () => {
            const _doc = window.document;
            _doc.querySelector("#anchor_store_title").href = "./signin.jsp";
<%
    Object kakaoUser = request.getAttribute("kakao_user");
    Object naverUser = request.getAttribute("naver_user");
    Object clovaUser = request.getAttribute("clova_user");
    if (kakaoUser != null) { //카카오 계정으로 가입
%>
            _doc.kakao_user = <%=kakaoUser.toString()%>;
            _doc.querySelector("#title").innerHTML = "카카오 계정으로 가입";
            _doc.querySelector("#sns-login").style.display = "none";

<%
    } else if (naverUser != null) { //네이버 계정으로 가입
%>
            _doc.naver_user = <%=naverUser.toString()%>;
            _doc.querySelector("#title").innerHTML = "네이버 계정으로 가입";
            _doc.querySelector("#sns-login").style.display = "none";
<%
        if (clovaUser != null) {
%>
            _doc.clova_user = <%=clovaUser.toString()%>;
<%
        }
    } else { //기타 계정으로 가입
%>
            _doc.kakao_user = false;
            _doc.naver_user = false;
            _doc.querySelector("#sns-login").style.display = "block";
<%
    }
    Object userEmail = request.getAttribute("user_email");
    if (userEmail != null) {
%>
            _doc.querySelector("#juid").value = '<%=userEmail.toString()%>';
            validEmail();
<%
    } else {
%>
            //온보딩 가이드
                if (stepbystep) {
					stepbystep();
                }
<%
    }
%>
            _doc.querySelector("#inviteCode").disabled = true;
            _doc.querySelector("#jupw").value = "";
            _doc.querySelector("#juname").value = "";
        }, false);

        document.querySelector("#storeM").addEventListener('click', function (event) {
            let chk_storem = document.querySelector("#storeM").checked;
            document.querySelector("#sname").disabled = !chk_storem;
            document.querySelector("#lifeBoxT").disabled = !chk_storem;
            document.querySelector("#refrigeBoxT").disabled = !chk_storem;
        }, false);

        document.querySelector("#storeS").addEventListener('click', function (event) {
            document.querySelector("#inviteCode").disabled = !(document.querySelector("#storeS").checked);
        }, false);

        document.querySelector("#btn_signup").addEventListener('click', function (event) {
            const _doc = window.document;
            
            let val_juid = document.querySelector("#juid").value;
            let val_jupw = document.querySelector("#jupw").value;
            let val_jupwc = document.querySelector("#jupw_confirm").value;
            let val_juname = document.querySelector("#juname").value;
            let chk_storem = document.querySelector("#storeM").checked;
            let val_sname = document.querySelector("#sname").value;
            let chk_stores = document.querySelector("#storeS").checked;
            let val_icode = document.querySelector("#inviteCode").value;
            let val_agree = document.querySelector("#aggrement").checked;

            if (!val_juid) {
                alert("아이디를 입력해주세요");
                return -1;
            }
            if (!val_jupw) {
                alert("비밀번호를 입력해주세요");
                return -1;
            }
            if (!val_jupwc) {
                alert("비밀번호 확인을 입력해주세요");
                return -1;
            }
            if (val_jupw != val_jupwc) {
                alert("비밀번호 확인이 올바르지 않습니다");
                return -1;
            }
            if (!document.valid_email) {
                alert("이메일을 다시 입력하여 주세요");
                return -1;
            }
            if (chk_storem && !val_sname) {
                alert("스토어명을 입력해주세요");
                return -1;
            }
            if (chk_stores && !val_icode) {
                alert("초대 받은 코드를 입력해주세요");
                return -1;
            }
            if (!val_agree) {
                alert("개인정보 수집 및 이용에 동의하여 주세요");
                return -1;
            }

            var path = "/jbs/user";
            var params = "cmd=regist";
            if (urlparam("buid")) params += "&buid=" + urlparam("buid");
            params += "&authcode=" + urlparam("authcode");
            params += "&juid=" + val_juid;
            params += "&jupw=" + val_jupw;
            params += "&juname=" + val_juname;
            params += "&sname=" + val_sname;
            params += "&storem=" + chk_storem; //자신의 저장소 생성 여부
            params += "&def_box_add=" + document.querySelector("#defaultBoxT").checked;
            params += "&life_box_add=" + document.querySelector("#lifeBoxT").checked;
            params += "&refrige_box_add=" + document.querySelector("#refrigeBoxT").checked;
            params += "&stores=" + chk_stores; //다른이로부터 저장소 초대 여부
            params += "&icode=" + val_icode;

            attachSpinnerButton(_doc.querySelector("#btn_signup"));

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        let alertMsg = resJo.msg;
                        if (resJo.code == '000') {
                            if (_doc.clova_user) {
                                alertMsg = "회원 가입에 성공하였습니다.\n현재 창을 닫고 클로바에서 다시 호출해 주세요";
                                window.location.href = "/jbs/confirm_close.jsp";
                            } else {
                                window.location.href = "/jbs/signin.jsp";
                            }
                        }
                        removeSpinnerButton(_doc.querySelector("#btn_signup"), "등록 신청");
                        alert(alertMsg);
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        document.querySelector("#btn_update").addEventListener('click', function (event) {

            const _doc = window.document;
            let val_juid = _doc.querySelector("#juid").value;
            let val_jupw = _doc.querySelector("#jupw").value;

            if (!val_juid) {
                alert("아이디를 입력해주세요");
                return -1;
            }
            if (!val_jupw) {
                alert("비밀번호를 입력해주세요");
                return -1;
            }

            var path = "/jbs/bixby";
            var params = "cmd=update_account";
            
            if (_doc.kakao_user) {
                path = "/jbs/kakao";
            } else if (_doc.naver_user) {
                path = "/jbs/naver";
            } else if (urlparam("buid")) {
                params += "&buid=" + urlparam("buid");
            }

            params += "&juid=" + val_juid;
            params += "&jupw=" + val_jupw;

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        if (resJo.code == '000') {
                            window.location.href = "/jbs/list_box.jsp";
                        } else if (resJo.code == '100') {
                            _doc.querySelector("#juid").disabled = true;
                            _doc.querySelector("#jupw").disabled = true;
                            window.location.href = "/jbs/confirm_close.jsp";
                        }
                        alert(resJo.msg);
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>