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

    String quickBtnList = "btn_list_top,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="title" value="지니박스 사용자 프로필" />
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
                    <!-- 기존 프로필 박스 -->
                    <h2>프로필</h2>
                    <!-- <form class="validation-form" novalidate> -->
                        <div class="mb-3">
                            <div class="row mb-2">
                                <div class="col">
                                    <label for="juid">아이디</label>
                                    <input type="text" class="form-control" id="juid" placeholder="*필수" value="" disabled readonly>
                                </div>
                                <div class="col">
                                    <label for="juname">사용자명</label>
                                    <input type="text" class="form-control" id="juname" placeholder="*옵션" value="">
                                </div>
                            </div>
                            <div class="row">
                                <div class="col">
                                    <label for="jupw">비밀번호</label>
                                    <input type="text" class="form-control" id="jupw" placeholder="" value="" required>
                                </div>
                                <div class="col">
                                    <label for="jupw_confirm">확인</label>
                                    <input type="text" class="form-control" id="jupw_confirm" placeholder="" value="" required>
                                </div>
                            </div>
                            <div class="mb-2"></div>
                            <button id="btn_p_modify" class="btn btn-primary btn-sm btn-block" type="button">적용</button>
                        </div>

                        <!-- <hr class="mb-4">
                        <div class="mb-3">
                            <label for="inviteCode">공유 목록</label>
                            <ul id="sharedlist" class="list-group list-group-flush"></ul>
                        </div>                        
                        <div class="row mb-2">
                            <div class="col">
                                <input type="text" class="form-control" id="inviteCode" placeholder="초대코드 입력">
                            </div>
                            <div class="col">
                                <button id="btn_shareadd" class="btn btn-primary btn-sm btn-block" type="button">공유 추가</button>
                            </div>
                        </div> -->
                    <!-- </form> -->
                </div>

            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script type="text/javascript">
        window.addEventListener('load', () => {

            var path = "/jbs/user";
            var params = "cmd=info";
            if (urlparam("buid")) params += "&buid=" + urlparam("buid");
            params += "&authcode=" + urlparam("authcode");

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var _doc = window.document;
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let userJo = JSON.parse(resMsg);
                        if (userJo.code == '000') {
                            _doc.querySelector("#juid").value = userJo.juid;
                            _doc.querySelector("#juname").value = userJo.juname;

                            // var list_group = _doc.querySelector("#sharedlist");
                            // for (sharedinfo of userJo.sharedlist) {
                            //     //TODO box_info.jsp 에서 addInviteRow() 함수를 참고하여 나머지 구현
                            //     var list_group_item = _doc.createElement("li");
                            //     list_group_item.className = "list-group-item d-flex justify-content-between align-items-center";
                            //     list_group_item.innerHTML = sharedinfo.name + (sharedinfo.name_box ? " - " + sharedinfo.name_box : "") + "<span class='badge bg-primary rounded-pill' >" + sharedinfo.authority + "</span >";
                            //     list_group.appendChild(list_group_item);
                            // }
                        } else {
                            authFailCheck(userJo.code, userJo.msg);
                        }
                    }
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        document.querySelector("#btn_p_modify").addEventListener('click', function (event) {
            
            alert("준비중입니다. 관리자에게 문의해 주세요.")
            return -1;
            
            
            var val_juid = document.querySelector("#juid").value;
            var val_jupw = document.querySelector("#jupw").value;
            var val_jupwc = document.querySelector("#jupw_confirm").value;
            var val_juname = document.querySelector("#juname").value;

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

            var path = "/jbs/user";
            var params = "cmd=modify";
            if (urlparam("buid")) params += "&buid=" + urlparam("buid");
            params += "&authcode=" + urlparam("authcode");
            params += "&juid=" + val_juid;
            params += "&jupw=" + val_jupw;
            params += "&juname=" + val_juname;

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) alert(resMsg);
                }
            };
            sendPost(_ajax, path, params);
        }, false);

        // document.querySelector("#btn_shareadd").addEventListener('click', function (event) {
        //     var val_juid = document.querySelector("#juid").value;
        //     var val_icode = document.querySelector("#inviteCode").value;

        //     if (!val_icode) {
        //         alert("초대 받은 코드를 입력해주세요");
        //         return -1;
        //     }

        //     var path = "/jbs/share";
        //     var params = "cmd=add";
        //     if (urlparam("buid")) params += "&buid=" + urlparam("buid");
        //     params += "&authcode=" + urlparam("authcode");
        //     params += "&juid=" + val_juid;
        //     params += "&icode=" + val_icode;

        //     var _ajax = new XMLHttpRequest();
        //     _ajax.onreadystatechange = function () {
        //         if (checkAjaxSuc(_ajax)) {
        //             var resMsg = _ajax.responseText;
        //             if (resMsg.length > 0) {
        //                 let resJo = JSON.parse(resMsg);
        //                 alert(resJo.msg);
        //                 if (resJo.code == '000')
        //                     window.location.reload(false);
        //             }
        //         }
        //     };
        //     sendPost(_ajax, path, params);
        // }, false);
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>

