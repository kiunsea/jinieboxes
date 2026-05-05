<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.omnibuscode.auth.AuthManager" %>
<%@ page import="com.omnibuscode.base.UserSession" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    boolean img_use = true;
    boolean is_partner = false;

    UserSession us = AuthManager.getInstance().getUserSession(request);
    if (us != null) {
        JSONObject storeInfo = us.getDefStoreInfo();
        if (storeInfo != null) {
            Object iuObj = storeInfo.get("img_use");
            if (iuObj == null || !"1".equals(iuObj.toString())) {
                img_use = false;
            }
        }
        is_partner = us.isPartner();
    }

    String quickBtnList = "btn_list_top,btn_item_add,btn_list_collapse,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="list_room" />
        <jsp:param name="title" value="지니박스 보관실(Room) 목록" />
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
            
                <ul class="nav nav-tabs mb-2">
                    <li class="nav-item">
                        <a class="nav-link" href="list_box.jsp">보관함</a>
                    </li>
                    <%-- <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">보관실</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="list_nanum.jsp">나눔함</a>
                    </li> --%>
                </ul>

                <jsp:include page="./include/quick_buttons.jsp">
                    <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
                </jsp:include>
                
                <%-- <hr class="mb-3"> --%>
                <div id="card-row-box" class="row"><span class="mb-2"></span></div>

                <!-- 아이템 추가 팝업 시작 -->
                <input type="text" id="modal_hidden_item_mode" value="ADD" hidden>
                <input type="text" id="modal_hidden_box_seq" hidden>
                <input type="text" id="modal_hidden_item_seq" hidden>
                <div id="ModalAddItem" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addItemModal" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <div>
                                    <p>
                                        <span id="addItemModal" class="modal-title" style="font-size: 1.7em; font-weight: bold;">
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
                                <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <nav>
                                    <div id="modal-nav-tab" class="nav nav-tabs" role="tablist">
                                        <button id="nav-barcode-tab" class="nav-link active" data-bs-toggle="tab" data-bs-target="#nav-barcode" type="button"
                                            role="tab" aria-controls="nav-barcode" aria-selected="true">정보입력</button>
                                        <button id="nav-receipt-tab" class="nav-link" data-bs-toggle="tab" data-bs-target="#nav-receipt" type="button"
                                            role="tab" aria-controls="nav-receipt" aria-selected="false">영수증스캔</button>
                                    </div>
                                </nav>

                                <div class="tab-content" id="nav-tabContent">                                    
                                    <div id="nav-barcode" class="tab-pane fade show active" role="tabpanel" aria-labelledby="nav-barcode-tab" tabindex="0">
                                        <!-- 아이템 바코드 스캔 입력 시작 -->
                                        <div id="mdai_div_barcode_video" class="input-group mb-2">
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
                                                        document.getElementById('mdai_input_barcode').value = result.text
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
                                                                    let resJson = JSON.parse(resMsg);
                                                                    if (resJson.code == '000') {
                                                                        document.querySelector("#mdai_input_itemname").value = resJson.item_name;
                                                                    } else if (resJson.msg) {
                                                                        alert(resJson.msg)
                                                                    }
                                                                }
                                                            }
                                                        };
                                                        sendPost(_ajax, path, params);
                                                    }
                                                    if (err && !(err instanceof ZXing.NotFoundException)) {
                                                        console.error(err)
                                                        document.getElementById('mdai_input_barcode').value = err
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
                                                document.getElementById('mdai_input_barcode').value = '';
                                                console.log('바코드 카메라 꺼짐');
                                            }

                                            //바코드카메라 초기화
                                            barcodeCameraOff();
                                            window.document.bctoggle = false;
                                        </script>
                                        <div id="mdai_div_barcode_btn" class="input-group mb-2">
                                            <span id="barcode_toggle" class="btn btn-primary">바코드</span>
                                            <input type="text" class="form-control" placeholder="스캔하려면 바코드 버튼을 터치" id="mdai_input_barcode" readonly>
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
                                        <!-- 아이템 바코드 스캔 입력 끝 -->
                                        <div class="input-group mb-2">
                                            <span class="input-group-text">이름</span><input type="text"
                                                class="form-control" placeholder="아이템명"
                                                id="mdai_input_itemname">
                                        </div>
                                        <div class="input-group mb-2">
                                            <span class="input-group-text">수량</span>
                                            <input id="mdai_input_qty" type="text" class="form-control" placeholder="1">
                                        </div>
                                        <div class="input-group mb-2">
                                            <span class="input-group-text">박스</span>
                                            <select id="mdai_input_boxseq" class="form-select" aria-label="Default select example">
                                                <!--option value="58">기본 박스</option>
                                                <option value="59">실온실</option>
                                                <option value="60">냉장실</option-->
                                            </select>
                                            <script>
                                                /**
                                                 * 아이템 추가 팝업의 박스 선택 목록 초기화
                                                 */
                                                function initBoxSelect() {
                                                    const _doc = window.document;
                                                    const boxes = _doc.boxes;
                                                    if (boxes) {
                                                        var str_options = "";
                                                        let seqArr = Object.keys(boxes);
                                                        for (let i = 1; i <= seqArr.length; i++) {
                                                            let box = boxes[seqArr[i - 1]];
                                                            str_options += "<option value='" + seqArr[i - 1] + "'>" + box.name + "</option>";

                                                            if (box.type == -1) {
                                                                _doc.seq_defbox = box.seq;
                                                            }
                                                        }
                                                        str_options += "<option value='-2'>-새로등록-</option>";
                                                        const elem_boxlist = _doc.querySelector("#mdai_input_boxseq");
                                                        elem_boxlist.innerHTML = str_options;
                                                    }
                                                }
                                            </script>
                                        </div>
                                        <div id="mdai_div_insd" class="input-group mb-2">
                                            <span class="input-group-text">저장일자</span>
                                            <input id="mdai_input_insd" type="text" class="form-control date datepicker-input" value="." disabled>
                                        </div>
                                        <div id="mdai_div_expd" class="input-group mb-2">
                                            <span class="input-group-text">만료일자</span>
                                            <input id="mdai_input_expd" type="text" class="form-control date datepicker-input" value=".">
                                        </div>
                                        <div class="input-group mb-2">
                                            <span class="input-group-text">상세</span>
                                            <textarea id="mdai_input_detail" class="form-control" aria-label="With textarea"></textarea>
                                        </div>
<% if (img_use) { %>
                                        <!-- 아이템 이미지 출력 시작 -->
                                        <div id="mdai_div_addimg" class="input-group mb-2">
                                            <span id="mdai_span_itemimage" class="input-group-text">사진</span>
                                            <input id="mdai_input_itemimages" type="file" multiple="multiple" class="form-control" placeholder="이미지" accept="image/*">
                                            <button id="mdai_btn_addimg" class="btn btn-outline-secondary" type="button">사진추가</button>
                                        </div>
                                        <div id="mdai_loading_imgs"></div>
                                        <div id="mdai_carousel_imgs" class="carousel slide mb-2">
                                            <div id="mdai_car_indi" class="carousel-indicators">
                                            </div>
                                            <div id="mdai_car_inne" class="carousel-inner">
                                            </div>
                                            <button id="carousel-ctrl-prev-btn-item" class="carousel-control-prev" type="button" data-bs-target="#mdai_carousel_imgs" data-bs-slide="prev">
                                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Previous</span>
                                            </button>
                                            <button id="carousel-ctrl-next-btn-item" class="carousel-control-next" type="button" data-bs-target="#mdai_carousel_imgs" data-bs-slide="next">
                                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Next</span>
                                            </button>
                                        </div>
                                        <button id="mdai_btn_delimg" type="button" class="btn btn-outline-secondary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">사진삭제</button>
                                        <script>
                                            /**
                                             * 아이템 이미지 추가
                                             */
                                            window.document.querySelector("#mdai_btn_addimg").addEventListener('click', () => {
                                                const _doc = window.document;

                                                if (_doc.querySelector("#mdai_input_itemimages").files.length < 1) {
                                                    alert("이미지가 지정되지 않았습니다");
                                                    return -1;
                                                }

                                                let seq_box = _doc.querySelector("#modal_hidden_box_seq").value;
                                                let seq_item = _doc.querySelector("#modal_hidden_item_seq").value;

                                                attachSpinnerButton(_doc.querySelector("#mdai_btn_addimg"));

                                                var path = "/jbs/item";
                                                const params = new Array();
                                                params.push({ name: "cmd", value: "addImg" });
                                                params.push({ name: "buid", value: urlparam("buid") });
                                                params.push({ name: "authcode", value: urlparam("authcode") });
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
                                                                _doc.querySelector("#mdai_input_itemimages").value = "";
                                                                
                                                                var item = _doc.boxes[seq_box].items[seq_item];
                                                                item.image_info = resJson.image_info;
                                                                showItemImages(item);
                                                                alertMsg = resJson.msg;
                                                            } else {
                                                                alertMsg = "* code : " + resJson.code + " (" + resJson.msg + ")\n* 입력값을 확인하여 주세요";
                                                            }

                                                            if (alertMsg)
                                                                alert(alertMsg);

                                                            removeSpinnerButton(_doc.querySelector("#mdai_btn_addimg"), "사진추가");
                                                        }
                                                    }
                                                };

                                                sendMultipart(_ajax,
                                                    path,
                                                    _doc.querySelector("#mdai_input_itemimages").files,
                                                    params);
                                            }, false);

                                            /**
                                             *  아이템 이미지 삭제
                                             */
                                            window.document.querySelector("#mdai_btn_delimg").addEventListener('click', () => {
                                                if (!confirm("현재 이미지를 삭제합니까?")) {
                                                    return -1;
                                                }

                                                const _doc = window.document;
                                                let seq_box = _doc.querySelector("#modal_hidden_box_seq").value;
                                                let seq_item = _doc.querySelector("#modal_hidden_item_seq").value;

                                                const activeItem = document.querySelector('#mdai_carousel_imgs>.carousel-inner>.carousel-item.active');
                                                var imgsrc = activeItem.firstChild.src;
                                                var imgid = activeItem.firstChild.alt;

                                                var path = "/jbs/item";
                                                var params = "cmd=delImg";
                                                params += "&buid=" + urlparam("buid");
                                                params += "&authcode=" + urlparam("authcode");
                                                params += "&seq_box=" + seq_box;
                                                params += "&seq_item=" + seq_item;
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
                                                                var item = _doc.boxes[seq_box].items[seq_item];
                                                                item.image_info = resJson.image_info;
                                                                showItemImages(item);
                                                            }

                                                            if (resJson.msg)
                                                                alert(resJson.msg)
                                                        }
                                                    }
                                                };
                                                sendPost(_ajax, path, params);
                                            }, false);
                                            /**
                                             * 아이템 이미지를 출력 및 갱신
                                             */
                                            function showItemImages(item) { // 아이템 이미지를 출력 및 갱신

                                                var carousel_prev = window.document.querySelector("#carousel-ctrl-prev-btn-item");
                                                var carousel_next = window.document.querySelector("#carousel-ctrl-next-btn-item");
                                                hideElement(carousel_prev); hideElement(carousel_next);

                                                //storage type 에 따라 google drive 인 경우 file id 로 url 을 취하여 imgs 에 적용
                                                if (item.image_info.storage_type == '1') {
                                                    let gd_files = item.image_info.files;
                                                    if (gd_files && gd_files.length > 0) {      
                                                        attachSpinnerDiv(window.document.querySelector("#mdai_loading_imgs"), "("+gd_files.length+" images)");
                                                        const gda = new GoogleDriveAccess(item.image_info.access_token);
                                                        gda.fetchMultiple(gd_files).then(results => {
                                                            // 이곳에서 results 배열을 이용하여 원하는 연산을 수행
                                                            item.image_info.imgs = results;
                                                            applyItemImages(item);
                                                        }).catch(error => {
                                                            console.error("An error occurred:", error);
                                                        });
                                                    }
                                                } else if (item.image_info.imgs && item.image_info.imgs.length > 0) {
                                                    attachSpinnerDiv(window.document.querySelector("#mdai_loading_imgs"), "("+item.image_info.imgs.length+" images)");
                                                    //service local storage 에서 이미지 로드시 바로 url 접근
                                                    applyItemImages(item);
                                                }
                                            }
                                            function applyItemImages(item) {
                                                const _doc = window.document;

                                                var carousel_prev = _doc.querySelector("#carousel-ctrl-prev-btn-item");
                                                var carousel_next = _doc.querySelector("#carousel-ctrl-next-btn-item");
                                                hideElement(carousel_prev); hideElement(carousel_next);
                                                
                                                var imgs = item.image_info.imgs;                                                 
                                                if (imgs && imgs.length > 0) {
                                                    _doc.querySelector("#mdai_carousel_imgs").hidden = false;
                                                    _doc.querySelector("#mdai_btn_delimg").hidden = false;
                                                    var img_cindi = _doc.querySelector("#mdai_car_indi");
                                                    var img_cinne = _doc.querySelector("#mdai_car_inne");
                                                    img_cindi.innerHTML = img_cinne.innerHTML = "";
                                                    
                                                    var cnt_btn = 0;
                                                    for (img of imgs) {
                                                        let btn_slide = _doc.createElement("button");
                                                        btn_slide.setAttribute("type", "button");
                                                        btn_slide.setAttribute("data-bs-target", "#mdai_carousel_imgs");
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
                                                    hideElement(_doc.querySelector("#mdai_carousel_imgs")); 
                                                    hideElement(_doc.querySelector("#mdai_btn_delimg"));
                                                }

                                                removeSpinnerDiv(window.document.querySelector("#mdai_loading_imgs"));
                                            }
                                        </script>
                                        <!-- 아이템 이미지 출력 끝 -->
<% } %>
                                    </div>
                                    <script>barcodeScanInit();</script>                                    
                                    <div id="nav-receipt" class="tab-pane fade" role="tabpanel" aria-labelledby="nav-receipt-tab" tabindex="0">
                                        준비중...
                                    </div>
                                </div>
                                <div id="mdai_add_cont_div">
                                    <input class="form-check-input" type="checkbox" id="mdai_add_continue">
                                    <span id="mdai_add_cont_label">계속 입력</span>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary"
                                    id="mdai_btn_add">추가</button>
                                <button type="button" class="btn btn-primary"
                                    id="mdai_btn_mod">수정</button>
                                <button type="button" class="btn btn-primary"
                                    id="mdai_btn_del">삭제</button>
                                <button type="button" class="btn btn-secondary" id="mdai_btn_cancel"
                                    data-bs-dismiss="modal">닫기</button>
                            </div>
                        </div>
                    </div>
                </div>
                <script>
                    showDatepicker(document.querySelector('#mdai_input_expd'));
                    /**
                     * 아이템 추가 팝업
                     */
                    function initItemAddPopup() {
                        const _doc = window.document;

                        //화면 초기화
                        _doc.querySelector("#modal_hidden_item_mode").value = "ADD";
                        _doc.querySelector("#modal_hidden_box_seq").value = "";
                        _doc.querySelector("#modal_hidden_item_seq").value = "";
                        _doc.querySelector("#addItemModal").innerText = "아이템 추가";
                        _doc.querySelector("#mdai_input_boxseq").value = "";
                        _doc.querySelector("#mdai_input_itemname").value = "";
                        _doc.querySelector("#mdai_input_qty").value = "1";
                        _doc.querySelector("#mdai_input_expd").value = "";
                        _doc.querySelector("#mdai_add_continue").checked = false;
                        _doc.querySelector("#mdai_div_insd").hidden = true;
                        _doc.querySelector("#mdai_btn_add").hidden = false;
                        _doc.querySelector("#mdai_btn_mod").hidden = true;
                        _doc.querySelector("#mdai_btn_del").hidden = true;
                        _doc.querySelector("#mdai_add_continue").hidden = false;
                        _doc.querySelector("#mdai_add_cont_label").hidden = false;
<% if (img_use) { %>
                        _doc.querySelector("#mdai_span_itemimage").hidden = false;
                        _doc.querySelector("#mdai_btn_addimg").hidden = true;
                        _doc.querySelector("#mdai_btn_delimg").hidden = true;
                        _doc.querySelector("#mdai_carousel_imgs").hidden = true;
<% } %>                        
                        initBoxSelect();

                        _doc.querySelector("#mdai_input_boxseq").disabled = false;
                        _doc.querySelector("#nav-receipt-tab").hidden = false;
                        _doc.querySelector("#nav-receipt").hidden = false;
                        _doc.querySelector("#addItemModal").innerText = "[박스] 아이템 추가";
                    }
                    /**
                     * 아이템 추가
                     */
                    window.document.querySelector("#mdai_btn_add").addEventListener('click', () => {

                        const _doc = window.document;

                        var path = "/jbs/item";
                        const params = new Array();
                        params.push({name:"cmd", value:"store"});
                        params.push({name:"buid", value:urlparam("buid")});
                        params.push({name:"authcode", value:urlparam("authcode")});

                        var box_seq = _doc.querySelector("#mdai_input_boxseq").value;
                        var item_name = _doc.querySelector("#mdai_input_itemname").value;
                        var item_qty = _doc.querySelector("#mdai_input_qty").value;
                        var item_iexpd = _doc.querySelector("#mdai_input_expd").value;
                        var item_detail = _doc.querySelector("#mdai_input_detail").value;

                        if (box_seq) {
                            params.push({name:"seq_box", value:box_seq});
                        } else {
                            params.push({name:"seq_box", value:_doc.seq_defbox});
                        }
                        params.push({name:"item_box_detail", value:item_detail});
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

                        attachSpinnerButton(_doc.querySelector("#mdai_btn_add"));

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
                                        _doc.querySelector("#mdai_input_boxseq").value = "";
                                        _doc.querySelector("#mdai_input_itemname").value = "";
                                        _doc.querySelector("#mdai_input_qty").value = "1";
                                        _doc.querySelector("#mdai_input_insd").value = "";
                                        _doc.querySelector("#mdai_input_expd").value = "";
                                        alertMsg = resJson.msg;
                                    } else {
                                        alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                    }
                                    if (alertMsg) {
                                        alert(alertMsg);
                                    }
                                    removeSpinnerButton(_doc.querySelector("#mdai_btn_add"));
                                    if (!_doc.querySelector("#mdai_add_continue").checked) {
                                        //bootstrap.Modal.getInstance(window.document.querySelector("#ModalAddItem")).hide();
                                        //TODO 2024.07.10 서버에 저장된 아이템 정보로 화면에 반영해야 하는데 그냥 refresh 해서 초기화를 타게 한다(엘리먼트 찾아서 수정하기 구찮음)
                                        window.location.replace("/jbs");
                                    }
                                }
                            }
                        };
                        var itemimages = _doc.querySelector("#mdai_input_itemimages");
                        sendMultipart(_ajax, 
                            path, 
                            itemimages ? itemimages.files : null,
                            params);

                    }, false);
                    /**
                    * 아이템 수정 팝업
                    */
                    function boxItemModify(seqItem, seqBox) {

                        const _doc = window.document;

                        //화면 초기화
                        _doc.querySelector("#addItemModal").innerText = "아이템 수정";
                        _doc.querySelector("#mdai_div_insd").hidden = false;
                        _doc.querySelector("#mdai_btn_add").hidden = true;
                        _doc.querySelector("#mdai_btn_mod").hidden = false;
                        _doc.querySelector("#mdai_btn_del").hidden = false;
                        _doc.querySelector("#mdai_add_continue").hidden = true;
                        _doc.querySelector("#mdai_add_cont_label").hidden = true;
<% if (img_use) { %>
                        _doc.querySelector("#mdai_car_indi").innerHTML = null;
                        _doc.querySelector("#mdai_car_inne").innerHTML = null;
                        _doc.querySelector("#mdai_span_itemimage").hidden = true;
                        _doc.querySelector("#mdai_btn_addimg").hidden = false;
<% } %>

                        initBoxSelect();

                        _doc.querySelector("#mdai_input_boxseq").disabled = false;
                        _doc.querySelector("#nav-receipt-tab").hidden = false;
                        _doc.querySelector("#nav-receipt").hidden = false;
                        _doc.querySelector("#addItemModal").innerText = "[박스] 아이템 수정";

                        //alert(JSON.stringify(_doc.boxes));

                        // BOX 참조값 초기화
                        var box = _doc.boxes[seqBox];
                        var item = box.items[seqItem];

                        _doc.querySelector("#modal_hidden_item_mode").value = "MODIFY";
                        _doc.querySelector("#modal_hidden_box_seq").value = seqBox;
                        _doc.querySelector("#modal_hidden_item_seq").value = item.seq;
                        _doc.querySelector("#mdai_input_boxseq").value = seqBox;
                        _doc.querySelector("#mdai_input_itemname").value = item.name;
                        _doc.querySelector("#mdai_input_qty").value = item.qty;
<% if (img_use) { %>
                        _doc.querySelector("#mdai_input_itemimages").value = "";
<% } %>
                        _doc.querySelector("#mdai_input_detail").value = item.detail ? item.detail : "";
                        let iinsd = item.insert_date;
                        if (iinsd > 0) {
                            _doc.querySelector("#mdai_input_insd").value = addDatedot(iinsd);
                        } else {
                            _doc.querySelector("#mdai_input_expd").value = ".";
                        }
                        let iexpd = item.expiry_date;
                        if (iexpd > 0) {
                            _doc.querySelector("#mdai_input_expd").value = addDatedot(iexpd);
                        } else {
                            _doc.querySelector("#mdai_input_expd").value = ".";
                        }
<% if (img_use) { %>
                        showItemImages(item);
<% } %>
                    }
                    /**
                     * 아이템 수정
                     */
                    window.document.querySelector("#mdai_btn_mod").addEventListener('click', () => {

                        const _doc = window.document;

                        var path = "/jbs/item";
                        const params = new Array();
                        params.push({name:"cmd", value:"store"});
                        params.push({name:"buid", value:urlparam("buid")});
                        params.push({name:"authcode", value:urlparam("authcode")});

                        //수정 여부 확인을 위한 기존값 추출
                        var seq_box = _doc.querySelector("#modal_hidden_box_seq").value;
                        var box = _doc.boxes[seq_box];
                        var item;
                        var seq_item = _doc.querySelector("#modal_hidden_item_seq").value;
                        if (seq_item) {
                            params.push({name:"seq_item", value:seq_item});
                            item = box.items[seq_item];
                        } else {
                            alert("아이템이 선택되지 않았습니다");
                            return -1;
                        }
                        
                        var box_seq = _doc.querySelector("#mdai_input_boxseq").value;
                        if (box_seq) {
                            params.push({name:"seq_box", value:box_seq});
                        } else {
                            params.push({name:"seq_box", value:_doc.seq_defbox});
                        }

                        params.push({name:"item_name", value:_doc.querySelector("#mdai_input_itemname").value});
                        params.push({name:"item_qty", value:_doc.querySelector("#mdai_input_qty").value});
                        var iexpd = _doc.querySelector("#mdai_input_expd").value;
                        if (iexpd) {
                            params.push({name:"item_expd", value:delDatedot(iexpd)});
                        }
                        var item_detail = _doc.querySelector("#mdai_input_detail").value;
                        if (item.detail != item_detail) {
                            params.push({name:"item_box_detail", value:item_detail});
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
                                        _doc.querySelector("#mdai_input_boxseq").value = "";
                                        _doc.querySelector("#mdai_input_itemname").value = "";
                                        _doc.querySelector("#mdai_input_qty").value = "1";
                                        _doc.querySelector("#mdai_input_insd").value = "";
                                        _doc.querySelector("#mdai_input_expd").value = "";
                                        _doc.querySelector("#mdai_input_itemimages").value = "";

                                        let seq_prev_box = _doc.querySelector("#modal_hidden_box_seq").value;
                                        var item = _doc.boxes[seq_prev_box].items[resJson.seq_item];
                                        if (item) {
                                            item.name = resJson.item_name;
                                            item.qty = resJson.item_qty;
                                            item.expiry_date = resJson.item_expd;
                                            item.imgs = resJson.image_info.imgs;
                                            item.seq_box = resJson.seq_box;
                                            _doc.boxes[resJson.seq_box].items[resJson.seq_item] = item;
                                            delete _doc.boxes[seq_prev_box].items[resJson.seq_item];
                                        }

                                        updateItemList(item);
                                        alertMsg = resJson.msg;
                                    } else {
                                        alertMsg = "* " + resJson.msg + "\n* 입력값을 확인하여 주세요";
                                    }
                                    if (alertMsg) {
                                        alert(alertMsg);
                                    }
                                    bootstrap.Modal.getInstance(window.document.querySelector("#ModalAddItem")).hide();
                                }
                            }
                        };
                        sendMultipart(_ajax, 
                            path, 
                            _doc.querySelector("#mdai_input_itemimages").files,
                            params);
                    }, false);
                    /**
                     * 아이템 삭제
                     */
                    window.document.querySelector("#mdai_btn_del").addEventListener('click', () => {
                        const _doc = window.document;

                        var path = "/jbs/item";
                        var params = "cmd=take";
                        params += "&buid=" + urlparam("buid");
                        params += "&authcode=" + urlparam("authcode");
                        params += "&seq_box=" + urlparam("seq_box");
                        let seq_item = _doc.querySelector("#modal_hidden_item_seq").value;
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
                                        let seq_item = _doc.querySelector("#modal_hidden_item_seq").value;
                                        let seq_box = _doc.querySelector("#modal_hidden_box_seq").value;
                                        delete _doc.boxes[seq_box].items[seq_item];
                                        var item_list = _doc.querySelector("#multicollapse_carditem_" + seq_box);
                                        var item_row = _doc.querySelector("#item_" + seq_item);
                                        item_list.removeChild(item_row); // row 삭제
                                        var box_badge = getOrCreateCardbadge(seq_box);
                                        box_badge.innerText = (+box_badge.innerText) - 1; // 뱃지 카운트 감소
                                        bootstrap.Modal.getInstance(window.document.querySelector("#ModalAddItem")).hide();
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
                     * 팝업 show시의 처리 (https://getbootstrap.com/docs/5.3/components/modal/#events)
                     */
                    window.document.querySelector("#ModalAddItem").addEventListener('show.bs.modal', () => {
                        //초기화
                        removeSpinnerDiv(window.document.querySelector("#mdai_loading_imgs"));
					}, false);
                    /**
                     * 팝업 hidden시의 처리 (https://getbootstrap.com/docs/5.3/components/modal/#events)
                     */
					window.document.querySelector("#ModalAddItem").addEventListener('hidden.bs.modal', () => {
						barcodeCameraOff();
					}, false);
                </script>
                <!-- 아이템 추가 팝업 끝 -->

                <!-- 박스 추가 팝업 시작 -->
                <div id="ModalShowAddBox" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addBoxModal" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <div>
                                    <h5 class="modal-title" id="addBoxModal">박스 추가</h5>
                                </div>
                                <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <div class="input-group mb-2">
                                    <span class="input-group-text">이름</span>
                                    <input type="text" class="form-control" placeholder="박스이름" id="abmd_input_name">
                                </div>
                                <div class="input-group mb-2">
                                    <span class="input-group-text">설명</span>
                                    <textarea class="form-control" aria-label="BOX Detail" id="abmd_input_details"></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary" id="abmd_btn_add">추가</button>
                                <button type="button" class="btn btn-secondary" id="abmd_btn_cancel" data-bs-dismiss="modal">닫기</button>
                            </div>
                        </div>
                    </div>
                </div>
                <script>
                    /**
                     * 박스 추가 팝업
                     */
                    window.document.querySelector("#mdai_input_boxseq").addEventListener('change', () => {
                        const _doc = window.document;

                        let box_seq = _doc.querySelector("#mdai_input_boxseq").value;
                        if (box_seq == -2) {
                            _doc.querySelector("#addBoxModal").innerText = "박스 추가";
                            _doc.querySelector("#abmd_input_name").value = "";
                            _doc.querySelector("#abmd_input_details").value = "";
                            bootstrap.Modal.getOrCreateInstance(document.getElementById('ModalShowAddBox')).show();
                        }
                    }, false);
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

                        var path = "/jbs/box";
                        var params = "cmd=add";
                        params += "&buid=" + urlparam("buid");
                        params += "&authcode=" + urlparam("authcode");
                        params += "&seq_store=" + urlparam("seq_store");
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
                                        _doc.querySelector("#abmd_input_name").value = "";
                                        _doc.querySelector("#abmd_input_details").value = "";
                                        updateBoxList(resJson);
                                        bootstrap.Modal.getOrCreateInstance(document.getElementById('ModalShowAddBox')).hide();
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
                </script>
                <!-- 박스 추가 팝업 끝 -->
            </main>
        </div>
    </div>

    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>
    
	<script>
	    window.addEventListener('load', () => {
	        fetchShopOrders();
	        initializeBoxes();
	    }, false);
	
	    /**
	     * box 목록 초기화
	     */
	    function initializeBoxes() {
	        const _doc = window.document;
	
	        var path = "/jbs/box";
	        var params = "cmd=list";
	        params += "&buid=" + urlparam("buid");
	        params += "&authcode=" + urlparam("authcode");
	        params += "&seq_store=" + urlparam("seq_store");
	
	        var _ajax = new XMLHttpRequest();
	        _ajax.onreadystatechange = function () {
	            if (checkAjaxSuc(_ajax)) {
	                var resMsg = _ajax.responseText;
	                if (resMsg.length > 0) {
	                    //alert(resMsg);
	                    let resJson = JSON.parse(resMsg);
	                    if (resJson.code == '000') {
	                        _doc.first_visit = resJson.first_visit;
	                        let boxes = _doc.boxes = resJson.boxes;
	                        createBoxcardRows(boxes, resJson.buid);
	                        var jinievisited = localStorage.getItem("jbs_visited_onboard");
	                        if (!jinievisited) {
	                            //사용자 온보딩 시나리오
	                            if (stepbystep) {
	                                if (confirm("사용자 가이드를 시작할까요?")) {
	                                    stepbystep();                                       
	                                }
	                                localStorage.setItem("jbs_visited_onboard", true);
	                            }
	                        }
	
	                    } else {
	                        authFailCheck(resJson.code, resJson.msg);
	                    }
	                }
	            }
	        };
	        sendPost(_ajax, path, params);
	
	        //collapse 버튼을 box item collapse 로 변경
	        var btn_coll = window.document.querySelector("#btn_list_collapse");
	        btn_coll.setAttribute("data-bs-target", ".multi-collapse");
	        // btn_coll.setAttribute("aria-controls", ""); //box는 multi-collapse 이기 때문에 이 속성을 createBoxcardRows() 함수에서 처리해야함
	    }
	
	    /**
	     * boxcard list 생성 (이미 있는 경우엔 재생성)
	     */
	    function createBoxcardRows(boxes, buid) {
	        window.document.querySelector("#card-row-box").innerHTML = "";
	
	        if (boxes) {
	            let aria_controls_value = "";
	            let seqArr = Object.keys(boxes);
	            for (let i = 1; i <= seqArr.length; i++) {
	                let box = boxes[seqArr[i - 1]];
	                createBoxcard(box, buid);
	                aria_controls_value += " multicollapse_carditem_" + box.seq;
	            }
	            if (seqArr.length > 0) {
	                window.document.querySelector("#btn_list_collapse").setAttribute("aria-controls", aria_controls_value);
	            }
	        }
	    }
	
	    function createBoxcard(box, buid) {
	        const _doc = window.document;
	
	        var seqBox = box.seq;
	        var name = box.name;
	        var items = box.items;
	
	        let card_outer = _doc.createElement("div");
	        card_outer.className = "col-sm-6 mb-3 mb-sm-3";
	        _doc.querySelector("#card-row-box").appendChild(card_outer);
	
	        let card = _doc.createElement("div");
	        card.setAttribute("id", "boxcard_" + seqBox);
	        card.className = "card";
	        card_outer.appendChild(card);
	
	        showBoxImages(box, card);
	
	        let card_body = _doc.createElement("div");
	        card_body.setAttribute("id", "boxcardbody_" + seqBox);
	        card_body.className = "card-body btn-toolbar justify-content-between";
	        card.appendChild(card_body);
	
	        let card_btntitle = _doc.createElement("button");
	        card_btntitle.type = "button";
	        card_btntitle.className = "btn btn-lg btn-light";
	        card_btntitle.innerText = name;
	        card_btntitle.addEventListener('click', event => {
	            window.location.href = "/jbs/box_info.jsp?seq_box=" + seqBox + "&buid=" + buid;
	        });
	        card_body.appendChild(card_btntitle);
	
	        if (items) {
	            //items 정렬(기준:insert_date DESC) - 날짜 기준 내림차순 정렬
	            const entries = Object.entries(items);
	            entries.sort((a, b) => b[1].insert_date - a[1].insert_date); // DESC: 최신순
	            
	            if (entries.length > 0) {
	                let card_btnbadge = _doc.createElement("button");
	                card_btnbadge.setAttribute("id", "box_badge_" + seqBox);
	                card_btnbadge.type = "button";
	                card_btnbadge.className = "btn";
	                card_btnbadge.style = "position: absolute; right: 60px; --bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
	                card_btnbadge.innerHTML = "<span class='badge text-bg-secondary' id='badge_cnt_" + seqBox + "'>" + entries.length + "</span>";
	                card_btnbadge.setAttribute("data-bs-toggle", "collapse");
	                card_btnbadge.setAttribute("data-bs-target", "#multicollapse_carditem_" + seqBox);
	                card_btnbadge.setAttribute("aria-expanded", "false");
	                card_btnbadge.setAttribute("aria-controls", "multicollapse_carditem_" + seqBox);
	                card_body.appendChild(card_btnbadge);
	
	                let list_group = _doc.createElement("ul");
	                list_group.className = "list-group list-group-flush collapse multi-collapse"; // box 의 아이템 목록을 collapse
	                list_group.setAttribute("id", "multicollapse_carditem_" + seqBox);
	                card.appendChild(list_group);
	
	                // 정렬된 entries 배열을 직접 사용하여 순서대로 출력
	                for (let i = 0; i < entries.length; i++) {
	                    let item = entries[i][1]; // entries[i] = [key, value], entries[i][1] = value (item 객체)
	                    addItemRow(list_group, seqBox, item);
	                }
	            }
	        }
	
	        let card_empty = _doc.createElement("div");
	        card_empty.style.width = "60px";
	        card.appendChild(card_empty);
	    }
	
	    function showBoxImages(box, card) {
	        if (box.type == -1 && box.image_info) { // 등록대기 박스인 경우
	            box.image_info.imgs = [{src:"res/web/images/containing_items.jpeg"}];
	            applyBoxImages(box, card);
	        } else {
<% if (img_use) { %>
	            if (box.image_info.storage_type == '1') { // 외부 저장소(구글 등)를 이용하는 경우
	                let gd_files = box.image_info.files;
	                if (gd_files && gd_files.length > 0) {
	                    const gda = new GoogleDriveAccess(box.image_info.access_token);
	                    gda.fetch(gd_files[0]).then(result => {
	                        box.image_info.imgs = [result];
	                        applyBoxImages(box, card);
	                    }).catch(error => {
	                        console.error("An error occurred:", error);
	                    });
	                }
	            } else {
	                //service local storage 에서 이미지 로드시 바로 url 접근
	                applyBoxImages(box, card);
	            }
<% } %>
	        }
	    }
	    function applyBoxImages(box, card) {
	        var image_info = box.image_info;
	        if (image_info && image_info.imgs && image_info.imgs.length > 0) {
	            card.style = "background : url('" + image_info.imgs[0].src + "'); background-size : cover; background-repeat : no-repeat";
	        }
	    }
	    
	    function addItemRow(list_group, seqBox, item) {
	        const _doc = window.document;
	
	        var list_group_item = _doc.createElement("li");
	        list_group_item.setAttribute("id", "item_" + item.seq);
	        list_group_item.className = "list-group-item d-flex justify-content-between align-items-center";
	
	        var item_name = _doc.createElement("span");
	        var chk_exp = item.check_expired + 0;
	        if (chk_exp == 0) {
	            item_name.className = "text-danger";
	        } else if (chk_exp == 1) {
	            item_name.className = "text-warning";
	        }
	        item_name.setAttribute("id", "item_name_" + item.seq);
	        item_name.setAttribute("data-bs-toggle", "modal");
	        item_name.setAttribute("data-bs-target", "#ModalAddItem");
	        var edate_str = " (저장:" + addDatedot(item.insert_date);
	        if (item.expiry_date > 0) {
	            edate_str += ", 만료:" + addDatedot(item.expiry_date);
	        }
	        edate_str += ")";			
	        item_name.innerText = " " + item.name + " " + edate_str;
	        item_name.addEventListener('click', () => {
	            boxItemModify(item.seq, seqBox);
	        });
	        list_group_item.appendChild(item_name);
	
	        var item_qty = _doc.createElement("span");
	        item_qty.setAttribute("id", "item_qty_" + item.seq);
	        item_qty.className = "badge bg-primary rounded-pill";
	        item_qty.innerText = item.qty;
	        list_group_item.appendChild(item_qty);
	
	        list_group.appendChild(list_group_item);
	    }
	
	    function updateItemList(item) {
	        const _doc = window.document;
	        let seq_prev_box = _doc.querySelector("#modal_hidden_box_seq").value;
	        if (seq_prev_box != item.seq_box) {
	            var prev_list = _doc.querySelector("#multicollapse_carditem_" + seq_prev_box);
	            // var dest_list = _doc.querySelector("#multicollapse_carditem_" + item.seq_box);
	            var dest_list = getOrCreateCarditem(item.seq_box);
	            var item_row = _doc.querySelector("#item_" + item.seq);
	            dest_list.appendChild(prev_list.removeChild(item_row));
	
	            var prev_box_badge = _doc.querySelector("#badge_cnt_" + seq_prev_box);
	            // var dest_box_badge = _doc.querySelector("#badge_cnt_" + item.seq_box);
	            var dest_box_badge = getOrCreateCardbadge(item.seq_box);
	            prev_box_badge.innerText = (+prev_box_badge.innerText) - 1;
	            dest_box_badge.innerText = (+dest_box_badge.innerText) + 1;
	
	            if ((+prev_box_badge.innerText) < 1) {
	                var itemCollapse = bootstrap.Collapse.getInstance(document.querySelector("#multicollapse_carditem_" + seq_prev_box));
	                if (itemCollapse) {
	                    itemCollapse.hide();
	                }
	            }
	        }
	    }
	
	    /**
	     * Carditem 을 찾는다 (생성되어 있지 않은 경우 동적으로 생성하여 반환)
	     */
	    function getOrCreateCarditem(seqBox) {
	        const _doc = window.document;
	        var list_group = _doc.querySelector("#multicollapse_carditem_" + seqBox);
	        if (!list_group) {
	            var card = _doc.querySelector("#boxcard_" + seqBox);
	            list_group = _doc.createElement("ul");
	            list_group.className = "list-group list-group-flush collapse multi-collapse"; // box 의 아이템 목록을 collapse
	            list_group.setAttribute("id", "multicollapse_carditem_" + seqBox);
	            card.appendChild(list_group);
	        }
	        return list_group;
	    }
	
	    /**
	     * Cardbadge 를 찾는다 (생성되어 있지 않은 경우 동적으로 생성하여 반환)
	     */
	    function getOrCreateCardbadge(seqBox) {
	        const _doc = window.document;
	        var badge_cnt_ = _doc.querySelector("#badge_cnt_" + seqBox);
	        if (!badge_cnt_) {
	            var card_body = _doc.querySelector("#boxcardbody_" + seqBox);
	            var card_btnbadge = _doc.createElement("button");
	            card_btnbadge.setAttribute("id", "box_badge_" + seqBox);
	            card_btnbadge.type = "button";
	            card_btnbadge.className = "btn";
	            card_btnbadge.style = "position: absolute; right: 60px; --bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
	            card_btnbadge.innerHTML = "<span class='badge text-bg-secondary' id='badge_cnt_" + seqBox + "'>0</span>";
	            card_btnbadge.setAttribute("data-bs-toggle", "collapse");
	            card_btnbadge.setAttribute("data-bs-target", "#multicollapse_carditem_" + seqBox);
	            card_btnbadge.setAttribute("aria-expanded", "false");
	            card_btnbadge.setAttribute("aria-controls", "multicollapse_carditem_" + seqBox);
	            card_body.appendChild(card_btnbadge);
	
	            badge_cnt_ = _doc.querySelector("#badge_cnt_" + seqBox);
	        }
	        return badge_cnt_;
	    }
	
	
	    function updateBoxList(resJson) {
	        const _doc = window.document;
	        
	        if (resJson.boxes) { //전체 목록을 갱신
	            let boxes = _doc.boxes = resJson.boxes;
	            createBoxcardRows(boxes, resJson.buid);
	            initBoxSelect();
	        }
	    }
	    
	    function findSeqBox(box_name) {
	        if (boxes) {
	            let boxes = window.document.boxes;                            
	            let seqArr = Object.keys(boxes);
	            for (let i = 1; i <= seqArr.length; i++) {
	                let box = boxes[seqArr[i - 1]];
	                if (box.name == box_name) {
	                    return box.seq;
	                }
	            }
	        }
	        return -1;
	    }
	</script>
        
    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>