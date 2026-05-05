<%@ page import="com.omnibuscode.auth.AuthManager" %>
<%@ page import="com.omnibuscode.base.UserSession" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    boolean is_partner = false;
    boolean img_use = true;
    
    UserSession us = AuthManager.getInstance().getUserSession(request);
    if (us != null) {
        Object iuObj = us.getDefStoreInfo().get("img_use");
        if (iuObj == null || !"1".equals(iuObj.toString())) {
            img_use = false;
        }
        is_partner = us.isPartner();
    }

    String quickBtnList = "btn_list_top,btn_item_add,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="box_info" />
        <jsp:param name="title" value="지니박스 보관함(Box) 정보" />
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
                    <h2>박스 정보</h2>
                    <div id="card-row-image" class="row">
                        <div class="col-sm-12 mb-3 mb-sm-3">
                            <div id="card" class="card">
<% if (img_use) { %>
                                <!-- 박스 사진 출력 시작 -->
                                <div id="box_loading_imgs"></div>
                                <svg id="box_noimg" class="bd-placeholder-img card-img-top" width="100%" height="130"
                                        xmlns="http://www.w3.org/2000/svg" role="img"
                                        aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice"
                                        focusable="false">
                                    <title>Placeholder</title>
                                    <rect width="100%" height="100%" fill="#868e96"></rect>
                                    <%-- <text x="50%" y="50%" fill="#dee2e6" dy=".3em">사진 없음</text> --%>
                                </svg>
                                <div id="box_imgs" class="carousel slide">
                                    <div id="box_car_indi" class="carousel-indicators">
                                        <!--button type="button" data-bs-target="#box_imgs" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
                                        <button type="button" data-bs-target="#box_imgs" data-bs-slide-to="1" aria-label="Slide 2"></button>
                                        <button type="button" data-bs-target="#box_imgs" data-bs-slide-to="2" aria-label="Slide 3"></button-->
                                    </div>
                                    <div id="box_car_inne" class="carousel-inner">
                                        <!--div class="carousel-item active">
                                            <img src="/107/345.jpg" class="d-block w-100" alt="...">
                                        </div>
                                        <div class="carousel-item">
                                            <img src="/107/234.jpg" class="d-block w-100" alt="...">
                                        </div>
                                        <div class="carousel-item">
                                            <img src="/107/123.png" class="d-block w-100" alt="...">
                                        </div-->
                                    </div>
                                    <div class="card-img-overlay">
                                        <div class="btn-toolbar d-flex justify-content-center">
                                            <button id="btn_addboximg" type="button" class="btn btn-outline-dark"
                                                style="z-index: 1; --bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;"
                                                data-bs-toggle="modal" data-bs-target="#showAddBoxImgsModal">추가</button>&nbsp;
                                            <button id="btn_delboximg" type="button" class="btn btn-outline-dark"
                                                style="z-index: 1; --bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;"
                                                onclick="javascript:deleteBoxImage()">삭제</button>
                                        </div>
                                    </div>
                                    <button id="carousel-ctrl-prev-btn-box" class="carousel-control-prev" type="button" data-bs-target="#box_imgs" data-bs-slide="prev" style="display:none;" hidden>
                                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Previous</span>
                                    </button>
                                    <button id="carousel-ctrl-next-btn-box" class="carousel-control-next" type="button" data-bs-target="#box_imgs" data-bs-slide="next" style="display:none;" hidden>
                                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Next</span>
                                    </button>
                                </div>
                                <div id="showAddBoxImgsModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="boximgsModalLabel" aria-hidden="true">
                                    <div class="modal-dialog" role="document">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <div>
                                                    <h5 class="modal-title" id="boximgsModalLabel">박스 사진 추가</h5>
                                                </div>
                                                <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="input-group mb-2">
                                                    <input id="input_boximages" type="file" multiple="multiple" class="form-control" placeholder="이미지" accept="image/*">
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-primary"
                                                    id="btn_add_boximg">추가</button>
                                                <button type="button" class="btn btn-secondary" id="btn_cancel_boximg"
                                                    data-bs-dismiss="modal">닫기</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <script>
                                    /**
                                     * 박스 이미지 추가
                                     */
                                    window.document.querySelector("#btn_add_boximg").addEventListener('click', () => {
                                        const _doc = window.document;

                                        if (_doc.querySelector("#input_boximages").files.length < 1) {
                                            alert("이미지가 지정되지 않았습니다");
                                            return -1;
                                        }

                                        attachSpinnerButton(_doc.querySelector("#btn_add_boximg"));

                                        var path = "/jbs/box";
                                        const params = new Array();
                                        params.push({ name: "cmd", value: "addImg" });
                                        params.push({ name: "buid", value: urlparam("buid") });
                                        params.push({ name: "authcode", value: urlparam("authcode") });
                                        params.push({ name: "seq_box", value: urlparam("seq_box") });

                                        var _ajax = new XMLHttpRequest();
                                        _ajax.onreadystatechange = function () {
                                            if (checkAjaxSuc(_ajax)) {
                                                var resMsg = _ajax.responseText;
                                                if (resMsg.length > 0) {
                                                    let resJson = JSON.parse(resMsg);
                                                    var alertMsg = null;
                                                    if (resJson.code == "000") {
                                                        //팝업창 초기화
                                                        _doc.querySelector("#input_boximages").value = "";
                                                        _doc.box.image_info = resJson.image_info;
                                                        showBoxImages();
                                                        bootstrap.Modal.getOrCreateInstance(document.getElementById('showAddBoxImgsModal')).hide();
                                                        alertMsg = resJson.msg;
                                                    } else {
                                                        alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                    }
                                                    alert(alertMsg);
                                                    removeSpinnerButton(_doc.querySelector("#btn_add_boximg"), "추가");
                                                }
                                            }
                                        };
                                        sendMultipart(_ajax,
                                            path,
                                            _doc.querySelector("#input_boximages").files,
                                            params);
                                    }, false);

                                    /**
                                     *  박스 이미지 삭제
                                     */
                                    function deleteBoxImage() {
                                        if (!confirm("현재 이미지를 삭제합니까?")) {
                                            return -1;
                                        }

                                        const activeItem = document.querySelector('#box_imgs>#box_car_inne>.carousel-item.active');
                                        var imgsrc = activeItem.firstChild.src;
                                        var imgid = activeItem.firstChild.alt;

                                        var path = "/jbs/box";
                                        var params = "cmd=delImg";
                                        params += "&buid=" + urlparam("buid");
                                        params += "&authcode=" + urlparam("authcode");
                                        params += "&seq_box=" + urlparam("seq_box");
                                        params += "&imgsrc=" + imgsrc;
                                        params += "&imgid=" + imgid;

                                        var _ajax = new XMLHttpRequest();
                                        _ajax.onreadystatechange = function () {
                                            if (checkAjaxSuc(_ajax)) {
                                                var resMsg = _ajax.responseText;
                                                if (resMsg.length > 0) {
                                                    //alert(resMsg); 
                                                    let resJson = JSON.parse(resMsg);
                                                    if (resJson.code == '000') {
                                                        let _doc = window.document;
                                                        _doc.box.image_info = resJson.image_info;
                                                        showBoxImages();
                                                    }
                                                    alert(resJson.msg)
                                                }
                                            }
                                        };
                                        sendPost(_ajax, path, params);
                                    }

                                    function showBoxImages() {
                                        const _doc = window.document;

                                        var carousel_prev = _doc.querySelector("#carousel-ctrl-prev-btn-box");
                                        var carousel_next = _doc.querySelector("#carousel-ctrl-next-btn-box");
                                        hideElement(carousel_prev); hideElement(carousel_next);

                                        var box = _doc.box;
                                        if (box.type == -1) {                                            
                                            // 등록대기 박스인 경우
                                            box.image_info.imgs = [{src:"res/web/images/containing_items.jpeg"}];
                                            applyBoxImages(box, card);
                                        } else {
                                            //storage type 에 따라 google drive 인 경우 file id 로 url 을 취하여 imgs 에 적용
                                            if (box.image_info.storage_type == '1') {
                                                let gd_files = box.image_info.files;
                                                if (gd_files && gd_files.length > 0) {
                                                    attachSpinnerDiv(window.document.querySelector("#box_loading_imgs"), "("+gd_files.length+" images)");
                                                    const gda = new GoogleDriveAccess(box.image_info.access_token);
                                                    gda.fetchMultiple(gd_files).then(results => {
                                                        // 이곳에서 results 배열을 이용하여 원하는 연산을 수행
                                                        _doc.box.image_info.imgs = results;
                                                        applyBoxImages();
                                                    }).catch(error => {
                                                        console.error("An error occurred:", error);
                                                    });
                                                } else {
                                                    box.image_info.imgs = [{src:"res/web/images/no_image.jpg"}];
                                                    applyBoxImages();
                                                }
                                            } else {
                                                if (!box.image_info.imgs || box.image_info.imgs.length < 1) {
                                                    box.image_info.imgs = [{src:"res/web/images/no_image.jpg"}];
                                                }
                                                attachSpinnerDiv(window.document.querySelector("#box_loading_imgs"), "("+box.image_info.imgs.length+" images)");
                                                
                                                //service local storage 에서 이미지 로드시 바로 url 접근                                                
                                                applyBoxImages();
                                            }
                                        }
                                    }
                                    function applyBoxImages() {
                                        const _doc = window.document;
										
                                        let imgs = _doc.box.image_info.imgs;
                                        if (imgs && imgs.length > 0) {
                                            //초기화
                                            _doc.querySelector("#box_imgs").hidden = false;
                                            _doc.querySelector("#box_noimg").style.display = "none";

                                            var img_cindi = _doc.querySelector("#box_car_indi");
                                            var img_cinne = _doc.querySelector("#box_car_inne");
                                            img_cindi.innerHTML = img_cinne.innerHTML = "";
                                            
                                            var cnt_btn = 0;
                                            for (img of imgs) {
                                                let btn_slide = _doc.createElement("button");
                                                btn_slide.setAttribute("type", "button");
                                                btn_slide.setAttribute("data-bs-target", "#box_imgs");
                                                btn_slide.setAttribute("data-bs-slide-to", "" + cnt_btn);
                                                btn_slide.setAttribute("aria-label", "Slide " + cnt_btn);
                                                if (cnt_btn == 0) {
                                                    btn_slide.className = "active";
                                                    btn_slide.setAttribute("aria-current", "true");
                                                }
                                                img_cindi.appendChild(btn_slide);

                                                let div_car_box = _doc.createElement("div");
                                                if (cnt_btn == 0) {
                                                    div_car_box.className = "carousel-item active";
                                                } else {
                                                    div_car_box.className = "carousel-item";
                                                }
                                                let img_car_box = _doc.createElement("img");
                                                img_car_box.src = img.src;
                                                img_car_box.className = "d-block w-100";
                                                img_car_box.setAttribute("alt", img.id);

                                                div_car_box.appendChild(img_car_box);
                                                img_cinne.appendChild(div_car_box);

                                                cnt_btn++;
                                            }

                                            if (imgs && imgs.length > 1) {
                                                var carousel_prev = _doc.querySelector("#carousel-ctrl-prev-btn-box");
                                                var carousel_next = _doc.querySelector("#carousel-ctrl-next-btn-box");
                                                showElement(carousel_prev); showElement(carousel_next);
                                            }
                                        } else {
                                            hideElement(_doc.querySelector("#box_imgs"));
                                            showELement(_doc.querySelector("#box_noimg"));
                                        }

                                        removeSpinnerDiv(window.document.querySelector("#box_loading_imgs"));
                                    }
                                </script>
                                <!-- 박스 사진 출력 끝 -->
<% } %>
                            </div>
                        </div>
                    </div>
                    <div id="card-row-detail" class="row">
                        <div class="col-sm-12 mb-3 mb-sm-3">
                            <div id="card" class="card">
                                <div id="card_title_detail" class="card-body">                                    
                                    <h5 id="card-title" class="card-title">Box Name</h5>
                                    <p id="card-text" class="card-text">Box Info</p>

                                    <div id="div-standby-days" class="d-flex align-items-center">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor" class="bi bi-asterisk" viewBox="0 0 16 16">
                                            <path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732l-4.562 2.634a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1"/>
                                        </svg>&nbsp;
                                        <label class="form-check-label">
                                            아이템이
                                        </label>&nbsp;
                                        <select id="sel-standby-days" class="form-select form-select-sm w-auto" aria-label="Small select example">
                                            <option value="0" selected>미적용</option>
                                            <option value="1">1주</option>
                                            <option value="2">2주</option>
                                            <option value="3">3주</option>
                                            <option value="4">4주</option>
                                        </select>&nbsp;
                                        <label class="form-check-label"> 지나면 숨기기</label>
                                        <script>
                                            window.document.querySelector("#sel-standby-days").addEventListener('change', () => {    
                                                const _doc = window.document;
                                                
                                                var path = "/jbs/box";
                                                var params = "cmd=setDelAfter";
                                                if (_doc.box.type == -1) {
                                                    path = "/jbs/store";
                                                    params = "cmd=setStandbyDays";
                                                }
                                                params += "&seq_box="+_doc.box.seq;
                                                params += "&val=" + _doc.querySelector("#sel-standby-days").value;

                                                var _ajax = new XMLHttpRequest();
                                                _ajax.onreadystatechange = function () {
                                                    if (checkAjaxSuc(_ajax)) {
                                                        var resMsg = _ajax.responseText;
                                                        if (resMsg.length > 0) {
                                                            //alert(resMsg); 
                                                            let resJo = JSON.parse(resMsg);
                                                            alert(resJo.msg)
                                                        }
                                                    }
                                                };
                                                sendPost(_ajax, path, params);
                                            }, false);
                                        </script>
                                    </div>
<% if (is_partner) { %>
                                    <div id="div-rundnow" class="d-flex align-items-center">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" fill="currentColor" class="bi bi-asterisk" viewBox="0 0 16 16">
                                            <path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732l-4.562 2.634a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1"/>
                                        </svg>&nbsp;
                                        <label class="form-check-label">
                                            장보고 자동화 규칙
                                        </label>
                                        &nbsp;<button type="button" id="btn-runnow" class="btn btn-outline-primary w-auto" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">지금 수행</button>
                                        <script>
                                            window.document.querySelector("#btn-runnow").addEventListener('click', () => {                                                
                                                var path = "/jbs/auto";
                                                var params = "cmd=runNow";

                                                var _ajax = new XMLHttpRequest();
                                                _ajax.onreadystatechange = function () {
                                                    if (checkAjaxSuc(_ajax)) {
                                                        var resMsg = _ajax.responseText;
                                                        if (resMsg.length > 0) {
                                                            //alert(resMsg); 
                                                            let resJo = JSON.parse(resMsg);
                                                            alert(resJo.msg)
                                                            if (resJo.code == '000') {
                                                                window.location.replace("/jbs/box_info.jsp");
                                                            }                                                            
                                                        }
                                                    }
                                                };
                                                sendPost(_ajax, path, params);
                                            }, false);
                                        </script>
                                    </div>
<% } %>
                                </div>
                                <hr class="mb-3">
                                <div class="card-body">
                                <!--
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item d-flex justify-content-between align-items-center">An item<span
                                            class="badge bg-primary rounded-pill">14</span></li>
                                    <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span
                                            class="badge bg-primary rounded-pill">2</span></li>
                                    <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span
                                            class="badge bg-primary rounded-pill">1</span></li>
                                </ul>
                                -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h5 class="card-title">아이템 목록</h5>
                                        <button id="btn_item_add" title="아이템 추가" type="button" class="btn btn-outline-primary d-flex align-items-center" data-bs-toggle="modal" data-bs-target="#ModalAddItem">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" class="bi bi-cart-plus-fill" viewBox="0 0 16 16">
                                                <path d="M.5 1a.5.5 0 0 0 0 1h1.11l.401 1.607 1.498 7.985A.5.5 0 0 0 4 12h1a2 2 0 1 0 0 4 2 2 0 0 0 0-4h7a2 2 0 1 0 0 4 2 2 0 0 0 0-4h1a.5.5 0 0 0 .491-.408l1.5-8A.5.5 0 0 0 14.5 3H2.89l-.405-1.621A.5.5 0 0 0 2 1zM6 14a1 1 0 1 1-2 0 1 1 0 0 1 2 0m7 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M9 5.5V7h1.5a.5.5 0 0 1 0 1H9v1.5a.5.5 0 0 1-1 0V8H6.5a.5.5 0 0 1 0-1H8V5.5a.5.5 0 0 1 1 0"/>
                                            </svg>
                                        </button>
                                        <script>
                                            window.document.querySelector("#btn_item_add").addEventListener('click', () => {
                                                initItemAddPopup();
                                            }, false);
                                            document.addEventListener('DOMContentLoaded', function() {
                                                
                                            });
                                        </script>
                                    </div>
                                    <div id="item-list"></div>
                                    <input type="text" id="modal_item_mode" value="ADD" hidden>
                                    <input type="text" id="modal_item_seq" hidden>
                                    <div id="ModalAddItem" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addItemModalLabel" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <p>
                                                            <span id="addItemModalLabel" class="modal-title" style="font-size: 1.7em; font-weight: bold;">
                                                                아이템 추가</span>
                                                            <!-- 온보딩 헬프 버튼 -->
                                                            <svg id="additem_onboarding" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-question-circle" viewBox="0 0 16 16">
                                                                <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"></path>
                                                                <path d="M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286m1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94"></path>
                                                            </svg>
                                                        </p>
                                                        <script>
                                                            window.document.querySelector("#additem_onboarding").addEventListener('click', () => {
                                                                if (stepbystep_pop) {
                                                                    stepbystep_pop();
                                                                }
                                                            }, false);
                                                        </script>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <nav>
                                                        <div class="nav nav-tabs" id="nav-tab" role="tablist">
                                                            <button class="nav-link active" id="nav-barcode-tab" data-bs-toggle="tab" data-bs-target="#nav-barcode" type="button"
                                                                role="tab" aria-controls="nav-barcode" aria-selected="true">정보입력</button>
                                                            <button class="nav-link" id="nav-receipt-tab" data-bs-toggle="tab" data-bs-target="#nav-receipt" type="button"
                                                                role="tab" aria-controls="nav-receipt" aria-selected="false">영수증스캔</button>
                                                        </div>
                                                    </nav>
                                                    <div class="tab-content" id="nav-tabContent">
                                                        <!-- 아이템 바코드 스캔 입력 시작 -->
                                                        <div id="nav-barcode" class="tab-pane fade show active" role="tabpanel" aria-labelledby="nav-barcode-tab" tabindex="0">
                                                            <div class="input-group mb-2">
                                                                <video id="video" width="100%" height="100%" style="border: 1px solid gray" controls></video>
                                                            </div>
                                                            <script type="text/javascript" src="https://unpkg.com/@zxing/library@latest/umd/index.min.js"></script>
                                                            <script>
                                                                function barcodeScanInit() {
                                                                    const codeReader = window.document.barcodeReader = new ZXing.BrowserMultiFormatReader();
                                                                    console.log('ZXing code reader initialized');
                                                                    codeReader.listVideoInputDevices()
                                                                        .then((videoInputDevices) => {
                                                                                if (videoInputDevices.length >= 1) {
                                                                                    videoInputDevices.forEach((element) => {
                                                                                        if (element.label.indexOf("back") > -1) {
                                                                                            window.document.cameraId = element.deviceId;
                                                                                        }
                                                                                    })
                                                                                }
                                                                        })
                                                                        .catch((err) => {
                                                                            console.error(err)
                                                                        });
                                                                };
                    
                                                                function barcodeCameraOn() {
                                                                    
                                                                    const codeReader = window.document.barcodeReader;
                                                                    const cameraId = window.document.cameraId;                                                
                    
                                                                    const elem_video = window.document.querySelector("#video");
                                                                    elem_video.style.width = '100%';
                                                                    elem_video.style.height = '100%';
                                                                    elem_video.style.display = "block";
                                                                    
                                                                    //입력 장치 설정후 스캔 결과를 출력
                                                                    codeReader.decodeFromVideoDevice(cameraId, 'video', (result, err) => {
                                                                        if (result) {
                                                                            console.log(result)
                                                                            document.getElementById('aimd_input_barcode').value = result.text
                                                                            var path = "/jbs/extsvc";
                                                                            var params = "cmd=getProductdName";
                                                                            params += "&buid=" + urlparam("buid");
                                                                            params += "&authcode=" + urlparam("authcode");
                                                                            params += "&barcode=" + result.text;
                    
                                                                            var _ajax = new XMLHttpRequest();
                                                                            _ajax.onreadystatechange = function () {
                                                                                if (checkAjaxSuc(_ajax)) {
                                                                                    var resMsg = _ajax.responseText;
                                                                                    if (resMsg.length > 0) {
                                                                                        //alert(resMsg); 
                                                                                        let resJo = JSON.parse(resMsg);
                                                                                        if (resJo.code == '000') {
                                                                                            document.querySelector("#aimd_input_itemname").value = resJo.item_name;
                                                                                        } else if (resJo.msg) {
                                                                                            alert(resJo.msg)
                                                                                        }
                                                                                    }
                                                                                }
                                                                            };
                                                                            sendPost(_ajax, path, params);
                                                                        }
                                                                        if (err && !(err instanceof ZXing.NotFoundException)) {
                                                                            console.error(err)
                                                                            document.getElementById('aimd_input_barcode').value = err
                                                                        }
                                                                    }).catch((err) => {
                                                                        console.error(err)
                                                                    });
                                                                    console.log(`Started continous decode from camera with id ${cameraId}`);
                                                                    console.log('바코드 카메라 켜짐');
                                                                }
                    
                                                                function barcodeCameraOff() {
                    
                                                                    const codeReader = window.document.barcodeReader;                                                
                                                                    const elem_video = window.document.querySelector("#video");
                                                                    elem_video.width = 0;
                                                                    elem_video.height = 0;
                                                                    elem_video.style.display = "none";
                    
                                                                    if (!codeReader || !codeReader.reset) return -1; //실행취소
                    
                                                                    codeReader.reset();
                                                                    document.getElementById('aimd_input_barcode').value = '';
                                                                    console.log('바코드 카메라 꺼짐');
                                                                }
                    
                                                                //바코드카메라 초기화
                                                                barcodeCameraOff();
                                                                window.document.bctoggle = false;
                                                            </script>
                                                            <div id="aimd_div_barcode_btn" class="input-group mb-2">
                                                                <span id="barcode_toggle" class="btn btn-primary">바코드</span>
                                                                <input type="text" class="form-control" placeholder="스캔하려면 바코드 버튼을 터치" id="aimd_input_barcode" readonly>
                                                                    <script>
                                                                        window.document.querySelector("#barcode_toggle").addEventListener('click', () => {
                                                                            if (window.document.bctoggle) {
                                                                                barcodeCameraOff();
                                                                                window.document.bctoggle = false;
                                                                            } else {
                                                                                barcodeCameraOn();
                                                                                window.document.bctoggle = true;
                                                                            }
                                                                        }, false);
                                                                    </script>
                                                            </div>
                                                            <div class="input-group mb-2">
                                                                <span class="input-group-text">이름</span><input type="text"
                                                                    class="form-control" placeholder="아이템명"
                                                                    id="aimd_input_itemname">
                                                            </div>
                                                            <div class="input-group mb-2">
                                                                <span class="input-group-text">수량</span>
                                                                <input type="text" class="form-control" placeholder="1" id="aimd_input_qty">
                                                            </div>
                                                            <div class="input-group mb-2" id="aimd_div_insd">
                                                                <span class="input-group-text">저장일자</span>
                                                                <input type="text" class="form-control date datepicker-input"
                                                                    id="aimd_input_insd" value="." disabled>
                                                            </div>
                                                            <div class="input-group mb-2">
                                                                <span class="input-group-text">만료일자</span>
                                                                <input type="text" class="form-control date datepicker-input"
                                                                    id="aimd_input_expd" value=".">
                                                            </div>
<% if (img_use) { %>
                                                            <!-- 아이템 이미지 출력 시작 -->
                                                            <div id="aimd_div_addimg" class="input-group mb-2">
                                                                <span id="aimd_span_itemimage" class="input-group-text">사진</span>
                                                                <input id="aimd_input_itemimages" type="file" multiple="multiple" class="form-control" placeholder="이미지" accept="image/*">
                                                                <button id="aimd_btn_addimg" class="btn btn-outline-secondary" type="button">사진추가</button>
                                                            </div>                                                            
<!-- #img slide template start#--
                                                            <div id="aimd_imgs" class="carousel slide">
                                                                <div class="carousel-indicators">
                                                                    <button type="button" data-bs-target="#aimd_imgs" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
                                                                    <button type="button" data-bs-target="#aimd_imgs" data-bs-slide-to="1" aria-label="Slide 2"></button>
                                                                    <button type="button" data-bs-target="#aimd_imgs" data-bs-slide-to="2" aria-label="Slide 3"></button>
                                                                </div>
                                                                <div class="carousel-inner">
                                                                    <div class="carousel-item active">
                                                                        <img src="/107/345.jpg" class="d-block w-100" alt="...">
                                                                    </div>
                                                                    <div class="carousel-item">
                                                                        <img src="/107/234.jpg" class="d-block w-100" alt="...">
                                                                    </div>
                                                                    <div class="carousel-item">
                                                                        <img src="/107/123.png" class="d-block w-100" alt="...">
                                                                    </div>
                                                                </div>
                                                                <button class="carousel-control-prev" type="button" data-bs-target="#aimd_imgs" data-bs-slide="prev">
                                                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                                    <span class="visually-hidden">Previous</span>
                                                                </button>
                                                                <button class="carousel-control-next" type="button" data-bs-target="#aimd_imgs" data-bs-slide="next">
                                                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                                    <span class="visually-hidden">Next</span>
                                                                </button>
                                                            </div>
-- #img slide template end#-->
                                                            <div id="aimd_loading_imgs"></div>
                                                            <div id="aimd_imgs" class="carousel slide mb-2">
                                                                <div id="aimd_car_indi" class="carousel-indicators">
                                                                </div>
                                                                <div id="aimd_car_inne" class="carousel-inner">
                                                                </div>
                                                                <button id="carousel-ctrl-prev-btn-item" class="carousel-control-prev" type="button" data-bs-target="#aimd_imgs" data-bs-slide="prev">
                                                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                                    <span class="visually-hidden">Previous</span>
                                                                </button>
                                                                <button id="carousel-ctrl-next-btn-item" class="carousel-control-next" type="button" data-bs-target="#aimd_imgs" data-bs-slide="next">
                                                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                                    <span class="visually-hidden">Next</span>
                                                                </button>
                                                            </div>
                                                            <button id="aimd_btn_delimg" type="button" class="btn btn-outline-secondary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">사진삭제</button>
                                                            <script>
                                                                /**
                                                                 * 아이템 이미지 추가
                                                                 */
                                                                window.document.querySelector("#aimd_btn_addimg").addEventListener('click', () => {
                                                                    const _doc = window.document;

                                                                    if (_doc.querySelector("#aimd_input_itemimages").files.length < 1) {
                                                                        alert("이미지가 지정되지 않았습니다");
                                                                        return -1;
                                                                    }

                                                                    let seq_item = _doc.querySelector("#modal_item_seq").value;

                                                                    attachSpinnerButton(_doc.querySelector("#aimd_btn_addimg"));

                                                                    var path = "/jbs/item";
                                                                    const params = new Array();
                                                                    params.push({ name: "cmd", value: "addImg" });
                                                                    params.push({ name: "buid", value: urlparam("buid") });
                                                                    params.push({ name: "authcode", value: urlparam("authcode") });
                                                                    params.push({ name: "seq_box", value: urlparam("seq_box") });
                                                                    params.push({ name: "seq_item", value: seq_item });

                                                                    var _ajax = new XMLHttpRequest();
                                                                    _ajax.onreadystatechange = function () {
                                                                        if (checkAjaxSuc(_ajax)) {
                                                                            var resMsg = _ajax.responseText;
                                                                            if (resMsg.length > 0) {
                                                                                let resJson = JSON.parse(resMsg);
                                                                                var alertMsg = null;
                                                                                if (resJson.code == "000") {
                                                                                    //팝업창 초기화
                                                                                    _doc.querySelector("#aimd_input_itemimages").value = "";
                                                                                    
                                                                                    var item = _doc.box.items[resJson.seqItem];
                                                                                    item.image_info = resJson.image_info;
                                                                                    showItemImages(item);
                                                                                    alertMsg = resJson.msg;
                                                                                } else {
                                                                                    alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                                                }
                                                                                alert(alertMsg);
                                                                                removeSpinnerButton(_doc.querySelector("#aimd_btn_addimg"), "사진추가");
                                                                            }
                                                                        }
                                                                    };
                                                                    sendMultipart(_ajax,
                                                                        path,
                                                                        _doc.querySelector("#aimd_input_itemimages").files,
                                                                        params);
                                                                }, false);

                                                                /**
                                                                 *  아이템 이미지 삭제
                                                                 */
                                                                window.document.querySelector("#aimd_btn_delimg").addEventListener('click', () => {
                                                                    if (!confirm("현재 이미지를 삭제합니까?")) {
                                                                        return -1;
                                                                    }

                                                                    const _doc = window.document;
                                                                    let seq_item = _doc.querySelector("#modal_item_seq").value;

                                                                    const activeItem = document.querySelector('#aimd_imgs>.carousel-inner>.carousel-item.active');

                                                                    var path = "/jbs/item";
                                                                    var params = "cmd=delImg";
                                                                    params += "&buid=" + urlparam("buid");
                                                                    params += "&authcode=" + urlparam("authcode");
                                                                    params += "&seq_box=" + urlparam("seq_box");
                                                                    params += "&seq_item=" + seq_item;
                                                                    params += "&imgid=" + activeItem.firstChild.alt;
                                                                    params += "&imgsrc=" + activeItem.firstChild.src;

                                                                    var _ajax = new XMLHttpRequest();
                                                                    _ajax.onreadystatechange = function () {
                                                                        if (checkAjaxSuc(_ajax)) {
                                                                            var resMsg = _ajax.responseText;
                                                                            if (resMsg.length > 0) {
                                                                                //alert(resMsg); 
                                                                                let resJo = JSON.parse(resMsg);
                                                                                if (resJo.code == '000') {
                                                                                    var item = _doc.box.items[resJo.seqItem];
                                                                                    item.imgs = resJo.image_info.imgs;
                                                                                    showItemImages(item);
                                                                                }
                                                                                alert(resJo.msg)
                                                                            }
                                                                        }
                                                                    };
                                                                    sendPost(_ajax, path, params);
                                                                }, false);

                                                                /**
                                                                 * 아이템 이미지를 출력 및 갱신
                                                                 */
                                                                function showItemImages(item) {

                                                                    var carousel_prev = window.document.querySelector("#carousel-ctrl-prev-btn-item");
                                                                    var carousel_next = window.document.querySelector("#carousel-ctrl-next-btn-item");
                                                                    hideElement(carousel_prev); hideElement(carousel_next);

                                                                    //storage type 에 따라 google drive 인 경우 file id 로 url 을 취하여 imgs 에 적용
                                                                    if (item.image_info.storage_type == '1') {
                                                                        let gd_files = item.image_info.files;
                                                                        if (gd_files && gd_files.length > 0) {
                                                                            attachSpinnerDiv(window.document.querySelector("#aimd_loading_imgs"), "("+gd_files.length+" images)");
                                                                            const gda = new GoogleDriveAccess(item.image_info.access_token);
                                                                            gda.fetchMultiple(gd_files).then(results => {
                                                                                // 이곳에서 results 배열을 이용하여 원하는 연산을 수행
                                                                                item.image_info.imgs = results;
                                                                                applyItemImages(item);
                                                                            }).catch(error => {
                                                                                console.error("An error occurred:", error);
                                                                            });
                                                                        }
                                                                    } else if (item.image_info.imgs) {
                                                                        attachSpinnerDiv(window.document.querySelector("#aimd_loading_imgs"), "("+item.image_info.imgs.length+" images)");
                                                                        //service local storage 에서 이미지 로드시 바로 url 접근
                                                                        applyItemImages(item);
                                                                    }
                                                                }
                                                                function applyItemImages(item) {
                                                                    const _doc = window.document;

                                                                    var carousel_prev = _doc.querySelector("#carousel-ctrl-prev-btn-item");
                                                                    var carousel_next = _doc.querySelector("#carousel-ctrl-next-btn-item");
                                                                    hideElement(carousel_prev); hideElement(carousel_next);

                                                                    let imgs = item.image_info.imgs;
                                                                    if (imgs && imgs.length > 0) {
                                                                        _doc.querySelector("#aimd_imgs").hidden = false;
                                                                        _doc.querySelector("#aimd_btn_delimg").hidden = false;
                                                                        var img_cindi = _doc.querySelector("#aimd_car_indi");
                                                                        var img_cinne = _doc.querySelector("#aimd_car_inne");
                                                                        img_cindi.innerHTML = img_cinne.innerHTML = "";
                                                                        
                                                                        var cnt_btn = 0;
                                                                        for (img of imgs) {
                                                                            let btn_slide = _doc.createElement("button");
                                                                            btn_slide.setAttribute("type", "button");
                                                                            btn_slide.setAttribute("data-bs-target", "#aimd_imgs");
                                                                            btn_slide.setAttribute("data-bs-slide-to", "" + cnt_btn);
                                                                            btn_slide.setAttribute("aria-label", "Slide " + cnt_btn);
                                                                            if (cnt_btn == 0) {
                                                                                btn_slide.className = "active";
                                                                                btn_slide.setAttribute("aria-current", "true");
                                                                            }
                                                                            img_cindi.appendChild(btn_slide);

                                                                            let div_car_itm = _doc.createElement("div");
                                                                            if (cnt_btn == 0) {
                                                                                div_car_itm.className = "carousel-item active";
                                                                            } else {
                                                                                div_car_itm.className = "carousel-item";
                                                                            }
                                                                            let img_car_itm = _doc.createElement("img");
                                                                            img_car_itm.src = img.src;
                                                                            img_car_itm.className = "d-block w-100";
                                                                            img_car_itm.setAttribute("alt", img.id);

                                                                            div_car_itm.appendChild(img_car_itm);
                                                                            img_cinne.appendChild(div_car_itm);

                                                                            cnt_btn++;
                                                                        }

                                                                        if (imgs.length > 1) {
                                                                            showElement(carousel_prev); showElement(carousel_next);
                                                                        }

                                                                    } else {
                                                                        hideElement(_doc.querySelector("#aimd_imgs")); 
                                                                        hideElement(_doc.querySelector("#aimd_btn_delimg"));
                                                                    }

                                                                    removeSpinnerDiv(window.document.querySelector("#aimd_loading_imgs"));
                                                                }
                                                            </script>
                                                            <!-- 아이템 이미지 출력 끝 -->
<% } %>
                                                        </div>
                                                        <!-- 아이템 바코드 스캔 입력 끝 -->
                                                        <div class="tab-pane fade" id="nav-receipt" role="tabpanel" aria-labelledby="nav-receipt-tab" tabindex="0">
                                                            준비중...
                                                        </div>
                                                    </div>

                                                    <div id="aimd_add_cont_div">
                                                        <input class="form-check-input" type="checkbox" id="aimd_add_continue">
                                                        <span id="aimd_add_cont_label">계속 입력</span>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-primary"
                                                        id="aimd_btn_add">추가</button>
                                                    <button type="button" class="btn btn-primary"
                                                        id="aimd_btn_mod">수정</button>
                                                    <button type="button" class="btn btn-primary"
                                                        id="aimd_btn_del">삭제</button>
                                                    <button type="button" class="btn btn-secondary" id="aimd_btn_cancel"
                                                        data-bs-dismiss="modal">닫기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <script>
                                        showDatepicker(document.querySelector('#aimd_input_expd'));
                                        /**
                                         * 아이템 추가 팝업
                                         */
                                        function initItemAddPopup() {
                                            const _doc = window.document;

                                            //화면 초기화
                                            _doc.querySelector("#modal_item_mode").value = "ADD";
                                            _doc.querySelector("#modal_item_seq").value = "";
                                            _doc.querySelector("#addItemModalLabel").innerText = "아이템 추가";
                                            _doc.querySelector("#aimd_input_itemname").value = "";
                                            _doc.querySelector("#aimd_input_qty").value = "1";
                                            _doc.querySelector("#aimd_input_expd").value = "";
                                            _doc.querySelector("#aimd_add_continue").checked = false;
<% if (img_use) { %>
                                            _doc.querySelector("#aimd_btn_addimg").hidden = true;
<% } %>
											_doc.querySelector("#aimd_div_insd").hidden = true;
                                            _doc.querySelector("#aimd_btn_add").hidden = false;
                                            _doc.querySelector("#aimd_btn_mod").hidden = true;
                                            _doc.querySelector("#aimd_btn_del").hidden = true;
                                            _doc.querySelector("#aimd_add_continue").hidden = false;
                                            _doc.querySelector("#aimd_add_cont_label").hidden = false;
<% if (img_use) { %>
                                            _doc.querySelector("#aimd_imgs").hidden = true;
                                            _doc.querySelector("#aimd_btn_delimg").hidden = true;
<% } %>
                                        }
                                        /**
                                         * 아이템 추가
                                         */
                                        window.document.querySelector("#aimd_btn_add").addEventListener('click', () => {

                                            const _doc = window.document;

                                            var path = "/jbs/item";
                                            const params = new Array();
                                            params.push({name:"cmd", value:"store"});
                                            params.push({name:"buid", value:urlparam("buid")});
                                            params.push({name:"authcode", value:urlparam("authcode")});
                                            params.push({name:"seq_box", value:urlparam("seq_box")});

                                            let item_name = _doc.querySelector("#aimd_input_itemname").value;
                                            let item_qty = _doc.querySelector("#aimd_input_qty").value;
                                            let item_iexpd = _doc.querySelector("#aimd_input_expd").value;

                                            if (!item_name) {
                                                alert("아이템명이 입력되지 않았습니다");
                                                return -1;
                                            }
                                            if (!item_qty) {
                                                alert("아이템 수량이 없습니다");
                                                return -1;
                                            }
                                            if (item_iexpd) {
                                                item_iexpd = item_iexpd.replaceAll(".", "");
                                            }

                                            params.push({name:"item_name", value:item_name});
                                            params.push({name:"item_qty", value:item_qty});
                                            params.push({name:"item_expd", value:item_iexpd});

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        let resJson = JSON.parse(resMsg);
                                                        var alertMsg = null;
                                                        if (resJson.code == "000") {
                                                            //팝업창 초기화
                                                            _doc.querySelector("#aimd_input_itemname").value = "";
                                                            _doc.querySelector("#aimd_input_qty").value = "1";
															_doc.querySelector("#aimd_input_insd").value = "";
                                                            _doc.querySelector("#aimd_input_expd").value = "";
<% if (img_use) { %>
                                                            _doc.querySelector("#aimd_input_itemimages").value = "";
<% } %>
															updateItemList(resJson);
                                                            if (!_doc.querySelector("#aimd_add_continue").checked) {
                                                                bootstrap.Modal.getOrCreateInstance(_doc.querySelector("#ModalAddItem")).hide();
                                                            }
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        alert(alertMsg);
                                                    }
                                                }
                                            };
                                            var itemimages = _doc.querySelector("#aimd_input_itemimages");
                                            sendMultipart(_ajax, 
                                                path, 
                                                itemimages ? itemimages.files : null,
                                                params);

                                        }, false);
                                        /**
                                        * 아이템 수정 팝업
                                        */
                                        function itemModify(seqItem) {
                                            const _doc = window.document;

                                            //화면 초기화
                                            _doc.querySelector("#addItemModalLabel").innerText = "아이템 수정";
											_doc.querySelector("#aimd_div_insd").hidden = false;
                                            _doc.querySelector("#aimd_btn_add").hidden = true;
                                            _doc.querySelector("#aimd_btn_mod").hidden = false;
                                            _doc.querySelector("#aimd_btn_del").hidden = false;
                                            _doc.querySelector("#aimd_add_continue").hidden = true;
                                            _doc.querySelector("#aimd_add_cont_label").hidden = true;
<% if (img_use) { %>
                                            _doc.querySelector("#aimd_car_indi").innerHTML = null;
                                            _doc.querySelector("#aimd_car_inne").innerHTML = null;
                                            _doc.querySelector("#aimd_span_itemimage").hidden = true;
                                            _doc.querySelector("#aimd_btn_addimg").hidden = false;
<% } %>

                                            //alert(JSON.stringify(_doc.box.items));
                                            var item = _doc.box.items[seqItem];
                                            _doc.querySelector("#modal_item_mode").value = "MODIFY";
                                            _doc.querySelector("#modal_item_seq").value = item.seq;
                                            _doc.querySelector("#aimd_input_itemname").value = item.name;
                                            _doc.querySelector("#aimd_input_qty").value = item.qty;
<% if (img_use) { %>
                                            _doc.querySelector("#aimd_input_itemimages").value = "";
<% } %>

											let iinsd = item.insert_date;
                                            if (iinsd > 0) {
                                                _doc.querySelector("#aimd_input_insd").value = addDatedot(iinsd);
                                            } else {
                                                _doc.querySelector("#aimd_input_expd").value = ".";
                                            }

											let iexpd = item.expiry_date;
                                            if (iexpd > 0) {
                                                _doc.querySelector("#aimd_input_expd").value = addDatedot(iexpd);
                                            } else {
                                                _doc.querySelector("#aimd_input_expd").value = ".";
                                            }
<% if (img_use) { %>
                                            showItemImages(item);
<% } %>
                                        }
                                        /**
                                         * 아이템 수정
                                         */
                                        window.document.querySelector("#aimd_btn_mod").addEventListener('click', () => {

                                            const _doc = window.document;

                                            var path = "/jbs/item";
                                            const params = new Array();
                                            params.push({name:"cmd", value:"store"});
                                            params.push({name:"buid", value:urlparam("buid")});
                                            params.push({name:"authcode", value:urlparam("authcode")});
                                            params.push({name:"seq_box", value:urlparam("seq_box")});

                                            let seq_item = _doc.querySelector("#modal_item_seq").value;
                                            if (seq_item) {
                                                params.push({name:"seq_item", value:seq_item});
                                            } else {
                                                alert("아이템이 선택되지 않았습니다");
                                                return -1;
                                            }
                                            params.push({name:"item_name", value:_doc.querySelector("#aimd_input_itemname").value});
                                            params.push({name:"item_qty", value:_doc.querySelector("#aimd_input_qty").value});
                                            let iexpd = _doc.querySelector("#aimd_input_expd").value;
                                            if (iexpd) {
                                                params.push({name:"item_expd", value:delDatedot(iexpd)});
                                            }
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
                                                            //팝업창 초기화
                                                            _doc.querySelector("#aimd_input_itemname").value = "";
                                                            _doc.querySelector("#aimd_input_qty").value = "1";
                                                            _doc.querySelector("#aimd_input_insd").value = "";
                                                            _doc.querySelector("#aimd_input_expd").value = "";
<% if (img_use) { %>
                                                            _doc.querySelector("#aimd_input_itemimages").value = "";
<% } %>
															updateItem(resJson);
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        alert(alertMsg);
                                                    }
                                                }
                                            };
                                            
                                            sendMultipart(_ajax, 
                                                path, 
                                                _doc.querySelector("#aimd_input_itemimages").files,
                                                params);

                                        }, false);
                                        /**
                                         * 아이템 삭제
                                         */
                                        window.document.querySelector("#aimd_btn_del").addEventListener('click', () => {
                                            const _doc = window.document;

                                            var path = "/jbs/item";
                                            var params = "cmd=take";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&seq_box=" + urlparam("seq_box");
                                            let seq_item = _doc.querySelector("#modal_item_seq").value;
                                            if (seq_item) {
                                                params += "&seq_item=" + seq_item;
                                            } else {
                                                alert("아이템이 선택되지 않았습니다");
                                                return -1;
                                            }
                                            if (!confirm("선택한 아이템을 삭제하시겠습니까?")) {
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
															updateItemList(resJson);
                                                            bootstrap.Modal.getOrCreateInstance(_doc.querySelector("#ModalAddItem")).hide();
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        alert(alertMsg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        /**
                                         * 팝업 close시의 처리
                                         */                                        
                                        window.document.querySelector("#ModalAddItem").addEventListener('hidden.bs.modal', () => {
                                            barcodeCameraOff();
                                        }, false);
                                    </script>
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-primary dropdown-toggle"
                                            data-bs-toggle="dropdown" aria-expanded="false" id="btn_item_batch">
                                            선택물품만
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#showEditItemsModal">속성 변경</a></li>
                                            <li><a class="dropdown-item" href="javascript:deleteItems()">삭제</a></li>
                                            <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#showJoinTheNanumModal">나눔에 추가</a></li>
                                        </ul>
                                    </div>
                                    <div id="showEditItemsModal" class="modal fade" tabindex="-1" role="dialog"
                                        aria-labelledby="editItemsModalLabel" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title" id="editItemsModalLabel">속성 변경</h5>
                                                        <h6 class="text-danger" id="eimd_check_msg"></h6>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                                                </div>
                                                <!--form action="/jbs/item?cmd=batch" method="post"-->
                                                    <div class="modal-body">
                                                        <div class="input-group mb-2">
                                                            <input type="checkbox" class="custom-control-input"
                                                                name="eimd_apply" value="to_box">&nbsp;
                                                            <select class="form-select mb-2"
                                                                aria-label="Default select example" id="eimd_to_box">
                                                                <!--option selected>Open this select menu</option>
																<option value="1">One</option>
																<option value="2">Two</option>
																<option value="3">Three</option-->
                                                            </select>
                                                        </div>
                                                        <div class="input-group mb-2">
                                                            <input type="checkbox" class="custom-control-input"
                                                                name="eimd_apply" value="input_qty">&nbsp;
                                                            <span class="input-group-text">수량</span>
                                                            <input type="text" class="form-control" placeholder="1"
                                                                id="eimd_input_qty">
                                                        </div>
                                                        <div class="input-group mb-2">
                                                            <input type="checkbox" class="custom-control-input"
                                                                name="eimd_apply" value="input_insd" disabled>&nbsp;
                                                            <span class="input-group-text">저장일자</span>
                                                            <input type="text"
                                                                class="form-control date datepicker-input"
                                                                id="eimd_input_insd" value="." disabled>
                                                        </div>
                                                        <div class="input-group mb-2">
                                                            <input type="checkbox" class="custom-control-input"
                                                                name="eimd_apply" value="input_expd" disabled>&nbsp;
                                                            <span class="input-group-text">만료일자</span>
                                                            <input type="text"
                                                                class="form-control date datepicker-input"
                                                                id="eimd_input_expd" value="." disabled>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-primary"
                                                            id="eimd_btn_apply">적용</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            id="eimd_btn_cancel" data-bs-dismiss="modal">닫기</button>
                                                    </div>
                                                <!--/form-->
                                            </div>
                                        </div>
                                    </div>
                                    <div id="showJoinTheNanumModal" class="modal fade" tabindex="-1" role="dialog"
                                        aria-labelledby="joinTheNanumModalLabel" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title" id="joinTheNanumModalLabel">나눔에 추가</h5>
                                                        <h6 class="text-danger" id="jtnmd_check_msg"></h6>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                                                </div>
                                                <!--form action="/jbs/nanum?cmd=join" method="post"-->
                                                    <div class="modal-body">
                                                        <div class="input-group mb-2">
                                                            <select id="jtnmd_to_nanum" class="form-select mb-2" aria-label="Default select example">
                                                                <!--option selected>Open this select menu</option>
																<option value="1">One</option>
																<option value="2">Two</option>
																<option value="3">Three</option-->
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-primary"
                                                            id="jtnmd_btn_apply">등록</button>
                                                        <button type="button" class="btn btn-secondary"
                                                            id="jtnmd_btn_cancel" data-bs-dismiss="modal">닫기</button>
                                                    </div>
                                                <!--/form-->
                                            </div>
                                        </div>
                                    </div>
                                    <script>
                                        showDatepicker(document.querySelector('#eimd_input_insd'));
                                        showDatepicker(document.querySelector('#eimd_input_expd'));
                                        
										window.document.querySelector("#btn_item_batch").addEventListener('click', () => {
                                            let selectedItems = window.document.querySelectorAll('input[name="items[]"]:checked');
                                            let eimdCheckMsg = window.document.querySelector("#eimd_check_msg");
                                            if (selectedItems.length < 1) {
                                                eimdCheckMsg.className = "text-danger";
                                                eimdCheckMsg.innerText = "선택한 아이템이 없습니다";
                                            } else {
                                                eimdCheckMsg.className = "text-primary";
                                                eimdCheckMsg.innerText = selectedItems.length + "개의 아이템이 선택되었습니다";
                                                eimdCheckMsg.innerText += "\n변경할 속성을 체크하여 적용하세요";
                                            }
                                        }, false);
										
                                        window.document.querySelector("#eimd_btn_apply").addEventListener('click', () => {
											const _doc = window.document;
                                            let selectedItems = _doc.querySelectorAll('input[name="items[]"]:checked');
                                            let selectedEimdAttrs = _doc.querySelectorAll('input[name="eimd_apply"]:checked');
                                            if (selectedItems.length > 0) {
                                                if (selectedEimdAttrs.length > 0) {

                                                    var path = "/jbs/item";
                                                    var params = "cmd=batch";
                                                    params += "&buid=" + urlparam("buid");
                                                    params += "&authcode=" + urlparam("authcode");
                                                    params += "&seq_box=" + urlparam("seq_box");

                                                    let items = "";
                                                    for (selItem of selectedItems) {
                                                        items += selItem.value + ",";
                                                    }
                                                    params += "&items=" + items;
                                                    for (selAttr of selectedEimdAttrs) {
                                                        let pNm = "eimd_" + selAttr.value;
                                                        let pVl = _doc.querySelector("#" + pNm).value;
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
																	_doc.querySelector("#eimd_input_qty").value = "";
																	_doc.querySelector("#eimd_input_insd").value = "";
																	_doc.querySelector("#eimd_input_expd").value = "";
																	for (selAttr of selectedEimdAttrs) {
																		selAttr.checked = false;
																	}
																	
																	//전체 목록을 갱신
                                                                    if (resJson.items) {
																		_doc.box.items = resJson.items;
                                                                        _doc.querySelector("#item-list").innerHTML = "";
                                                                        let card_outer = _doc.createElement("ul");
                                                                        card_outer.className = "list-group list-group-flush";
                                                                        let keyArr = Object.keys(resJson.items);
                                                                        for (i = 1; i <= keyArr.length; i++) {
                                                                            addItemRow(card_outer, resJson.items[keyArr[i - 1]]);
                                                                        }
                                                                        _doc.querySelector("#item-list").appendChild(card_outer);
                                                                    }
                                                                    bootstrap.Modal.getOrCreateInstance(_doc.getElementById('showEditItemsModal')).hide();
                                                                }
                                                                alert(resJson.msg);
                                                            }
                                                        }
                                                    };
                                                    sendPost(_ajax, path, params);

                                                } else {
                                                    alert("변경할 속성이 체크되지 않았습니다");
                                                }
                                            } else {
                                                alert("선택한 아이템이 없습니다");
                                            }
                                        }, false);
										
										function deleteItems() {
											const _doc = window.document;
										
											let selectedItems = _doc.querySelectorAll('input[name="items[]"]:checked');
											if (selectedItems.length > 0) {											
												if (confirm("선택한 아이템을 삭제하시겠습니까?")) {
													var path = "/jbs/item";
													var params = "cmd=takeItems";
													params += "&buid=" + urlparam("buid");
													params += "&authcode=" + urlparam("authcode");
													params += "&seq_box=" + urlparam("seq_box");
												
													let items = "";
													for (selItem of selectedItems) {
														items += selItem.value + ",";
													}
													params += "&items=" + items;
													
													var _ajax = new XMLHttpRequest();
													_ajax.onreadystatechange = function () {
														
														if (checkAjaxSuc(_ajax)) {
															let resMsg = _ajax.responseText;
															if (resMsg.length > 0) {
																let resJson = JSON.parse(resMsg);
																if (resJson.code == "000") {
																	updateItemList(resJson);
																}
																alert(resJson.msg);
															}
														}
													};
													sendPost(_ajax, path, params);
												}
											} else {
                                                alert("선택한 아이템이 없습니다");
                                            }
										}

                                        window.document.querySelector("#jtnmd_btn_apply").addEventListener('click', () => {
											const _doc = window.document;
                                            let selectedItems = _doc.querySelectorAll('input[name="items[]"]:checked');
                                            if (selectedItems.length > 0) {
                                                var path = "/jbs/nanum";
                                                var params = "cmd=registItems";
                                                params += "&buid=" + urlparam("buid");
                                                params += "&authcode=" + urlparam("authcode");
                                                params += "&seq_box=" + urlparam("seq_box");
                                                params += "&seq_nanum=" + _doc.querySelector("#jtnmd_to_nanum").value;

                                                let items = "";
                                                for (selItem of selectedItems) {
                                                    items += selItem.value + ",";
                                                }
                                                params += "&items=" + items;
                                                
                                                var _ajax = new XMLHttpRequest();
                                                _ajax.onreadystatechange = function () {
                                                    
                                                    if (checkAjaxSuc(_ajax)) {
                                                        let resMsg = _ajax.responseText;
                                                        if (resMsg.length > 0) {
                                                            let resJson = JSON.parse(resMsg);
                                                            if (resJson.code == "000") {
                                                                //팝업창 초기화																	
                                                                _doc.querySelector("#jtnmd_to_nanum").value = "";
                                                                for (selItem of selectedItems) {
                                                                    selItem.checked = false;
                                                                }
                                                                
                                                                bootstrap.Modal.getOrCreateInstance(_doc.getElementById('showJoinTheNanumModal')).hide();
                                                            }
                                                            alert(resJson.msg);
                                                        }
                                                    }
                                                };
                                                sendPost(_ajax, path, params);
                                            } else {
                                                alert("선택한 아이템이 없습니다");
                                            }
                                        }, false);
                                    </script>
                                </div>
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
                                        <tbody>
                                            <!--tr>
                                                <th scope="row">1</th>
                                                <td>Mark</td>
                                                <td>Friend</td>
                                            </tr>
                                            <tr>
                                                <th scope="row">2</th>
                                                <td>Jacob</td>
                                                <td>Guest</td>
                                            </tr-->
                                        </tbody>
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
                                    <!-- <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#showInvModal" id="btn_invite_add">ADD</button> -->
                                    <button type="button" class="btn btn-primary" id="btn_invite_add">추가</button>
                                    <input type="text" id="modal_inv_mode" value="ADD" hidden>
                                    <input type="text" id="modal_inv_seq" hidden>
                                    <input type="text" id="modal_inv_authright" hidden>
                                    <input type="text" id="modal_inv_expire" hidden>
                                    <input type="text" id="modal_inv_updated" hidden>
                                    <div id="showInvModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="inviteModalLabel" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <div>
                                                        <h5 class="modal-title" id="inviteModalLabel">초대장</h5>
                                                    </div>
                                                    <button type="button" class="btn-close" aria-label="Close"
                                                        data-bs-dismiss="modal" id="imd_btn_close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userid">대상아이디</span><input
                                                            type="text" class="form-control" list="imd_id_dl_opts"
                                                            id="imd_input_id">
                                                        <datalist id="imd_id_dl_opts"></datalist>
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text" id="userauth">공유권한</span>
                                                        <select class="form-select form-select-sm"
                                                            aria-label=".form-select-sm example" id="imd_auth_right">
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
                                                        <span class="input-group-text">초대링크</span><input type="text"
                                                            class="form-control" placeholder="please generate" disabled
                                                            readonly id="inviteurl">
                                                    </div>
                                                    <div class="input-group mb-2">
                                                        <span class="input-group-text">유효일자</span><input type="text"
                                                            class="form-control" placeholder="please generate" disabled
                                                            readonly id="expirydate">
                                                    </div>
                                                    <div class="input-group mb-2"><button type="button"
                                                            class="btn btn-primary" id="imd_btn_gen">Generate</button>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-primary"
                                                        id="imd_btn_ok">수정</button>
                                                    <button type="button" class="btn btn-secondary" id="imd_btn_cancel"
                                                        data-bs-dismiss="modal">닫기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <script>
                                        /**
                                         * 초대 추가 팝업
                                         */
                                        window.document.querySelector("#btn_invite_add").addEventListener('click', () => {
                                            alert("준비중입니다");
                                            return -1;

                                            const _doc = window.document;
                                            _doc.querySelector("#imd_input_id").removeAttribute("disabled");
                                            _doc.querySelector("#imd_input_id").removeAttribute("readonly");
                                            _doc.querySelector("#imd_btn_ok").textContent = "추가";
                                            _doc.querySelector("#imd_btn_gen").textContent = "Generate";

                                            _doc.querySelector("#modal_inv_mode").value = "ADD";
                                            _doc.querySelector("#modal_inv_seq").value = "";
                                            _doc.querySelector("#imd_input_id").value = "";
                                            _doc.querySelector("#imd_auth_right").value = "F";
                                            _doc.querySelector("#invitecode").value = "";
                                            _doc.querySelector("#inviteurl").value = "";
                                            _doc.querySelector("#expirydate").value = "";
                                        }, false);
                                        /**
                                        * 초대 수정 팝업
                                        */
                                        function inviteModify(invSeq) {
                                            const _doc = window.document;
                                            _doc.querySelector("#imd_input_id").setAttribute("disabled", "");
                                            _doc.querySelector("#imd_input_id").setAttribute("readonly", "");
                                            _doc.querySelector("#modal_inv_updated").value = "false";
                                            _doc.querySelector("#imd_btn_ok").textContent = "수정";
                                            _doc.querySelector("#imd_btn_gen").textContent = "Regenerate";

                                            var invite = _doc.box.invites[invSeq];
                                            //alert(JSON.stringify(invite));
                                            _doc.querySelector("#modal_inv_mode").value = "MODIFY";
                                            _doc.querySelector("#modal_inv_seq").value = invite.seq;
                                            _doc.querySelector("#imd_input_id").value = invite.mem_id;
                                            _doc.querySelector("#imd_auth_right").value = invite.auth_right;
                                            _doc.querySelector("#invitecode").value = invite.invite_code;
                                            _doc.querySelector("#inviteurl").value = invite.invite_url;
                                            _doc.querySelector("#expirydate").value = invite.expiry_date;
                                        }
                                        /**
                                        * 아이디 검색
                                        */
                                        window.document.imd_input_id_prevalue = null;
                                        window.document.querySelector("#imd_input_id").addEventListener('keyup', () => {
                                            //console.debug("[debug] keyup~");
                                            const _doc = window.document;
                                            let inputTxt = _doc.querySelector("#imd_input_id").value;

                                            if (window.document.imd_input_id_prevalue == inputTxt) {
                                                return -1;
                                            } else {
                                                window.document.imd_input_id_prevalue = inputTxt;
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
                                                            var dataList = _doc.querySelector("#imd_id_dl_opts");
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
                                        window.document.querySelector("#imd_btn_gen").addEventListener('click', () => {
                                            const _doc = window.document;

                                            if (!_doc.querySelector("#imd_input_id").value) {
                                                alert("아이디를 입력해야 합니다");
                                                return -1;
                                            }

                                            var path = "/jbs/invite";
                                            var params = "cmd=generate";
                                            params += "&buid=" + urlparam("buid");
                                            params += "&authcode=" + urlparam("authcode");
                                            params += "&uid=" + _doc.querySelector("#imd_input_id").value;
                                            // params += "&right=" + _doc.querySelector("#imd_auth_right").value;
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
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        /**
                                         * 초대장 저장
                                         */
                                        window.document.querySelector("#imd_btn_ok").addEventListener('click', () => {
                                            const _doc = window.document;

                                            if (!_doc.querySelector("#imd_input_id").value) {
                                                alert("아이디를 입력해야 합니다");
                                                return -1;
                                            }

                                            var invites = _doc.box.invites;
                                            if (invites) {
                                                var invite = null;
                                                var keyArr = Object.keys(invites);
                                                for (i = 0; i < keyArr.length; i++) {
                                                    invite = invites[keyArr[i]];
                                                    if (invite.seq_object == urlparam("seq_box")
                                                        && invite.mem_id == _doc.querySelector("#imd_input_id").value) {
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
                                            params += "&uid=" + _doc.querySelector("#imd_input_id").value; //공유 받을 사람
                                            params += "&authority=" + _doc.querySelector("#imd_auth_right").value; // 부여할 자격
                                            params += "&seq_box=" + urlparam("seq_box");
                                            params += "&code=" + _doc.querySelector("#invitecode").value;
                                            params += "&url=" + _doc.querySelector("#inviteurl").value;
                                            params += "&expire=" + _doc.querySelector("#expirydate").value;

                                            if (!confirm("초대장을 저장하시겠습니까?")) {
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
                                                            bootstrap.Modal.getOrCreateInstance(document.getElementById('showInvModal')).hide();
                                                            window.document.querySelector("#modal_inv_updated").value = "true";
                                                            alertMsg = resJson.msg;
                                                        } else {
                                                            alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                                        }
                                                        alert(alertMsg);
                                                    }
                                                }
                                            };
                                            sendPost(_ajax, path, params);
                                        }, false);
                                        window.document.querySelector("#imd_btn_close").addEventListener('click', () => {
                                            const _doc = window.document;
                                            if (_doc.querySelector("#modal_inv_updated").value == "true") {
                                                var invSeq = _doc.querySelector("#modal_inv_seq").value;
                                                var invite = _doc.box.invites[invSeq];
                                                invite.auth_right = _doc.querySelector("#imd_auth_right").value;
                                                invite.expiry_date = _doc.querySelector("#expirydate").value;
                                                _doc.querySelector("#td_invmod_authright_" + invSeq).innerText = _doc.querySelector("#imd_auth_right").value;
                                                _doc.querySelector("#td_invmod_expirydate_" + invSeq).innerText = _doc.querySelector("#expirydate").value;
                                            }
                                        }, false);
                                        window.document.querySelector("#imd_btn_cancel").addEventListener('click', () => {
                                            const _doc = window.document;
                                            if (_doc.querySelector("#modal_inv_updated").value == "true") {
                                                //수정중인 값을 초기화한다
                                                var invSeq = _doc.querySelector("#modal_inv_seq").value;
                                                var invite = _doc.box.invites[invSeq];
                                                invite.auth_right = _doc.querySelector("#imd_auth_right").value;
                                                invite.expiry_date = _doc.querySelector("#expirydate").value;
                                                _doc.querySelector("#td_invmod_authright_" + invSeq).innerText = _doc.querySelector("#imd_auth_right").value;
                                                _doc.querySelector("#td_invmod_expirydate_" + invSeq).innerText = _doc.querySelector("#expirydate").value;
                                            }
                                        }, false);
                                    </script>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

	<script>
	    window.addEventListener('load', () => {
	
	        barcodeScanInit();
	
	        var path = "/jbs/box";
	        var params = "cmd=info";
	        params += "&buid=" + urlparam("buid");
	        params += "&authcode=" + urlparam("authcode");
	        params += "&seq_box=" + urlparam("seq_box");
	
	        var _ajax = new XMLHttpRequest();
	        _ajax.onreadystatechange = function () {
	            if (checkAjaxSuc(_ajax)) {
	                var resMsg = _ajax.responseText;
	                if (resMsg.length > 0) {
	                    //alert(resMsg); 
	                    let resJo = JSON.parse(resMsg);
	                    if (resJo.code == '000') {
	                        let _doc = window.document;
	                        _doc.nanums = resJo.nanums;
	                        let box = _doc.box = resJo.box;
	
	                        showBoxDetail();
	                        createEimdBoxList(resJo.boxes, box.seq);
                            createJtnmdNanumList(resJo.nanums);
	                    } else {
	                        authFailCheck(resJo.code, resJo.msg);
	                    }
	                }
	            }
	        };
	        sendPost(_ajax, path, params);
	    }, false);
	
	    function showBoxDetail() {
	<% if (img_use) { %>
	        showBoxImages();
	<% } %>
	        const _doc = window.document;

	        let box = _doc.box;
            if (box.type == -1) {
                //등록대기 박스에서 버튼 숨기기
                if (_doc.querySelector("#btn_addboximg"))
                    hideElement(_doc.querySelector("#btn_addboximg"));
                if (_doc.querySelector("#btn_delboximg"))
                    hideElement(_doc.querySelector("#btn_delboximg"));
                if (_doc.querySelector("#box_car_indi > button"))
                    hideElement(_doc.querySelector("#box_car_indi > button"));

                //대기 일자 설정                
                _doc.querySelector("#sel-standby-days").value = box.standby_days / 7;
            } else {
                _doc.querySelector("#sel-standby-days").value = box.hide_after / 7;
            }
	        _doc.querySelector("#card-title").innerText = box.name;
	        if (box.details) {
	            _doc.querySelector("#card-text").innerText = box.details;
	        }
	        if (box.invites) {
	            let seqArr = Object.keys(box.invites);
	            for (i = 1; i <= seqArr.length; i++) {
	                addInviteRow(_doc.querySelector("#invite-tbody"), i, box.invites[seqArr[i - 1]]);
	            }
	        }
	        if (box.items) {
	            let card_outer = _doc.createElement("ul");
	            card_outer.className = "list-group list-group-flush";
	            let seqArr = Object.keys(box.items);
	            for (let i = 1; i <= seqArr.length; i++) {
	                addItemRow(card_outer, box.items[seqArr[i - 1]]);
	            }
	            _doc.querySelector("#item-list").appendChild(card_outer);
	        }
	    }
	
	    function addInviteRow(list_group, cnt, invite) {
	        const _doc = window.document;
	        var elem_tr = _doc.createElement("tr");
	        var tr_inner = "<th>" + cnt + "</th>";
	        tr_inner += "<td><button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#showInvModal' id='btn_invite_mod_" + invite.seq + "' onclick='javascript:inviteModify(" + invite.seq + ")'>" + invite.mem_id + "</button></td>";
	        tr_inner += "<td id='td_invmod_authright_" + invite.seq + "'>" + invite.auth_right + "</td>";
	        tr_inner += "<td id='td_invmod_expirydate_" + invite.seq + "'>" + invite.expiry_date + "</td>";
	        elem_tr.innerHTML = tr_inner;
	        list_group.insertBefore(elem_tr, list_group.children[0]);
	    }
	
	    function addItemRow(list_group, item) {
	        const _doc = window.document;
	
	        let list_group_item = _doc.createElement("li");
	        list_group_item.className = "list-group-item d-flex justify-content-between align-items-center";
	
	        let item_div = _doc.createElement("div");
	        item_div.className = "grid gap-2";
	
	        let item_chk = _doc.createElement("input");
	        item_chk.type = "checkbox";
	        item_chk.className = "custom-control-input";
	        item_chk.name = "items[]";
	        item_chk.value = item.seq;
	        item_div.appendChild(item_chk);
	        list_group_item.appendChild(item_div);
	
	        let item_name = _doc.createElement("span");
	        var chk_exp = item.check_expired + 0;
	        if (chk_exp == 0) {
	            item_name.className = "text-danger";
	        } else if (chk_exp == 1) {
	            item_name.className = "text-warning";
	        }
	        item_name.setAttribute("id", "item_" + item.seq);
	        item_name.setAttribute("data-bs-toggle", "modal");
	        item_name.setAttribute("data-bs-target", "#ModalAddItem");
	        let edate_str = " (저장:" + addDatedot(item.insert_date);
	        if (item.expiry_date > 0) {
	            edate_str += ", 만료:" + addDatedot(item.expiry_date);
	        }
	        edate_str += ")";
	        item_name.innerText = " " + item.name + " " + edate_str;
	        item_name.addEventListener('click', () => {
	            itemModify(item.seq);
	        });
	        item_div.appendChild(item_name);
	        list_group_item.appendChild(item_div);
	
	        let item_qty = _doc.createElement("span");
	        item_qty.setAttribute("id", "item_qty_" + item.seq);
	        item_qty.className = "badge bg-primary rounded-pill";
	        item_qty.innerText = item.qty;
	        list_group_item.appendChild(item_qty);
	
	        list_group.appendChild(list_group_item);
	    }
	    
	    //수정된 해당 아이템만 변경
	    function updateItem(resJson) {
	        const _doc = window.document;
	        var item = _doc.box.items[resJson.seqItem];
	        item.name = resJson.itemName;
	        item.qty = resJson.itemQty;
	        item.expiry_date = resJson.itemExpd;
	        item.image_info = resJson.image_info;
	
	        let itemElem = _doc.querySelector("#item_" + resJson.seqItem);
	        let edate_str = " (저장:" + addDatedot(item.insert_date);
	        if (item.expiry_date > 0) {
	            edate_str += ", 만료:" + addDatedot(item.expiry_date);
	        }
	        edate_str += ")";
	        itemElem.innerText = " " + item.name + " " + edate_str;
	
	        _doc.querySelector("#item_qty_" + resJson.seqItem).innerText = item.qty;
	    }
	    //전체 아이템 목록을 갱신
	    function updateItemList(resJson) {
	        const _doc = window.document;
	        _doc.box.items = resJson.items;
	        _doc.querySelector("#item-list").innerHTML = "";
	        let card_outer = _doc.createElement("ul");
	        card_outer.className = "list-group list-group-flush";
	        let keyArr = Object.keys(resJson.items);
	        for (i = 1; i <= keyArr.length; i++) {
	            addItemRow(card_outer, resJson.items[keyArr[i - 1]]);
	        }
	        _doc.querySelector("#item-list").appendChild(card_outer);
	    }
	
	    function createEimdBoxList(boxes, seqCur) {
	        let list_inner = "";
	        for (box of boxes) {
	            if (box.seq == seqCur) {
	                list_inner += "<option value='" + box.seq + "' selected>" + box.name + "</option>";
	            } else {
	                list_inner += "<option value='" + box.seq + "'>" + box.name + "</option>";
	            }
	        }
	        window.document.querySelector("#eimd_to_box").innerHTML = list_inner;
	    }

        function createJtnmdNanumList(nanums) {
            let list_inner = "";
            for (nanum of nanums) {
                list_inner += "<option value='" + nanum.seq + "'>" + nanum.name + "</option>";
            }
            window.document.querySelector("#jtnmd_to_nanum").innerHTML = list_inner;
        }
	</script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>