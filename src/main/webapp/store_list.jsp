<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="com.omnibuscode.auth.AuthManager" %>
<%@ page import="com.omnibuscode.base.UserSession" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    boolean is_partner = false;	
    UserSession us = AuthManager.getInstance().getUserSession(request);
    if (us != null) {
		is_partner = us.isPartner();
    }

    String quickBtnList = "btn_list_top,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="store_list" />
        <jsp:param name="title" value="지니박스 저장소(Store) 목록" />
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
                    <h2>저장소(Store) 목록</h2>
                    <div id="card-row" class="row">
                        <div class="col-sm-12 mb-3 mb-sm-3">
                            <div id="card" class="card">
                                <img src="/jbs/res/web/images/stores.jpeg" class="card-img-top mb-3" alt="shopping_items">
                                <div class="card-body">
                                    <p id="card-text" class="card-text">나의 저장소와 공유 받은 저장소 목록입니다.</p>
                                </div>
                            </div>
                        <!-- #card template#
            <div class="col-sm-6 mb-3 mb-sm-3">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title">Card title</h5>
                  <p class="card-text">Some quick example text to build on the card title and make up the bulk of the
                    card's
                    content.</p>
                </div>
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center">An item<span
                      class="badge bg-primary rounded-pill">14</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span
                      class="badge bg-primary rounded-pill">2</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span
                      class="badge bg-primary rounded-pill">1</span></li>
                </ul>
              </div>
            </div>
            <div class="col-sm-6 mb-3 mb-sm-3">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title">Card title</h5>
                  <p class="card-text">Some quick example text to build on the card title and make up the bulk of the
                    card's
                    content.</p>
                </div>
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center">An item<span
                      class="badge bg-primary rounded-pill">14</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span
                      class="badge bg-primary rounded-pill">2</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span
                      class="badge bg-primary rounded-pill">1</span></li>
                </ul>
              </div>
            </div>
            <div class="col-sm-6 mb-3 mb-sm-3">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title">Card title</h5>
                  <p class="card-text">Some quick example text to build on the card title and make up the bulk of the
                    card's
                    content.</p>
                </div>
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center">An item<span
                      class="badge bg-primary rounded-pill">14</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span
                      class="badge bg-primary rounded-pill">2</span></li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span
                      class="badge bg-primary rounded-pill">1</span></li>
                </ul>
              </div>
            </div>
          -->
                        </div>
                    </div>

                    <hr class="mb-4">
                    <div class="mb-3">
                        <label for="inviteCode">공유 목록</label>
                        <ul id="sharedlist" class="list-group list-group-flush">
                            <!--li class="list-group-item d-flex justify-content-between align-items-center">UNISOTRE<span
                                    class="badge bg-primary rounded-pill">F</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">BOMISTORE - SORABOX<span
                                    class="badge bg-primary rounded-pill">G</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">BOMISTORE - DORIBOX<span
                                    class="badge bg-primary rounded-pill">G</span></li-->
                        </ul>
                    </div>                        
                    <div class="row mb-2">
                        <div class="col">
                            <input type="text" class="form-control" id="inviteCode" placeholder="초대코드 입력">
                        </div>
                        <div class="col">
                            <button id="btn_shareadd" class="btn btn-primary btn-sm btn-block" type="button">공유 추가</button>
                        </div>
                    </div>
                    <script>
                        document.querySelector("#btn_shareadd").addEventListener('click', function (event) {
                            // var val_juid = document.querySelector("#juid").value;
                            var val_icode = document.querySelector("#inviteCode").value;

                            if (!val_icode) {
                                alert("초대 받은 코드를 입력해주세요");
                                return -1;
                            }

                            var path = "/jbs/share";
                            var params = "cmd=add";
                            if (urlparam("buid")) params += "&buid=" + urlparam("buid");
                            params += "&authcode=" + urlparam("authcode");
                            // params += "&juid=" + val_juid;
                            params += "&icode=" + val_icode;

                            var _ajax = new XMLHttpRequest();
                            _ajax.onreadystatechange = function () {
                                if (checkAjaxSuc(_ajax)) {
                                    var resMsg = _ajax.responseText;
                                    if (resMsg.length > 0) {
                                        let resJo = JSON.parse(resMsg);
                                        alert(resJo.msg);
                                        if (resJo.code == '000')
                                            window.location.reload(false);
                                    }
                                }
                            };
                            sendPost(_ajax, path, params);
                        }, false);
                    </script>
                </div>

            </main>
        </div>
    </div>

    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script>
        window.addEventListener('load', () => {
            const _doc = window.document;

            var path = "/jbs/store";
            var params = "cmd=list";
            params += "&buid=" + urlparam("buid");
            params += "&authcode=" + urlparam("authcode");
            params += "&seq_owner=" + urlparam("seq_owner");

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        if (resJo.code == '000') {
                            let storesJo = resJo.stores;
                            if (storesJo) {
                                for (store of storesJo) {
                                    //createStorecard(resJo.buid, store.seq, store.def_check, store.name, store.details, store.insert_time);
                                    createStorecard(resJo, store);
                                }
                            } else if (resJo.msg) {
                                alert(resJo.msg);
                            }

                            var list_group = _doc.querySelector("#sharedlist");
                            for (sharedinfo of resJo.sharedlist) {
                                //TODO box_info.jsp 에서 addInviteRow() 함수를 참고하여 나머지 구현
                                var list_group_item = _doc.createElement("li");
                                list_group_item.className = "list-group-item d-flex justify-content-between align-items-center";
                                list_group_item.innerHTML = sharedinfo.name + (sharedinfo.name_box ? " - " + sharedinfo.name_box : "") + "<span class='badge bg-primary rounded-pill' >" + sharedinfo.authority + "</span >";
                                list_group.appendChild(list_group_item);
                            }
                        } else {
                            authFailCheck(resJo.code, resJo.msg);
                        }
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        // function createStorecard(buid, seq, defcheck, name, details, instime) {
        function createStorecard(resJo, store) {
            const _doc = window.document;

            var buid = resJo.buid;
            var seq = store.seq;
            var defcheck = store.def_check;
            var name = store.name;
            var details = store.details;
            var instime = store.insert_time;
            var mystore = store.my_store;

            let card_outer = _doc.createElement("div");
            card_outer.className = "col-sm-6 mb-3 mb-sm-3";
            _doc.querySelector("#card-row").appendChild(card_outer);

            let card = _doc.createElement("div");
            card.className = "card";
            card_outer.appendChild(card);

            let card_body = _doc.createElement("div");
            card_body.className = "card-body";
            card.appendChild(card_body);

            let card_title = _doc.createElement("h5");
            card_title.className = "card-title";
            card_title.innerText = name + (mystore ? " (My Store)" : "") + " ";
            card_body.appendChild(card_title);

            let card_detail_btn = _doc.createElement("button");
            card_detail_btn.className = "btn btn-outline-primary btn-sm";
            card_detail_btn.innerText = "상세정보";
            card_detail_btn.addEventListener('click', event => {
                window.location.href = "/jbs/store_info.jsp?seq_store=" + seq + "&buid=" + buid;
            });
            card_title.appendChild(card_detail_btn);            

            let card_defdiv = _doc.createElement("div");
            card_body.appendChild(card_defdiv);

            let card_defradio = _doc.createElement("input");
            card_defradio.className = "form-check-input";
            card_defradio.type = "radio";
            card_defradio.name = "flexRadioDefault";
            card_defradio.id = "card_" + seq;
            card_defradio.value = seq;
            if (defcheck) {
                _doc.seqDefStore = seq;
                card_defradio.checked = defcheck;
            }
            card_defradio.addEventListener('click', getActive);
            card_defdiv.appendChild(card_defradio);

            let card_deflabel = _doc.createElement("label");
            card_deflabel.className = "form-check-label";
            card_deflabel.setAttribute("for", "card_" + seq);
            card_deflabel.innerText = " 기본 저장소로 지정";
            card_defdiv.appendChild(card_deflabel);

            if (details) {
                ;
            } else {
                let card_text = _doc.createElement("p");
                card_text.className = "card-text";
                card_text.innerText = details;
                card_body.appendChild(card_text);
            }
        }

        function getActive() {
            const _doc = window.document;

            var checkedRadio = document.querySelector('input[name="flexRadioDefault"]:checked');
            var value = checkedRadio ? checkedRadio.value : null;
            if (value && value != _doc.seqDefStore) {

                if (confirm("기본 저장소로 지정하시겠습니까?")) {
                    var path = "/jbs/user";
                    var params = "cmd=setDefStore";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_store=" + value;

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                //alert(resMsg);
                                let resJo = JSON.parse(resMsg);
                                console.debug(resJo.msg);
                                if (resJo.code == '000') {
                                    window.document.seqDefStore = value;
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                } else {
                    // chkItem.removeAttribute("checked");
                }
            }
        }
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>