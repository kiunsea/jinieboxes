<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.omnibuscode.auth.AuthManager" %>
<%@ page import="com.omnibuscode.base.UserSession" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    boolean is_partner = false;
    boolean isMyStore = false;
    UserSession us = AuthManager.getInstance().getUserSession(request);
    if (us != null) {
        is_partner = us.isPartner();
        String seqCurStore = request.getParameter("seq_store");
        isMyStore = us.isOwner(seqCurStore);
    }

    String quickBtnList = "btn_list_top,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="store_info" />
        <jsp:param name="title" value="지니박스 저장소(Store) 정보" />
        <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
    </jsp:include>
</head>

<body>
    <jsp:include page="./include/main_menu.jsp">
        <jsp:param name="is_partner" value="<%= is_partner %>" />
    </jsp:include>
    <div class="container-fluid">
        <div class="row">
            <main class="col-md-12 ms-sm-auto col-lg-12 px-md-4">
                <jsp:include page="./include/quick_buttons.jsp">
                    <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
                </jsp:include>
                <hr class="mb-3">
                <div class="container">
                    <h2>저장소(Store) 정보</h2>
                    <div class="row" id="card-row">
                        <div class="col-sm-12 mb-3 mb-sm-3">
                            <div id="card" class="card">
                                <img src="/jbs/res/web/images/store_pink.jpeg" class="card-img-top" alt="shopping_items">
                                <div class="card-body">
                                    <h5 id="card-title" class="card-title">Store Title</h5>
                                    <p id="card-text" class="card-text">저장소(Store) 정보입니다.</p>
                                </div>

                                <hr id="setting_hr" class="mb-3">
                                <div id="setting_div" class="card-body">
                                    <h5 class="card-title">설정</h5>
                                    <div class="mb-3">
                                        <div class="container">
                                            <div class="row">
                                                <div class="col">
                                                    <div class="form-check">
                                                        <input id="img_use" class="form-check-input" type="checkbox" value="">
                                                        <label class="form-check-label" for="img_use">
                                                            박스와 아이템 이미지 사용
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row ms-3">
                                                <div class="col">
                                                    <div class="form-check">
                                                        <input name="flexRadioDefault" id="storage_google" value="google" class="form-check-input" type="radio">
                                                        <label class="form-check-label" for="storage_google">
                                                            파일을 개인 구글 드라이브에 저장
                                                        </label>
                                                        <div class="input-group">
                                                            <input id="gmail" type="text" class="form-control" placeholder="계정 입력" value="">
                                                            <button id="btn_get_token" class="btn btn-primary btn-sm btn-block" type="button">연결</button>
                                                        </div>                                                        
                                                    </div>
                                                </div>
                                            </div>
<% if (is_partner) { %>
                                            <div id="storage_jbs_block" class="row ms-3">
                                                <div class="col">
                                                    <div class="form-check">
                                                        <input name="flexRadioDefault" id="storage_jbs" value="jbs" class="form-check-input" type="radio">
                                                        <label class="form-check-label" for="storage_jbs">
                                                            파일을 지니박스 스토리지에 저장
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
<% } %>
                                            <script type="text/javascript">
                                                window.document.querySelector("#img_use").addEventListener('change', () => {
                                                    const _doc = window.document;
                                                    setImguse();                                            
                                                }, false);

                                                window.document.querySelector("#storage_google").addEventListener('click', () => {
                                                    const _doc = window.document;

                                                    if (_doc.querySelector("#img_use").checked) {
                                                        var gauthcd = _doc.store.gauthcd;
                                                        if (!(gauthcd && gauthcd == "000")) { //구글 인증 결과가 실패라면
                                                            _doc.querySelector("#gmail").removeAttribute("disabled");
                                                            _doc.querySelector("#gmail").removeAttribute("readonly");
                                                            _doc.querySelector("#storage_google").checked = false;
                                                            alert("구글 계정 연결이 필요합니다");
                                                            return -1;
                                                        }
                                                    } else {
                                                        alert("이미지 사용 체크박스를 체크해 주세요");
                                                        _doc.querySelector("#storage_google").checked = false;
                                                        return -1;
                                                    }

                                                    setImguse();

                                                }, false);
<% if (is_partner) { %>
                                                window.document.querySelector("#storage_jbs").addEventListener('click', () => {
                                                    const _doc = window.document;

                                                    if (!_doc.querySelector("#img_use").checked) {
                                                        alert("이미지 사용 체크박스를 체크해 주세요");
                                                        _doc.querySelector("#storage_jbs").checked = false;
                                                        return -1;
                                                    }

                                                    setImguse();

                                                }, false);
<% } %>
                                                function setImguse() {
                                                    const _doc = window.document;

                                                    var chk_iu = _doc.querySelector("#img_use").checked;
                                                    var chk_radio = _doc.querySelector('input[name="flexRadioDefault"]:checked');

                                                    if (chk_iu && !chk_radio) {
                                                        alert("이미지 파일 저장 위치를 선택해 주세요");
                                                        return -1;
                                                    }

                                                    if (_doc.querySelector("#storage_google").checked) {
                                                        var gauthcd = _doc.store.gauthcd;
                                                        if (!(gauthcd && gauthcd == "000")) { //구글 인증 결과가 실패라면
                                                            _doc.querySelector("#gmail").removeAttribute("disabled");
                                                            _doc.querySelector("#gmail").removeAttribute("readonly");
                                                            _doc.querySelector("#storage_google").checked = false;
                                                            alert("구글 계정 연결이 필요합니다");
                                                            return -1;
                                                        }
                                                    }

                                                    var path = "/jbs/store";
                                                    var params = "cmd=saveSetting"
                                                    params += "&img_use=" + chk_iu;
                                                    params += "&storage_type=" + (chk_radio ? chk_radio.value : "");
                                                    
                                                    var _ajax = new XMLHttpRequest();
                                                    _ajax.onreadystatechange = function () {
                                                        if (checkAjaxSuc(_ajax)) {
                                                            var resMsg = _ajax.responseText;
                                                            if (resMsg.length > 0) {
                                                                let resJson = JSON.parse(resMsg);
                                                                var alertMsg = null;
                                                                if (resJson.code == "000") {
                                                                //alertMsg = resJson.msg;
                                                                } else {
                                                                    alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                                }
                                                                if (alertMsg) {
                                                                    alert(alertMsg);
                                                                }
                                                            }
                                                        }
                                                    };
                                                    sendPost(_ajax, path, params);
                                                }

                                                window.document.querySelector("#btn_get_token").addEventListener('click', () => {
                                                    const _doc = window.document;
                    
                                                    if (_doc.store.img_use == 1 && _doc.store.storage_type == 1) {
                                                        //연결해제
                                                        let path = "/jbs/gcp";
                                                        let params = "cmd=releaseGmail";
                                                        
                                                        let _ajax = new XMLHttpRequest();
                                                        _ajax.onreadystatechange = function () {
                                                            if (checkAjaxSuc(_ajax)) {
                                                                let resMsg = _ajax.responseText;
                                                                if (resMsg.length > 0) {
                                                                    let resJson = JSON.parse(resMsg);
                                                                    let alertMsg = null;
                                                                    if (resJson.code == "000") {
                                                                        alertMsg = resJson.msg;
                                                                        _doc.querySelector("#gmail").removeAttribute("disabled");
                                                                        _doc.querySelector("#gmail").removeAttribute("readonly");
                                                                        _doc.querySelector("#btn_get_token").innerText = "연결";
                                                                        _doc.querySelector("#img_use").checked = false;
                                                                    } else {
                                                                        alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                                    }
                                                                    if (alertMsg) {
                                                                        alert(alertMsg);
                                                                    }
                                                                }
                                                            }
                                                        };            
                                                        sendPost(_ajax,
                                                            path,
                                                            params);
                                                    } else {
                                                        //구글연결
                                                        let gmail = _doc.querySelector("#gmail").value;
                                                        const pattern = /^[^·]+@[^·]+\.[a-z]{2,3}$/;   // email 값의 조건
                                                        if (gmail.length < 3 || !gmail.match(pattern)) {
                                                            alert("구글 이메일 계정을 입력해주세요");
                                                            return -1;
                                                        }
                        
                                                        let path = "/jbs/gcp";
                                                        let params = "cmd=getUriForAuth"
                                                        params += "&storage_type=1";
                                                        params += "&gmail=" + gmail;
                                                        
                                                        let _ajax = new XMLHttpRequest();
                                                        _ajax.onreadystatechange = function () {
                                                            if (checkAjaxSuc(_ajax)) {
                                                                let resMsg = _ajax.responseText;
                                                                if (resMsg.length > 0) {
                                                                    let resJson = JSON.parse(resMsg);
                                                                    let alertMsg = null;
                                                                    if (resJson.code == "000") {
                                                                        window.open(resJson.gauthUri, '_blank');
                                                                    } else {
                                                                        alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                                    }
                                                                    if (alertMsg) {
                                                                        alert(alertMsg);
                                                                    }
                                                                }
                                                            }
                                                        };            
                                                        sendPost(_ajax,
                                                            path,
                                                            params);
                                                    }
                                                }, false);
                                                
                                            </script>
                                            
                                            <%-- <div class="row">
                                                <div class="col d-flex align-items-center">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-asterisk" viewBox="0 0 16 16">
                                                        <path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732l-4.562 2.634a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1"/>
                                                    </svg>&nbsp;&nbsp;
                                                    <label class="form-check-label">
                                                        등록대기 보관함에서 아이템 저장 일수
                                                    </label>
                                                </div>
                                            </div> --%>
                                        </div>
                                    </div>
                                </div>

                                <hr class="mb-3">
                                <div class="card-body">
                                    <!-- <ul class="list-group list-group-flush">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">An item<span
                                                class="badge bg-primary rounded-pill">14</span></li>
                                        <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span
                                                class="badge bg-primary rounded-pill">2</span></li>
                                        <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span
                                                class="badge bg-primary rounded-pill">1</span></li>
                                    </ul> -->
                                    <h5 class="card-title">보관함(박스) 목록</h5>
                                    <div id="box-list"></div>
<% if(is_partner) { %>
                                    <h5 class="card-title">나눔함 목록</h5>
                                    <div id="nanum-list"></div>
<% } %>
                                    <button id="btn_box_add" type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#showAddBoxModal">추가</button>
                                    <input type="text" id="modal_box_mode" value="ADD" hidden>
                                    <input type="text" id="modal_box_seq" hidden>
                                    <input type="text" id="modal_box_type" value="box" hidden>
                                    <div id="showAddBoxModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addBoxModal" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
<% if(is_partner) { %>
                                                        <h5 class="modal-title" id="addBoxModal">보관함/나눔함 추가</h5>
<% } else {%>
                                                        <h5 class="modal-title" id="addBoxModal">보관함 추가</h5>
<% } %>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">타입</span>
                                                        <select id="abmd_select_type" class="form-select">
                                                            <option value="box" selected>보관함</option>
<% if(is_partner) { %>
                                                            <option value="nanum">나눔함</option>
<% } %>
                                                        </select>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">이름</span>
														<input id="abmd_input_name" type="text" class="form-control">
                                                    </div>
                                                    <div id="abmd_div_share" class="input-group mb-2" hidden>
                                                        <span class="input-group-text">공유</span>
                                                        <select id="abmd_select_share" class="form-select">
                                                            <option value="F" selected>친구</option>
                                                            <option value="M">회원</option>
                                                            <option value="O">오픈</option>
                                                        </select>
                                                    </div>
                                                    <div id="abmd_div_accesscd" class="input-group mb-2">
                                                        <span class="input-group-text">암호</span>
														<input id="abmd_input_accesscd" type="text" class="form-control" placeholder="필수 옵션입니다">
                                                    </div>
													<div id="abmd_div_url" class="input-group mb-2">
                                                        <span class="input-group-text">주소</span>
														<input id="abmd_input_url" type="text" class="form-control user-select-all" readonly>
                                                        <span class="input-group-text" onclick="javascript:regenurl('abmd_input_url')">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-repeat" viewBox="0 0 16 16">
                                                                <path d="M11 5.466V4H5a4 4 0 0 0-3.584 5.777.5.5 0 1 1-.896.446A5 5 0 0 1 5 3h6V1.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384l-2.36 1.966a.25.25 0 0 1-.41-.192Zm3.81.086a.5.5 0 0 1 .67.225A5 5 0 0 1 11 13H5v1.466a.25.25 0 0 1-.41.192l-2.36-1.966a.25.25 0 0 1 0-.384l2.36-1.966a.25.25 0 0 1 .41.192V12h6a4 4 0 0 0 3.585-5.777.5.5 0 0 1 .225-.67Z"/>
                                                            </svg>
                                                        </span>
                                                        <script>
                                                            function regenurl(input) {
                                                                const _doc = window.document;

                                                                var path = "/jbs/nanum";
                                                                var params = "cmd=newsc";
                                                                params += "&buid=" + urlparam("buid");
                                                                params += "&authcode=" + urlparam("authcode");
                                                                params += "&seq_box=" + _doc.querySelector("#modal_box_seq").value;

                                                                var _ajax = new XMLHttpRequest();
                                                                _ajax.onreadystatechange = function () {
                                                                    if (checkAjaxSuc(_ajax)) {
                                                                        var resMsg = _ajax.responseText;
                                                                        if (resMsg.length > 0) {
                                                                            //alert(resMsg);
                                                                            let newsc = JSON.parse(resMsg)["newsc"];
                                                                            if (newsc && newsc.length > 0) {
                                                                                let page_path = window.location.origin + window.location.pathname;
                                                                                let lstidx = page_path.lastIndexOf("/");
                                                                                let share_url = page_path.substring(0, lstidx) + "/nanum?scd=" + newsc;
                                                                                _doc.querySelector("#abmd_input_url").value = share_url;
                                                                                alert("공유주소가 갱신되었습니다.");
                                                                            }
                                                                        }
                                                                    }
                                                                };
                                                                sendPost(_ajax, path, params);
                                                            }
                                                        </script>
                                                        <span class="input-group-text" onclick="javascript:copyToClipboard('abmd_input_url')">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-copy" viewBox="0 0 16 16">
                                                                <path fill-rule="evenodd" d="M4 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V2Zm2-1a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H6ZM2 5a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-1h1v1a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h1v1H2Z"></path>
                                                            </svg>
                                                        </span>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">설명</span>
														<textarea class="form-control" aria-label="BOX Detail" id="abmd_input_details"></textarea>
                                                    </div>
                                                    <input class="form-check-input" type="checkbox" id="abmd_add_continue" checked>
                                                    <label for="abmd_add_continue" id="abmd_add_cont_label">계속 입력</label>
                                                </div>
                                                <div class="modal-footer">
                                                    <button id="abmd_btn_add" type="button" class="btn btn-primary">추가</button>
                                                    <button id="abmd_btn_mod" type="button" class="btn btn-primary">수정</button>
                                                    <button id="abmd_btn_del" type="button" class="btn btn-primary">삭제</button>
                                                    <button id="abmd_btn_cancel" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <script>
                                        window.document.querySelector("#abmd_input_url").addEventListener('focus', () => {
                                            window.document.querySelector("#abmd_input_url").select();
                                        }, false);
                                        /**
                                         * 박스 추가 팝업
                                         */
                                        window.document.querySelector("#btn_box_add").addEventListener('click', () => {
                                            const _doc = window.document;
                                            _doc.querySelector("#modal_box_mode").value = "ADD";
                                            _doc.querySelector("#modal_box_seq").value = "";
<% if (is_partner) { %>
                                            _doc.querySelector("#addBoxModal").innerText = "보관함/나눔함 추가";
<% } else { %>
                                            _doc.querySelector("#addBoxModal").innerText = "보관함 추가";
<% } %>
                                            _doc.querySelector("#abmd_input_accesscd").value = "";
                                            _doc.querySelector("#abmd_input_name").value = "";
                                            _doc.querySelector("#abmd_input_details").value = "";
                                            _doc.querySelector("#abmd_add_continue").checked = true;
                                            _doc.querySelector("#abmd_btn_add").hidden = false;
                                            _doc.querySelector("#abmd_btn_mod").hidden = true;
											_doc.querySelector("#abmd_btn_del").hidden = true;
                                            _doc.querySelector("#abmd_add_continue").hidden = false;
                                            _doc.querySelector("#abmd_add_cont_label").hidden = false;
                                            _doc.querySelector("#abmd_select_type").disabled = false;
                                            _doc.querySelector("#abmd_div_accesscd").hidden = true;
                                            _doc.querySelector("#abmd_div_url").hidden = true;
                                            showShareSelectInput();
                                        }, false);
                                        /**
                                         * 팝업 형태 선택
                                         */
                                        window.document.querySelector("#abmd_select_type").addEventListener('change', () => {
                                            showShareSelectInput();
                                        }, false);
                                        /**
                                         * 팝업 형태에 따라 입력 보이기
                                         */
                                        function showShareSelectInput() {
                                            const _doc = window.document;
                                            if (_doc.querySelector("#abmd_select_type").value == 'nanum') {
                                                _doc.querySelector("#abmd_div_share").hidden = false;
                                                _doc.querySelector("#abmd_div_accesscd").hidden = false;
                                                // showElement(_doc.querySelector("#abmd_div_share"));
                                                // showElement(_doc.querySelector("#abmd_div_accesscd"));
                                                // hideElement(_doc.querySelector("#abmd_input_settings"));
                                                // _doc.querySelector("#abmd_input_settings").hidden = true;
                                                showAccesscodeInput();
                                            } else {
                                                _doc.querySelector("#abmd_div_share").hidden = true;
                                                _doc.querySelector("#abmd_div_accesscd").hidden = true;
                                                // hideElement(_doc.querySelector("#abmd_div_share"));
                                                // hideElement(_doc.querySelector("#abmd_div_accesscd"));
                                                // showElement(_doc.querySelector("#abmd_input_settings"));
                                                // _doc.querySelector("#abmd_input_settings").hidden = false;
                                            }
                                        }
                                        /**
                                         * 공유 형태 선택
                                         */
                                        window.document.querySelector("#abmd_select_share").addEventListener('change', () => {
                                            showAccesscodeInput();
                                        }, false);
                                        /**
                                         * 공유 형태에 따라 입력 힌트 출력
                                         */
                                        function showAccesscodeInput() {
                                            const _doc = window.document;

                                            let access_level = _doc.querySelector("#abmd_select_share").value;
                                            let strPh = "필수 옵션입니다";
                                            if (access_level == 'F') {
                                                strPh = "필수 옵션입니다";
                                            } else {
                                                strPh = "선택 옵션입니다";
                                            }
                                            _doc.querySelector("#abmd_input_accesscd").placeholder = strPh;
                                        }
                                        /**
                                         * 박스 추가
                                         */
                                        window.document.querySelector("#abmd_btn_add").addEventListener('click', () => {

                                            const _doc = window.document;

                                            let box_name = _doc.querySelector("#abmd_input_name").value;
                                            if (!box_name) {
                                                alert("박스 이름이 입력되지 않았습니다");
                                                return -1;
                                            }

                                            if (_doc.querySelector("#abmd_select_type").value == 'nanum') {
                                                let access_level = _doc.querySelector("#abmd_select_share").value;
                                                let accesscd = _doc.querySelector("#abmd_input_accesscd").value;                                            
                                                if (access_level == 'F' && !accesscd) {
                                                    alert("암호(접속코드)는 필수입니다.");
                                                    _doc.querySelector("#abmd_input_accesscd").focus();
                                                    return -1;
                                                }
                                            }

                                            var path = "/jbs/box";
                                            if (_doc.querySelector("#abmd_select_type").value == 'nanum') {
                                                path = "/jbs/nanum";
                                            }
                                            var params = "cmd=add";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&seq_store=" + urlparam("seq_store");
                                            params += "&box_share=" + _doc.querySelector("#abmd_select_share").value;
                                            params += "&box_acd=" + _doc.querySelector("#abmd_input_accesscd").value;
                                            params += "&box_name=" + box_name;
                                            params += "&box_details=" + _doc.querySelector("#abmd_input_details").value;

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        var alertMsg = null;
                                                        if (resJson.code == "000") {
                                                            //팝업창 초기화
                                                            _doc.querySelector("#abmd_input_accesscd").value = "";
                                                            _doc.querySelector("#abmd_input_name").value = "";
                                                            _doc.querySelector("#abmd_input_details").value = "";
                                                            updateBoxList(resJson);
                                                            if (!_doc.querySelector("#abmd_add_continue").checked) {
                                                                bootstrap.Modal.getOrCreateInstance(document.getElementById('showAddBoxModal')).hide();
                                                            }
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        if (alertMsg)
                                                            alert(alertMsg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        /**
                                        * 박스 수정 팝업
                                        */
                                        function boxModify(type, seq) {
                                            const _doc = window.document;
<% if (is_partner) { %>
                                            _doc.querySelector("#addBoxModal").innerText = "보관함/나눔함 수정";
<% } else { %>
                                            _doc.querySelector("#addBoxModal").innerText = "보관함 수정";
<% } %>
                                            _doc.querySelector("#abmd_btn_add").hidden = true;
                                            _doc.querySelector("#abmd_btn_mod").hidden = false;
											_doc.querySelector("#abmd_btn_del").hidden = false;
                                            _doc.querySelector("#abmd_add_continue").hidden = true;
                                            _doc.querySelector("#abmd_add_cont_label").hidden = true;
                                            _doc.querySelector("#abmd_select_type").disabled = true;

                                            var box = _doc.store.boxes[seq];
                                            var nanum = _doc.store.nanums[seq];
                                            _doc.querySelector("#modal_box_mode").value = "MODIFY";
                                            _doc.querySelector("#modal_box_type").value = type;
                                            _doc.querySelector("#abmd_select_type").value = type;
                                            if (type == 'box') {
                                                _doc.querySelector("#abmd_div_accesscd").hidden = true;
                                                _doc.querySelector("#abmd_div_url").hidden = true;
                                                _doc.querySelector("#modal_box_seq").value = box.seq;
                                                _doc.querySelector("#abmd_input_name").value = box.name;
                                                _doc.querySelector("#abmd_input_details").value = box.details;
                                            } else if (type == 'nanum') {
                                                _doc.querySelector("#abmd_div_accesscd").hidden = false;
                                                _doc.querySelector("#abmd_div_url").hidden = false;
                                                _doc.querySelector("#modal_box_seq").value = nanum.seq;
                                                _doc.querySelector("#abmd_select_share").value = nanum.access_level;
                                                _doc.querySelector("#abmd_input_accesscd").value = nanum.access_code;
                                                _doc.querySelector("#abmd_input_name").value = nanum.name;
                                                _doc.querySelector("#abmd_input_details").value = nanum.details;
                                                if (nanum.share_code) {
                                                    let page_path = window.location.origin + window.location.pathname;
                                                    let lstidx = page_path.lastIndexOf("/");
                                                    let share_url = page_path.substring(0, lstidx) + "/nanum?scd=" + nanum.share_code;
                                                    _doc.querySelector("#abmd_input_url").value = share_url;
                                                } else {
                                                    _doc.querySelector("#abmd_input_url").value = "";
                                                }
                                            }
                                            showShareSelectInput();
                                        }
                                        /**
                                         * 박스 수정
                                         */
                                        window.document.querySelector("#abmd_btn_mod").addEventListener('click', () => {

                                            const _doc = window.document;

                                            var path = "/jbs/box";
                                            if (_doc.querySelector("#modal_box_type").value == 'nanum') {
                                                path = "/jbs/nanum";
                                            }
                                            
                                            let access_level = _doc.querySelector("#abmd_select_share").value;
                                            let accesscd = _doc.querySelector("#abmd_input_accesscd").value;
                                            if (_doc.querySelector("#abmd_select_type").value == 'nanum') {
                                                if (access_level == 'F' && !accesscd) {
                                                    alert("암호(접속코드)는 필수입니다.");
                                                    _doc.querySelector("#abmd_input_accesscd").focus();
                                                    return -1;
                                                }
                                            }

                                            var params = "cmd=update";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&seq_store=" + urlparam("seq_store");
                                            params += "&box_share=" + access_level;
                                            let seq_box = _doc.querySelector("#modal_box_seq").value;
                                            if (seq_box) {
                                                params += "&seq_box=" + seq_box;
                                            } else {
                                                alert("박스가 선택되지 않았습니다");
                                                return -1;
                                            }
                                            params += "&box_acd=" + accesscd;
                                            params += "&box_name=" + _doc.querySelector("#abmd_input_name").value;
                                            params += "&box_details=" + _doc.querySelector("#abmd_input_details").value;
                                            if (!confirm("수정하시겠습니까?")) {
                                                return -1;
                                            }
                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        var alertMsg = null;
                                                        if (resJson.code == "000") {
                                                            _doc.querySelector("#abmd_input_name").value = "";
                                                            _doc.querySelector("#abmd_input_details").value = "";
                                                            updateBoxList(resJson);
                                                            bootstrap.Modal.getOrCreateInstance(document.getElementById('showAddBoxModal')).hide();
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        if (alertMsg)
                                                            alert(alertMsg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        /**
                                         * 박스 삭제
                                         */
                                        window.document.querySelector("#abmd_btn_del").addEventListener('click', () => {
                                            const _doc = window.document;

                                            var path = "/jbs/box";
                                            if (_doc.querySelector("#modal_box_type").value == 'nanum') {
                                                path = "/jbs/nanum";
                                            }
                                            var params = "cmd=delete";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&seq_store=" + urlparam("seq_store");
                                            let seq_box = _doc.querySelector("#modal_box_seq").value;
                                            if (seq_box) {
                                                params += "&seq_box=" + seq_box;
                                            } else {
                                                alert("박스가 선택되지 않았습니다");
                                                return -1;
                                            }
                                            if (!confirm("선택한 박스를 삭제하시겠습니까?")) {
                                                return -1;
                                            }
                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        var alertMsg = null;
                                                        if (resJson.code == "000") {
                                                            updateBoxList(resJson);
                                                            bootstrap.Modal.getOrCreateInstance(document.getElementById('showAddBoxModal')).hide();
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        if(alertMsg)
                                                            alert(alertMsg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                    </script>
									<div class="btn-group" role="group">
                                        <button type="button" class="btn btn-primary dropdown-toggle"
                                            data-bs-toggle="dropdown" aria-expanded="false" id="btn_box_batch">
                                            선택박스만
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a class="dropdown-item" data-bs-toggle="modal"
                                                    data-bs-target="#showEditBoxesModal">속성 변경</a></li>
                                            <li><a class="dropdown-item" href="javascript:deleteBoxes()">삭제</a></li>
                                        </ul>
                                    </div>
                                    <div id="showEditBoxesModal" class="modal fade" tabindex="-1" role="dialog"
                                        aria-labelledby="editBoxesModal" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title" id="editBoxesModal">속성 변경</h5>
                                                        <h6 class="text-danger" id="ebmd_check_msg"></h6>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal"></button>
                                                </div>
                                                <form action="/jbs/box?cmd=batch" method="post">
                                                    <div class="modal-body">
                                                        <div class="input-group mb-2">
                                                            <input type="checkbox" class="custom-control-input" name="ebmd_apply" value="to_store">&nbsp;
                                                            <span class="input-group-text">저장소</span>
                                                            <select class="form-select" aria-label="Default select example" id="ebmd_to_store">
                                                                <!--option selected>Open this select menu</option>
																<option value="1">One</option>
																<option value="2">Two</option>
																<option value="3">Three</option-->
                                                            </select>
                                                        </div>                                                        														
														<div class="input-group mb-2">
															<input type="checkbox" class="custom-control-input"
                                                                name="ebmd_apply" value="input_detail">&nbsp;
															<span class="input-group-text">설명</span>
															<textarea class="form-control" aria-label="BOX Detail" id="ebmd_input_detail"></textarea>
														</div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-primary"
                                                            id="ebmd_btn_apply">적용</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            id="ebmd_btn_cancel" data-bs-dismiss="modal">닫기</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    <script>
                                        window.document.querySelector("#btn_box_batch").addEventListener('click', () => {
                                            let selectedBoxes = window.document.querySelectorAll('input[name="boxes[]"]:checked');
                                            let checkMsg = window.document.querySelector("#ebmd_check_msg");
                                            if (selectedBoxes.length < 1) {
                                                checkMsg.className = "text-danger";
                                                checkMsg.innerText = "선택한 박스가 없습니다";
                                            } else {
                                                checkMsg.className = "text-primary";
                                                checkMsg.innerText = selectedBoxes.length + "개의 박스가 선택되었습니다";
                                                checkMsg.innerText += "\n변경할 속성을 체크하여 적용하세요";
                                            }
                                        }, false);
										
                                        window.document.querySelector("#ebmd_btn_apply").addEventListener('click', () => {
											const _doc = window.document;
                                            let selectedBoxes = window.document.querySelectorAll('input[name="boxes[]"]:checked');
                                            let selectedEbmdAttrs = window.document.querySelectorAll('input[name="ebmd_apply"]:checked');
                                            if (selectedBoxes.length > 0) {
                                                if (selectedEbmdAttrs.length > 0) {

                                                    var path = "/jbs/box";
                                                    var params = "cmd=batch";
                                                    params += "&buid=" + urlparam("buid");
                                                    params += "&authcode=" + urlparam("authcode");
                                                    params += "&seq_store=" + urlparam("seq_store");

                                                    let boxes = "";
                                                    for (selBox of selectedBoxes) {
                                                        boxes += selBox.value + ",";
                                                    }
                                                    params += "&boxes=" + boxes;
                                                    for (selAttr of selectedEbmdAttrs) {
                                                        let pNm = "ebmd_" + selAttr.value;
                                                        let pVl = window.document.querySelector("#" + pNm).value;
                                                        params += "&" + pNm + "=" + pVl;
                                                    }

                                                    var _ajax = new XMLHttpRequest();
                                                    _ajax.onreadystatechange = function () {														
                                                        if (checkAjaxSuc(_ajax)) {
                                                            let resMsg = _ajax.responseText;
                                                            if (resMsg.length > 0) {
                                                                let resJson = JSON.parse(resMsg);
                                                                if (resJson.code == "000") {
																	//팝업창 초기화
																	_doc.querySelector("#ebmd_input_detail").value = "";
																	for (selAttr of selectedEbmdAttrs) {
																		selAttr.checked = false;
																	}
																	
																	//전체 목록을 갱신
                                                                    if (resJson.boxes) {
																		_doc.store.boxes = resJson.boxes;
                                                                        _doc.querySelector("#box-list").innerHTML = "";
                                                                        let card_outer = _doc.createElement("ul");
                                                                        card_outer.className = "list-group list-group-flush";
                                                                        let keyArr = Object.keys(resJson.boxes);
																		let box = null;
                                                                        for (i = 1; i <= keyArr.length; i++) {
																			box = resJson.boxes[keyArr[i - 1]];
																			addBoxRow(card_outer, box);
                                                                        }
                                                                        _doc.querySelector("#box-list").appendChild(card_outer);
                                                                    }
                                                                    bootstrap.Modal.getOrCreateInstance(_doc.getElementById('showEditBoxesModal')).hide();
                                                                }
                                                                if (resJson.msg)
                                                                    alert(resJson.msg);
                                                            }
                                                        }
                                                    };
                                                    sendPost(_ajax, path, params);

                                                } else {
                                                    alert("변경할 속성이 체크되지 않았습니다");
                                                }
                                            } else {
                                                alert("선택한 박스가 없습니다");
                                            }
                                        }, false);
										
										function deleteBoxes() {
											const _doc = window.document;
										
											var selectedBoxes = _doc.querySelectorAll('input[name="boxes[]"]:checked');
                                            var selectedNanums = _doc.querySelectorAll('input[name="nanums[]"]:checked');
											var cntSelected = selectedBoxes.length + selectedNanums.length;
                                            
                                            if (cntSelected < 1) {
                                                alert("선택한 박스가 없습니다.");
                                                return -1;
                                            }

                                            if (!confirm("선택한 박스를 삭제하시겠습니까?")) {
                                                return -1;
                                            }

                                            var path = "/jbs/store";
                                            var params = "cmd=deleteBoxes";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&seq_store=" + urlparam("seq_store");

											if (selectedBoxes.length > 0) {
                                                let boxes = "";
                                                for (selBox of selectedBoxes) {
                                                    boxes += selBox.value + ",";
                                                }
                                                params += "&boxes=" + boxes;
                                            }
                                            if (selectedNanums.length > 0) {
                                                let nanums = "";
                                                for (selNanum of selectedNanums) {
                                                    nanums += selNanum.value + ",";
                                                }
                                                params += "&nanums=" + nanums;
                                            }

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                
                                                if (checkAjaxSuc(_ajax)) {
                                                    let resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        if (resJson.code == "000") {
                                                            updateBoxList(resJson);
                                                        }
                                                        if (resJson.msg)
                                                            alert(resJson.msg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
										}
                                    </script>
                                </div>
<% if (isMyStore) { %>
                                <hr class="mb-3">
                                <div class="card-body">
                                    <h5 class="card-title">공유</h5>
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th scope="col">#</th>
                                                <th scope="col">아이디</th>
                                                <th scope="col">권한</th>
                                            </tr>
                                        </thead>
                                        <tbody id="share-tbody"></tbody>
                                    </table>
                                </div>
                                <hr class="mb-3">
                                <div class="card-body">
                                    <h5 class="card-title">초대</h5>
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th scope="col">#</th>
                                                <th scope="col">아이디</th>
                                                <th scope="col">권한</th>
                                                <th scope="col">유효일자</th>
                                            </tr>
                                        </thead>
                                        <tbody id="invite-tbody"></tbody>
                                    </table>
                                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#showInviteModal" id="btn_invite_add">추가</button>
                                    <!-- 초대장 팝업 시작 -->
                                    <input type="text" id="modal_inv_seq" hidden>
                                    <input type="text" id="modal_inv_authright" hidden>
                                    <input type="text" id="modal_inv_expire" hidden>
                                    <input type="text" id="modal_inv_updated" hidden>
                                    <input type="text" id="modal_inv_mode" value="ADD" hidden>
                                    <div id="showInviteModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="inviteModalLabel" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title" id="inviteModalLabel">초대장</h5>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal" id="md_inv_btn_close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userid">대상아이디</span><input
                                                            type="text" class="form-control" list="md_inv_id_dl_opts"
                                                            id="md_inv_input_id">
                                                        <datalist id="md_inv_id_dl_opts"></datalist>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userauth">공유권한</span>
                                                        <select class="form-select form-select-sm"
                                                            aria-label=".form-select-sm example" id="md_inv_authority">
                                                            <option value="M" selected>수정가능</option>
                                                            <option value="R">읽기전용</option>
                                                        </select>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">초대코드</span><input type="text"
                                                            class="form-control" placeholder="please generate" disabled
                                                            readonly id="invitecode">
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">초대링크</span>
                                                            <input type="text" class="form-control" placeholder="please generate" readonly id="inviteurl">
                                                        <span class="input-group-text" onclick="javascript:copyToClipboard('inviteurl')">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-copy" viewBox="0 0 16 16">
                                                                <path fill-rule="evenodd" d="M4 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V2Zm2-1a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H6ZM2 5a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-1h1v1a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h1v1H2Z"></path>
                                                            </svg>
                                                        </span>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">유효일자</span><input type="text"
                                                            class="form-control" placeholder="please generate" disabled
                                                            readonly id="expirydate">
                                                    </div>
                                                    <div class="input-group mb-2"><button type="button"
                                                            class="btn btn-primary"
                                                            id="md_inv_btn_gen">Generate</button></div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button id="md_inv_btn_ok" type="button" class="btn btn-primary">수정</button>
                                                    <button id="md_inv_btn_cancel" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- 초대장팝업 끝 -->
                                    <!-- 공유팝업 시작 -->
                                    <input type="text" id="modal_share_seq" hidden>
                                    <input type="text" id="modal_share_authright" hidden>
                                    <input type="text" id="modal_share_expire" hidden>
                                    <input type="text" id="modal_share_updated" hidden>
                                    <input type="text" id="modal_share_mode" value="ADD" hidden>
                                    <div id="showShareModal" class="modal fade" tabindex="-1" role="dialog"
                                        aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title">공유</h5>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal" id="md_share_btn_close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userid">대상아이디</span><input
                                                            type="text" class="form-control" list="md_share_id_dl_opts"
                                                            id="md_share_input_id">
                                                        <datalist id="md_share_id_dl_opts"></datalist>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userauth">공유권한</span>
                                                        <select class="form-select form-select-sm"
                                                            aria-label=".form-select-sm example"
                                                            id="md_share_authority">
                                                            <option value="M" selected>수정가능</option>
                                                            <option value="R">읽기전용</option>
                                                        </select>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button id="md_share_btn_ok" type="button" class="btn btn-primary">수정</button>
                                                    <button id="md_share_btn_cancel" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- 공유팝업 끝 -->
                                    <script>
                                        /**
                                         * 초대 추가 팝업
                                         */
                                        window.document.querySelector("#btn_invite_add").addEventListener('click', () => {
                                            const _doc = window.document;
                                            _doc.querySelector("#md_inv_input_id").removeAttribute("disabled");
                                            _doc.querySelector("#md_inv_input_id").removeAttribute("readonly");
                                            _doc.querySelector("#md_inv_btn_ok").textContent = "초대";
                                            _doc.querySelector("#md_inv_btn_gen").textContent = "Generate";

                                            _doc.querySelector("#modal_inv_mode").value = "ADD";
                                            _doc.querySelector("#modal_inv_seq").value = "";
                                            _doc.querySelector("#md_inv_input_id").value = "";
                                            _doc.querySelector("#md_inv_authority").value = "F";
                                            _doc.querySelector("#invitecode").value = "";
                                            _doc.querySelector("#inviteurl").value = "";
                                            _doc.querySelector("#expirydate").value = "";
                                        }, false);
                                        /**
                                         * 초대 수정 팝업
                                         */
                                        function inviteModify(invSeq) {
                                            const _doc = window.document;
                                            _doc.querySelector("#md_inv_input_id").setAttribute("disabled", "");
                                            _doc.querySelector("#md_inv_input_id").setAttribute("readonly", "");
                                            _doc.querySelector("#modal_inv_updated").value = "false";
                                            _doc.querySelector("#md_inv_btn_ok").textContent = "수정";
                                            _doc.querySelector("#md_inv_btn_gen").textContent = "Regenerate";

                                            var invite = _doc.store.invites[invSeq];
                                            //alert(JSON.stringify(invite));
                                            _doc.querySelector("#modal_inv_mode").value = "MODIFY";
                                            _doc.querySelector("#modal_inv_seq").value = invite.seq;
                                            _doc.querySelector("#md_inv_input_id").value = invite.juid;
                                            _doc.querySelector("#md_inv_authority").value = invite.authority;
                                            _doc.querySelector("#invitecode").value = invite.invite_code;
                                            _doc.querySelector("#inviteurl").value = invite.invite_url;
                                            _doc.querySelector("#expirydate").value = invite.expiry_date;
                                        }
                                        /**
                                         * 공유 수정 팝업
                                         */
                                        function shareModify(shareSeq) {
                                            const _doc = window.document;
                                            _doc.querySelector("#md_share_input_id").setAttribute("disabled", "");
                                            _doc.querySelector("#md_share_input_id").setAttribute("readonly", "");
                                            _doc.querySelector("#modal_share_updated").value = "false";
                                            _doc.querySelector("#md_share_btn_ok").textContent = "수정";

                                            var share = _doc.store.shares[shareSeq];
                                            //alert(JSON.stringify(share));
                                            _doc.querySelector("#modal_share_mode").value = "MODIFY";
                                            _doc.querySelector("#modal_share_seq").value = share.seq;
                                            _doc.querySelector("#md_share_input_id").value = share.juid;
                                            _doc.querySelector("#md_share_authority").value = share.authority;
                                        }
                                        /**
                                         * 공유 수정
                                         */
                                        window.document.querySelector("#md_share_btn_ok").addEventListener('click', () => {
                                            alert("준비중...");
                                        }, false);
                                        /**
                                         * 아이디 검색
                                         */
                                        window.document.md_inv_input_id_prevalue = null;
                                        window.document.querySelector("#md_inv_input_id").addEventListener('keyup', () => {
                                            //console.debug("[debug] keyup~");
                                            const _doc = window.document;
                                            let inputTxt = _doc.querySelector("#md_inv_input_id").value;

                                            if (window.document.md_inv_input_id_prevalue == inputTxt) {
                                                return -1;
                                            } else {
                                                window.document.md_inv_input_id_prevalue = inputTxt;
                                            }

                                            var path = "/jbs/user";
                                            var params = "cmd=predictiveId";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&input_txt=" + inputTxt;

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        //alert(resMsg);
                                                        const _doc = window.document;
                                                        let juids = JSON.parse(resMsg)["juids"];
                                                        if (juids && juids.length > 0) {
                                                            var dataList = _doc.querySelector("#md_inv_id_dl_opts");
                                                            var options = "";
                                                            for (juid of juids) {
                                                                options += "<option value='" + juid + "'>";
                                                            }
                                                            dataList.innerHTML = options;
                                                        }
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        /**
                                         * Generate
                                         */
                                        window.document.querySelector("#md_inv_btn_gen").addEventListener('click', () => {
                                            const _doc = window.document;

                                            if (!_doc.querySelector("#md_inv_input_id").value) {
                                                alert("아이디를 입력해야 합니다");
                                                return -1;
                                            }

                                            var path = "/jbs/invite";
                                            var params = "cmd=generate";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&uid=" + _doc.querySelector("#md_inv_input_id").value;
                                            // params += "&right=" + _doc.querySelector("#md_inv_authority").value;
                                            // params += "&seq_box=" + urlparam("seq_box");

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        //alert(resMsg);
                                                        let invJson = JSON.parse(resMsg);
                                                        const _doc = window.document;
                                                        _doc.querySelector("#invitecode").value = invJson.code;
                                                        _doc.querySelector("#inviteurl").value = invJson.url;
                                                        _doc.querySelector("#expirydate").value = invJson.expire;
                                                    }
                                                }
                                            };

                                            // POST 방식으로 요청시
                                            _ajax.open("POST", path);
                                            _ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                                            try {
                                                _ajax.send(params);
                                            } catch (e) {
                                                if (e.message && e.message.indexOf("0x80004005") > -1) {
                                                    return;
                                                }
                                            }
                                        }, false);
                                        /**
                                         * 초대장 저장
                                         */
                                        window.document.querySelector("#md_inv_btn_ok").addEventListener('click', () => {
                                            const _doc = window.document;

                                            if (!_doc.querySelector("#md_inv_input_id").value) {
                                                alert("아이디를 입력해야 합니다");
                                                return -1;
                                            }

                                            var invites = _doc.store.invites;
                                            if (invites) {
                                                var invite = null;
                                                var keyArr = Object.keys(invites);
                                                for (i = 0; i < keyArr.length; i++) {
                                                    invite = invites[keyArr[i]];
                                                    if (invite.seq_object == urlparam("seq_store")
                                                        && invite.juid == _doc.querySelector("#md_inv_input_id").value) {
                                                        if (!confirm("기존에 저장된 초대장에 반영하시겠습니까?")) {
                                                            return -1;
                                                        }
                                                    }
                                                }
                                            }

                                            var path = "/jbs/invite";
                                            var params = "cmd=save";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&juid=" + _doc.querySelector("#md_inv_input_id").value; //공유 받을 사람
                                            params += "&authority=" + _doc.querySelector("#md_inv_authority").value; // 부여할 자격
                                            params += "&seq_store=" + urlparam("seq_store");
                                            params += "&code=" + _doc.querySelector("#invitecode").value;
                                            params += "&url=" + _doc.querySelector("#inviteurl").value;
                                            params += "&expire=" + _doc.querySelector("#expirydate").value;

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        var alertMsg = null;
                                                        if (resJson.code == "000") {
                                                            _doc.querySelector("#modal_inv_updated").value = "true";
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        if (alertMsg)
                                                            alert(alertMsg);

                                                        if (resJson.code == "000")
                                                            window.location.reload(false);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        window.document.querySelector("#md_inv_btn_close").addEventListener('click', () => {
                                            const _doc = window.document;
                                            if (_doc.querySelector("#modal_inv_updated").value == "true") {
                                                var invSeq = _doc.querySelector("#modal_inv_seq").value;
                                                var invite = _doc.store.invites[invSeq];
                                                invite.authority = _doc.querySelector("#md_inv_authority").value;
                                                invite.expiry_date = _doc.querySelector("#expirydate").value;
                                                _doc.querySelector("#td_invmod_authright_" + invSeq).innerText = _doc.querySelector("#md_inv_authority").value;
                                                _doc.querySelector("#td_invmod_expirydate_" + invSeq).innerText = _doc.querySelector("#expirydate").value;
                                            }
                                        }, false);
                                        window.document.querySelector("#md_inv_btn_cancel").addEventListener('click', () => {
                                            const _doc = window.document;
                                            if (_doc.querySelector("#modal_inv_updated").value == "true") {
                                                var invSeq = _doc.querySelector("#modal_inv_seq").value;
                                                var invite = _doc.store.invites[invSeq];
                                                invite.authority = _doc.querySelector("#md_inv_authority").value;
                                                invite.expiry_date = _doc.querySelector("#expirydate").value;
                                                _doc.querySelector("#td_invmod_authright_" + invSeq).innerText = _doc.querySelector("#md_inv_authority").value;
                                                _doc.querySelector("#td_invmod_expirydate_" + invSeq).innerText = _doc.querySelector("#expirydate").value;
                                            }
                                        }, false);
                                    </script>
                                </div>
<% } %>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script type="text/javascript">
        window.addEventListener('load', () => {

            var path = "/jbs/store";
            var params = "cmd=info";
            params += "&buid=" + urlparam("buid");
            params += "&authcode=" + urlparam("authcode");
            params += "&seq_store=" + urlparam("seq_store");

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        if (resJo.code == '000') {                         
                            window.document.is_partner = resJo.is_partner;   
                            let store = window.document.store = resJo.store;
                            showStoreDetail(store);
                            showStoreSetting(store, resJo.gmail);
							createEbmdStoreList(resJo.stores, store.seq);
                        } else {
                            authFailCheck(resJo.code, resJo.msg);
                        }
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        function showStoreDetail(store) {
            const _doc = window.document;
            _doc.querySelector("#card-title").innerText = store.name;
            if (store.details)
                _doc.querySelector("#card-text").innerText = store.details;
<% if (isMyStore) { %>
            if (store.shares) {
                var keyArr = Object.keys(store.shares);
                for (i = 1; i <= keyArr.length; i++) {
                    addShareRow(_doc.querySelector("#share-tbody"), i, store.shares[keyArr[i - 1]]);
                }
            }
            if (store.invites) {
                var keyArr = Object.keys(store.invites);
                for (i = 1; i <= keyArr.length; i++) {
                    addInviteRow(_doc.querySelector("#invite-tbody"), i, store.invites[keyArr[i - 1]]);
                }
            }
<% } %>
            if (store.boxes) {
                let card_outer = _doc.createElement("ul");
                card_outer.className = "list-group list-group-flush";
				let keyArr = Object.keys(store.boxes);
				let box = null;
                for (i = 1; i <= keyArr.length; i++) {
					box = store.boxes[keyArr[i - 1]];
                    addBoxRow(card_outer, box);
                }
                _doc.querySelector("#box-list").appendChild(card_outer);
            }
<% if (is_partner) { %>
            if (store.nanums) {
                let card_outer = _doc.createElement("ul");
                card_outer.className = "list-group list-group-flush";
				let keyArr = Object.keys(store.nanums);
				let nanum = null;
                for (i = 1; i <= keyArr.length; i++) {
					nanum = store.nanums[keyArr[i - 1]];
                    addNanumRow(card_outer, nanum);
                }
                _doc.querySelector("#nanum-list").appendChild(card_outer);
            }
<% } %>
        }

        function showStoreSetting(store, gmail) {
            const _doc = window.document;

            //화면 초기화
            _doc.querySelector("#gmail").value = gmail;

            if (store.is_owner) { //스토어 소유자만 설정 가능
                _doc.querySelector("#setting_hr").hidden = false;
                _doc.querySelector("#setting_div").hidden = false;
                if (store.img_use == 1) {//이미지 저장 사용 여부
                    _doc.querySelector("#img_use").checked = true;
                } else {
                    _doc.querySelector("#img_use").checked = false;
                }

                if (store.storage_type == 1) {//google drive 사용 여부
                    _doc.querySelector("#storage_google").checked = true;
                } else {
<% if (is_partner) { %>
                    _doc.querySelector("#storage_jbs").checked = true;
<% } %>
                }

                if (store.gauthcd && store.gauthcd == "000") {
                    _doc.querySelector("#gmail").setAttribute("disabled", "");
                    _doc.querySelector("#gmail").setAttribute("readonly", "");
                    _doc.querySelector("#btn_get_token").innerText = "해제";
                } else {
                    _doc.querySelector("#gmail").removeAttribute("disabled");
                    _doc.querySelector("#gmail").removeAttribute("readonly");
                    _doc.querySelector("#btn_get_token").innerText = "연결";
                }

                // if (_doc.is_partner) {
                //     _doc.querySelector("#storage_jbs_block").style.display = "none";
                // } else {
                //     _doc.querySelector("#storage_jbs_block").style.display = "block";
                // }
            } else {
                _doc.querySelector("#setting_hr").hidden = true;
                _doc.querySelector("#setting_div").hidden = true;
            }
        }

        function addShareRow(list_group, cnt, share) {
            const _doc = window.document;
            var elem_tr = _doc.createElement("tr");
            var tr_inner = "<th>" + cnt + "</th>";
            tr_inner += "<td><button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#showShareModal' id='btn_share_mod_" + share.seq + "' onclick='javascript:shareModify(" + share.seq + ")'>" + share.juid + "</button></td>";
            tr_inner += "<td id='td_sharemod_authright_" + share.seq + "'>" + share.authority + "</td>";
            elem_tr.innerHTML = tr_inner;
            list_group.insertBefore(elem_tr, list_group.children[0]);
        }

        function addInviteRow(list_group, cnt, invite) {
            const _doc = window.document;
            var elem_tr = _doc.createElement("tr");
            var tr_inner = "<th>" + cnt + "</th>";
            tr_inner += "<td><button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#showInviteModal' id='btn_invite_mod_" + invite.seq + "' onclick='javascript:inviteModify(" + invite.seq + ")'>" + invite.juid + "</button></td>";
            tr_inner += "<td id='td_invmod_authright_" + invite.seq + "'>" + invite.authority + "</td>";
            tr_inner += "<td id='td_invmod_expirydate_" + invite.seq + "'>" + invite.expiry_date + "</td>";
            elem_tr.innerHTML = tr_inner;
            list_group.insertBefore(elem_tr, list_group.children[0]);
        }

		function addBoxRow(list_group, box) {

            if (box.type == -1) return -1; // 등록대기는 출력하지 않는다.

			const _doc = window.document;

            let seq = box.seq;
            let name = box.name;

            let list_group_box = _doc.createElement("li");
            list_group_box.className = "list-group-item d-flex justify-content-between align-items-center";

            let box_div = _doc.createElement("div");
            box_div.className = "grid gap-2";

            let box_chk = _doc.createElement("input");
            box_chk.type = "checkbox";
            box_chk.className = "custom-control-input";
            box_chk.name = "boxes[]";
            box_chk.value = seq;
            box_div.appendChild(box_chk);
            list_group_box.appendChild(box_div);

            let box_name = _doc.createElement("span");
            box_name.setAttribute("id", "box_" + seq);
            box_name.setAttribute("data-bs-toggle", "modal");
            box_name.setAttribute("data-bs-target", "#showAddBoxModal");
            box_name.innerText = " " + name;
            if (box.type > -1) {
                box_name.addEventListener('click', () => {
                    boxModify('box', seq);
                });
            }
            box_div.appendChild(box_name);
            list_group_box.appendChild(box_div);

            // CHECK 스토어 정보에서 보관함의 아이템 갯수 출력은 무의미
            // let item_cnt = _doc.createElement("span");
            // item_cnt.setAttribute("id", "item_cnt_" + seq);
            // item_cnt.className = "badge bg-primary rounded-pill";
            // item_cnt.innerText = itemcnt;
            // list_group_box.appendChild(item_cnt);

            list_group.appendChild(list_group_box);
		}

		function addNanumRow(list_group, nanum) {
			const _doc = window.document;

            let seq = nanum.seq;
            let name = nanum.name;

            let list_group_nanum = _doc.createElement("li");
            list_group_nanum.className = "list-group-item d-flex justify-content-between align-items-center";

            let nanum_div = _doc.createElement("div");
            nanum_div.className = "grid gap-2";

            let nanum_chk = _doc.createElement("input");
            nanum_chk.type = "checkbox";
            nanum_chk.className = "custom-control-input";
            nanum_chk.name = "nanums[]";
            nanum_chk.value = seq;
            nanum_div.appendChild(nanum_chk);
            list_group_nanum.appendChild(nanum_div);

            let nanum_name = _doc.createElement("span");
            nanum_name.setAttribute("id", "nanum_" + seq);
            nanum_name.setAttribute("data-bs-toggle", "modal");
            nanum_name.setAttribute("data-bs-target", "#showAddBoxModal");
            nanum_name.innerText = " " + name;
            nanum_name.addEventListener('click', () => {
                boxModify('nanum', seq);
            });
            nanum_div.appendChild(nanum_name);
            list_group_nanum.appendChild(nanum_div);

            list_group.appendChild(list_group_nanum);
		}
		
        function updateBoxList(resJson) {
            const _doc = window.document;

            if (resJson.boxes) { // 보관박스 전체 목록을 갱신
                var boxes = _doc.store.boxes = resJson.boxes;
                _doc.querySelector("#box-list").innerHTML = "";
                let card_outer = _doc.createElement("ul");
                card_outer.className = "list-group list-group-flush";
                let keyArr = Object.keys(boxes);
                let box = null;
                for (i = 1; i <= keyArr.length; i++) {
                    box = boxes[keyArr[i - 1]];
                    addBoxRow(card_outer, box);
                }
                _doc.querySelector("#box-list").appendChild(card_outer);
            } else if (resJson.seqBox) { //수정된 보관박스 한개만 변경
                var box = _doc.store.boxes[resJson.seqBox];
                box.name = resJson.boxName;
                box.details = resJson.boxDetails;

                _doc.querySelector("#box_" + resJson.seqBox).innerText = " " + box.name;
            }

            if (resJson.nanums) { // 나눔박스 전체 목록을 갱신
                var nanums = _doc.store.nanums = resJson.nanums;
                _doc.querySelector("#nanum-list").innerHTML = "";
                let card_outer = _doc.createElement("ul");
                card_outer.className = "list-group list-group-flush";
                let keyArr = Object.keys(nanums);
                let nanum = null;
                for (i = 1; i <= keyArr.length; i++) {
                    nanum = nanums[keyArr[i - 1]];
                    addNanumRow(card_outer, nanum);
                }
                _doc.querySelector("#nanum-list").appendChild(card_outer);
            } else if (resJson.seqNanum) { //수정된 나눔박스 한개만 변경
                var nanum = _doc.store.nanums[resJson.seqNanum];
                nanum.name = resJson.nanumName;
                nanum.details = resJson.nanumDetails;

                _doc.querySelector("#nanum_" + resJson.seqNanum).innerText = " " + nanum.name;
            }
        }
		
        function createEbmdStoreList(stores, seqCur) {
            let list_inner = "";
            for (store of stores) {
                if (store.seq == seqCur) {
                    list_inner += "<option value='" + store.seq + "' selected>" + store.name + "</li>";
                } else {
                    list_inner += "<option value='" + store.seq + "'>" + store.name + "</li>";
                }
            }
            window.document.querySelector("#ebmd_to_store").innerHTML = list_inner;
        }
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>