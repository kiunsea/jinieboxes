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

    String quickBtnList = "btn_list_top,btn_list_collapse,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="list_nanum" />
        <jsp:param name="title" value="지니박스 나눔함(Nanum) 목록" />
        <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
    </jsp:include>
</head>

<body>
	<jsp:include page="./include/main_menu.jsp">
        <jsp:param name="is_partner" value="<%= is_partner %>" />
    </jsp:include>
    <div class="container-fluid">
        <main class="col-md-12 ms-sm-auto col-lg-12 px-md-4">
        
            <ul class="nav nav-tabs mb-2">
                <li class="nav-item">
                    <a class="nav-link" href="list_box.jsp">보관함</a>
                </li>
                <%-- <li class="nav-item">
                    <a class="nav-link" href="list_room.jsp">보관실</a>
                </li> --%>
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#">나눔함</a>
                </li>
            </ul>

            <jsp:include page="./include/quick_buttons.jsp">
                <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
            </jsp:include>
            
            <%-- <hr class="mb-3"> --%>
            <!-- 나눔함 정보 출력 시작 -->
            <input type="text" id="seq_selected_nanum" value="" hidden>
            <div class="nav-scroller bg-body shadow-sm">
                <nav id="favorite_list" class="nav" aria-label="Secondary navigation">
                    <!-- 나눔 즐겨찾기 메뉴 시작 -->
                    <%-- <a class="nav-link active" aria-current="page" href="javascript:showFavoriteModal()">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmark-plus-fill" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M2 15.5V2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.74.439L8 13.069l-5.26 2.87A.5.5 0 0 1 2 15.5zm6.5-11a.5.5 0 0 0-1 0V6H6a.5.5 0 0 0 0 1h1.5v1.5a.5.5 0 0 0 1 0V7H10a.5.5 0 0 0 0-1H8.5V4.5z"/>
                            </svg>
                    </a>
                    <a class="nav-link" href="#">
                        Link0
                        <span class="badge text-bg-light rounded-pill align-text-bottom">25</span>
                    </a>
                    <a class="nav-link" href="#">Link1</a>
                    <a class="nav-link" href="#">Link2</a>
                    <a class="nav-link" href="#">Link3</a>
                    <a class="nav-link" href="#">Link4</a> --%>
                    <!-- 나눔 즐겨찾기 메뉴 끝 -->
                </nav>
            </div>

            <div class="mb-2"></div>
            <div id="nanum-title" class="bg-body shadow-sm">
                <div class="input-group d-flex justify-content-between">
                    <button id="btn_additem" type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#ModalAddItem">+아이템</button>
                    <span id="name_nanum" class="border border border-light-subtle">나눔함 이름</span>                                    
                    <button id="btn_addshare" type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#ModalAddShare">+공유</button>
                </div>
            </div>
            <div class="mb-2"></div>

            <!-- 나눔아이템 목록 시작 -->
            <div id="card-row-nanumitem" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
                <!--
                <div class="col">
                    <div class="card shadow-sm">
                    <svg class="bd-placeholder-img card-img-top" width="100%" height="225" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Thumbnail" preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title><rect width="100%" height="100%" fill="#55595c"></rect><text x="50%" y="50%" fill="#eceeef" dy=".3em">Thumbnail</text></svg>
                    <div class="card-body">
                        <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
                        <div class="d-flex justify-content-between align-items-center">
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-outline-secondary">View</button>
                            <button type="button" class="btn btn-sm btn-outline-secondary">Edit</button>
                        </div>
                        <small class="text-muted">9 mins</small>
                        </div>
                    </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card shadow-sm">
                    <svg class="bd-placeholder-img card-img-top" width="100%" height="225" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Thumbnail" preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title><rect width="100%" height="100%" fill="#55595c"></rect><text x="50%" y="50%" fill="#eceeef" dy=".3em">Thumbnail</text></svg>
                    <div class="card-body">
                        <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
                        <div class="d-flex justify-content-between align-items-center">
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-outline-secondary">View</button>
                            <button type="button" class="btn btn-sm btn-outline-secondary">Edit</button>
                        </div>
                        <small class="text-muted">9 mins</small>
                        </div>
                    </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card shadow-sm">
                    <svg class="bd-placeholder-img card-img-top" width="100%" height="225" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Thumbnail" preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title><rect width="100%" height="100%" fill="#55595c"></rect><text x="50%" y="50%" fill="#eceeef" dy=".3em">Thumbnail</text></svg>
                    <div class="card-body">
                        <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
                        <div class="d-flex justify-content-between align-items-center">
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-outline-secondary">View</button>
                            <button type="button" class="btn btn-sm btn-outline-secondary">Edit</button>
                        </div>
                        <small class="text-muted">9 mins</small>
                        </div>
                    </div>
                    </div>
                </div>
                -->
            </div>
            <!-- 나눔아이템 목록 끝 -->
            <script>
                /**
                 * 나눔 즐겨찾기와 목록을 초기화
                 */
                function initializeNanums() {
                    const _doc = window.document;

                    var path = "/jbs/nanum";
                    var params = "cmd=nanums";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_store=" + urlparam("seq_store");

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                let resJson = JSON.parse(resMsg);
                                var alertMsg = null;
                                if (resJson.code == "000") {
                                    _doc.nanums_fav = resJson.nanums_fav; //즐겨찾기
                                    _doc.nanums_own = resJson.nanums_own; //자신의 소유
                                    _doc.nanums_nearby = resJson.nanums_nearby; //친구 또는 회원에게 공유받은

                                    const nav_favlist = _doc.querySelector("#favorite_list");
                                    let nanums_fav = resJson.nanums_fav;
                                    let alink_list = "<a class='nav-link active' aria-current='page' href='javascript:showFavoriteModal()'>";
                                        alink_list += "<svg xmlns='http://www.w3.org/2000/svg' width='25' height='25' fill='currentColor' class='bi bi-bookmark-plus-fill' viewBox='0 0 16 16'>";
                                        alink_list += "<path fill-rule='evenodd' d='M2 15.5V2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.74.439L8 13.069l-5.26 2.87A.5.5 0 0 1 2 15.5zm6.5-11a.5.5 0 0 0-1 0V6H6a.5.5 0 0 0 0 1h1.5v1.5a.5.5 0 0 0 1 0V7H10a.5.5 0 0 0 0-1H8.5V4.5z'/>";
                                        alink_list += "</svg>";
                                        alink_list += "</a>";
                                        alink_list += "<a class='nav-link' href='javascript:showNanumitemList(0)'>열린나눔</a>";

                                    for (nanum of nanums_fav) {
                                        alink_list += "<a class='nav-link' href='javascript:showNanumitemList(" + nanum.seq + ", " + nanum.show_btn_additem + ", " + nanum.show_btn_addshare + ")'>" + nanum.name;
                                        if (nanum.itemcnt > 0) {
                                            alink_list += "<span class='badge text-bg-light rounded-pill align-text-bottom'>" + nanum.itemcnt + "</span>";
                                        }
                                        alink_list += "</a>";
                                    }
                                    nav_favlist.innerHTML = alink_list;

                                    if (!_doc.currSeqNanum) {
                                        showNanumitemList(0); //초기화면을 열린나눔으로 출력
                                    }
                                } else {
                                    alertMsg = "* " + resJson.msg;
                                }

                                if (alertMsg) {
                                    alert(alertMsg);
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);

                    //collapse 버튼을 nanum item collapse 로 변경
                    var btn_coll = window.document.querySelector("#btn_list_collapse");
                    btn_coll.setAttribute("data-bs-target", "#collapseNanum");
                    btn_coll.setAttribute("aria-controls", "collapseNanum");
                }

                /**
                 * 나눔함 정보와 나눔아이템 카드 목록 출력
                 */
                function showNanumitemList(seq_nanum, additem, addshare) {
                    const _doc = window.document;

                    _doc.querySelector("#seq_selected_nanum").value = seq_nanum;

                    //나눔아이템 추가버튼 출력
                    var btn_additem = _doc.querySelector("#btn_additem");
                    if (additem) {
                        btn_additem.style.display = "block";
                    } else {
                        btn_additem.style.display = "none";
                    }

                    //공유 추가버튼과 연결주소 출력
                    var nanum_name = _doc.querySelector("#name_nanum");
                    var btn_addshare = _doc.querySelector("#btn_addshare");
                    if (addshare) {
                        nanum_name.style.display = "block";
                        btn_addshare.style.display = "block";

                        if (seq_nanum > 0) {
                            for (nanum of _doc.nanums_fav) {
                                if (nanum.seq == seq_nanum) {
                                    let share_url = "";
                                    if (nanum.share_code) {
                                        let page_path = window.location.origin + window.location.pathname;
                                        let lstidx = page_path.lastIndexOf("/");
                                        share_url = page_path.substring(0, lstidx) + "/nanum?scd=" + nanum.share_code;
                                    }
                                    nanum_name.innerText = "[" + nanum.name + "] " + share_url;
                                    break;
                                }
                            }
                        } else {
                            nanum_name.innerText = '열린나눔';
                        }
                    } else {
                        nanum_name.style.display = "none";
                        btn_addshare.style.display = "none";
                    }

                    //나눔함 정보(아이템,초대,공유) 출력
                    var path = "/jbs/nanum";
                    var params = "cmd=getInfo";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_nanum=" + seq_nanum;

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                let resJson = JSON.parse(resMsg);
                                var alertMsg = null;
                                if (resJson.code == "000") {
                                    const itemcardlist = window.document.querySelector("#card-row-nanumitem");
                                    
                                    let items = window.document.curr_nanum_items = resJson.nanum_items;
                                    itemcardlist.innerHTML = createNanumitemRows(resJson.seq_nanum, resJson.is_nanum_owner, items);
                                    _doc.currSeqNanum = resJson.seq_nanum; //현재 출력된 나눔시퀀스 저장

                                    let invites = resJson.nanum_invites;
                                    const invite_list_group = window.document.querySelector("#asmd_invite_list");
                                    invite_list_group.innerHTML = createNanumInviteRows(invites);

                                    let shares = resJson.nanum_shares;
                                    const share_list_group = window.document.querySelector("#asmd_share_list");
                                    share_list_group.innerHTML = createNanumShareRows(shares);
                                } else {
                                    alertMsg = "* " + resJson.msg;
                                }

                                if (alertMsg) {
                                    alert(alertMsg);
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                }

                /**
                 * 나눔아이템 카드 목록을 생성 
                 */
                function createNanumitemRows(seq_nanum, is_nanum_owner, items) {
                    var _doc = window.document;

                    var cardrow_list = "";
                    var cnt_item = 0;
                    if (items) {
                        let seqArr = Object.keys(items);
                        if (seqArr.length > 0) {
                            for (let i = 1; i <= seqArr.length; i++) {
                                let item = items[seqArr[i - 1]];
                                cardrow_list += "<div id='nanumitem_" + seq_nanum + "_" + item.seq_i + "' class='col'>";
                                cardrow_list += "  <div class='card shadow-sm'>";

                                cardrow_list += "    <div class='card-body'>";
                                cardrow_list += "      <div class='input-group d-flex justify-content-between'>";

                                if (item.is_owner) { 
                                    cardrow_list += "        <button class='btn btn-secondary rounded-circle position-relative' style='width: 27px; height: 27px; font-size: 12px; padding: 0;'>";
                                } else {
                                    cardrow_list += "        <button onclick='javascript:nanumZZim(" + item.seq + ")' class='btn btn-primary rounded-circle position-relative' style='width: 27px; height: 27px; font-size: 12px; padding: 0;'>";
                                }
                                cardrow_list += "            찜";
                                if (item.zzimcnt && item.zzimcnt > 0) {
                                    cardrow_list += "            <span class='position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger' style='font-size: 10px;'>";
                                    cardrow_list += item.zzimcnt;
                                    cardrow_list += "                <span class='visually-hidden'>zzim count</span>";
                                    cardrow_list += "            </span>";
                                }
                                cardrow_list += "        </button>";
                                
                                cardrow_list += "        <span data-bs-toggle='modal' data-bs-target='#ModalNanumItemInfo' style='cursor:pointer;'"
                                                            + " onclick='javascript:nanumItemInfo(" + item.seq + "," + seq_nanum + ")'>"
                                                            + "<h5 class='card-title'>" + item.name+ "</h5>"
                                                        + "</span>";
                                cardrow_list += "        <div class='btn-group btn-group-sm'>";
                                cardrow_list += "          <button id='' type='button' style='display:" 
                                                                + ((is_nanum_owner || item.is_owner) ? "block" : "none") 
                                                            + "; class='btn btn-sm btn-outline-primary btn_releaseitem' onclick='javascript:releaseNanumitem(" 
                                                            + seq_nanum + "," + item.seq_i + ")'>등록해제</button>";
                                cardrow_list += "        </div>";
                                cardrow_list += "      </div>";
                                cardrow_list += "    </div>";
                                cardrow_list += "    <div class='collapse show' id='collapseNanum'>";
                                
                                let imgs = item.images.imgs;
                                if (imgs) {
                                    cardrow_list += "  <div id='item_imgs_" + cnt_item + "' class='carousel slide'>";
                                    cardrow_list += "    <div class='carousel-inner'>";
                                    var cnt_imgitem = 0;
                                    for (img_item of imgs) {
                                        cardrow_list += "      <div class='carousel-item";
                                        if (cnt_imgitem == 0) {
                                            cardrow_list += "   active";
                                        }
                                        cardrow_list += "'>";
                                        cardrow_list += "        <img src='" + img_item.src + "' class='d-block w-100' alt='...'>";
                                        cardrow_list += "      </div>";
                                        cnt_imgitem++;
                                    }
                                    cardrow_list += "    </div>";
                                    if (imgs.length > 1) {
                                        cardrow_list += "    <button class='carousel-control-prev' type='button' data-bs-target='#item_imgs_" + cnt_item + "' data-bs-slide='prev'>";
                                        cardrow_list += "      <span class='carousel-control-prev-icon' aria-hidden='true'></span>";
                                        cardrow_list += "      <span class='visually-hidden'>Previous</span>";
                                        cardrow_list += "    </button>";
                                        cardrow_list += "    <button class='carousel-control-next' type='button' data-bs-target='#item_imgs_" + cnt_item + "' data-bs-slide='next'>";
                                        cardrow_list += "      <span class='carousel-control-next-icon' aria-hidden='true'></span>";
                                        cardrow_list += "      <span class='visually-hidden'>Next</span>";
                                        cardrow_list += "    </button>";
                                    }
                                    cardrow_list += "  </div>";
                                }
                                cardrow_list += "    </div>";

                                cardrow_list += "  </div>";
                                cardrow_list += "</div>";
                                cnt_item++;
                            }
                        }
                    }
                    return cardrow_list;
                }

                /**
                 * 해당 아이템을 나눔아이템 목록에서 등록 해제한다.
                 */
                function releaseNanumitem(seq_nanum, seq_item) {
                    //alert("nanum:"+seq_nanum+", item:"+seq_item);
                    if (confirm("등록 해제할까요?")) {
                        const _doc = window.document;

                        var path = "/jbs/nanum";
                        var params = "cmd=releaseItem";
                        params += "&buid=" + urlparam("buid");
                        params += "&authcode=" + urlparam("authcode");
                        params += "&seq_store=" + urlparam("seq_store");
                        params += "&seq_nanum=" + seq_nanum;
                        params += "&seq_item=" + seq_item;

                        var _ajax = new XMLHttpRequest();
                        _ajax.onreadystatechange = function () {
                            if (checkAjaxSuc(_ajax)) {
                                var resMsg = _ajax.responseText;
                                if (resMsg.length > 0) {
                                    let resJson = JSON.parse(resMsg);
                                    var alertMsg = null;
                                    if (resJson.code == "000") {
                                        var itemElem = _doc.querySelector("#nanumitem_"+resJson.seq_nanum+"_"+resJson.seq_item);
                                        itemElem.style.display = "none";
                                        alertMsg = "등록 해제되였습니다.";
                                    } else {
                                        alertMsg = "* " + resJson.msg;
                                    }

                                    if (alertMsg) {
                                        alert(alertMsg);
                                    }
                                }
                            }
                        };
                        sendPost(_ajax, path, params);
                    }
                }

                /**
                 * 나눔함 초대 ROW를 생성
                 */
                function createNanumInviteRows(invites) {
                    const _doc = window.document;

                    let seqArr = Object.keys(invites);
                    let cardrow_list = "";
                    for (let i = 1; i <= seqArr.length; i++) {
                        let invite = invites[seqArr[i - 1]];
                        
                        cardrow_list += "<li id='inv_"+invite.seq+"' class='list-group-item d-flex justify-content-between align-items-center'>";
                        cardrow_list += invite.juid + " - CD : " + invite.invite_code;
                        cardrow_list += "<span id='inv_authority_"+invite.seq+"' class='badge bg-primary rounded-pill'>";
                        cardrow_list += invite.authority;
                        cardrow_list += "</span>";
                        cardrow_list += "</li>";
                    }
                    return cardrow_list;
                }
                /**
                 * 나눔함 공유 ROW를 생성
                 */
                function createNanumShareRows(shares) {
                    const _doc = window.document;

                    let seqArr = Object.keys(shares);
                    let cardrow_list = "";
                    for (let i = 1; i <= seqArr.length; i++) {
                        let share = shares[seqArr[i - 1]];
                        
                        cardrow_list += "<li class='list-group-item d-flex justify-content-between align-items-center'>";
                        cardrow_list += share.juid;
                        cardrow_list += "<span class='badge bg-primary rounded-pill'>";
                        cardrow_list += share.authority;
                        cardrow_list += "</span>";
                        cardrow_list += "</li>";
                    }
                    return cardrow_list;
                }

                /**
                 * 나눔찜
                 */
                function nanumZZim(seqNitem) {
                    //TODO
                    if (confirm(seqNitem+" - 나눔을 요청할까요?")) {

                        let totQty = window.document.curr_nanum_items[seqNitem].qty;

                        let zzim_qty = prompt("나눔 받을 갯수를 입력해주세요:", "1");
                        if (!zzim_qty) {
                            zzim_qty = 1;
                        } else if (zzim_qty > totQty) {
                            alert(totQty + " 보다 같거나 작아야 합니다");
                            return -1;
                        }

                        const _doc = window.document;

                        var path = "/jbs/nanum";
                        var params = "cmd=itemZZim";
                        params += "&seq_nitem=" + seqNitem;
                        params += "&zzim_qty=" + zzim_qty;

                        var _ajax = new XMLHttpRequest();
                        _ajax.onreadystatechange = function () {
                            if (checkAjaxSuc(_ajax)) {
                                var resMsg = _ajax.responseText;
                                if (resMsg.length > 0) {
                                    let resJson = JSON.parse(resMsg);
                                    var alertMsg = resJson.msg;
                                    if (resJson.code == "000") {

                                    }

                                    if (alertMsg) {
                                        alert(alertMsg);
                                    }
                                }
                            }
                        };
                        sendPost(_ajax, path, params);
                    }
                }
            </script>
            <!-- 나눔함 정보 출력 끝 -->
            <!-- 나눔 즐겨찾기 팝업 시작 -->
            <input type="text" id="hidden_focus" value="" hidden>
            <div id="ModalShowFavorite" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="favoriteModal" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <div>
                                <h5 class="modal-title" id="favoriteModal">즐겨찾기</h5>
                            </div>
                            <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <!-- Example Code -->
                            <!-- <div class="accordion" id="accordionPanelsStayOpenExample">
                                <div class="accordion-item">
                                    <h2 class="accordion-header">
                                        <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true" aria-controls="panelsStayOpen-collapseOne">
                                        나의 나눔
                                        </button>
                                    </h2>
                                    <div id="panelsStayOpen-collapseOne" class="accordion-collapse collapse show">
                                        <div class="accordion-body">
                                            <ul class="list-group">
                                                <li class="list-group-item">An item 
                                                    <button type="button" class="btn btn-outline-success">
                                                        <svg display="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks-fill" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z" />
                                                            <path d="M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z" />
                                                        </svg>
                                                        <svg display="block" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z" />
                                                            <path d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z" />
                                                        </svg>
                                                    </button>
                                                </li>
                                                <li class="list-group-item">A second item 
                                                    <button type="button" class="btn btn-outline-success">
                                                        <svg display="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks-fill" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z" />
                                                            <path d="M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z" />
                                                        </svg>
                                                        <svg display="block" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z" />
                                                            <path d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z" />
                                                        </svg>
                                                    </button>
                                                </li>
                                                <li class="list-group-item">A third item 
                                                    <button type="button" class="btn btn-outline-success">
                                                        <svg display="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks-fill" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z" />
                                                            <path d="M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z" />
                                                        </svg>
                                                        <svg display="block" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z" />
                                                            <path d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z" />
                                                        </svg>
                                                    </button></li>
                                                <li class="list-group-item">A fourth item 
                                                    <button type="button" class="btn btn-outline-success">
                                                        <svg display="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks-fill" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z" />
                                                            <path d="M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z" />
                                                        </svg>
                                                        <svg display="block" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z" />
                                                            <path d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z" />
                                                        </svg>
                                                    </button>
                                                </li>
                                                <li class="list-group-item">And a fifth one 
                                                    <button type="button" class="btn btn-outline-success">
                                                        <svg display="block" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks-fill" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z" />
                                                            <path d="M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z" />
                                                        </svg>
                                                        <svg display="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmarks" viewBox="0 0 16 16">
                                                            <path d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z" />
                                                            <path d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z" />
                                                        </svg>
                                                    </button>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="accordion-item">
                                    <h2 class="accordion-header">
                                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="true" aria-controls="panelsStayOpen-collapseTwo">
                                        Accordion Item #2
                                        </button>
                                    </h2>
                                    <div id="panelsStayOpen-collapseTwo" class="accordion-collapse collapse">
                                        <div class="accordion-body">
                                            <strong>This is the second item's accordion body.</strong> It is hidden by default, until the collapse plugin adds the appropriate classes that we use to style each element. These classes control the overall appearance, as well as the showing and hiding via CSS transitions. You can modify any of this with custom CSS or overriding our default variables. It's also worth noting that just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit overflow.
                                        </div>
                                    </div>
                                </div>
                                <div class="accordion-item">
                                    <h2 class="accordion-header">
                                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseThree" aria-expanded="false" aria-controls="panelsStayOpen-collapseThree">
                                        Accordion Item #3
                                        </button>
                                    </h2>
                                    <div id="panelsStayOpen-collapseThree" class="accordion-collapse collapse">
                                        <div class="accordion-body">
                                            <strong>This is the third item's accordion body.</strong> It is hidden by default, until the collapse plugin adds the appropriate classes that we use to style each element. These classes control the overall appearance, as well as the showing and hiding via CSS transitions. You can modify any of this with custom CSS or overriding our default variables. It's also worth noting that just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit overflow.
                                        </div>
                                    </div>
                                </div>
                            </div> -->
                            <!-- End Example Code -->
                            <div class="accordion" id="accordionNanum">
                                <div class="accordion-item">
                                    <h2 class="accordion-header">
                                        <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true" aria-controls="panelsStayOpen-collapseOne">
                                            나의 나눔박스
                                        </button>
                                    </h2>
                                    <div id="panelsStayOpen-collapseOne" class="accordion-collapse collapse show">
                                        <div class="accordion-body">
                                            현재 저장소의 나눔박스 목록입니다
                                            <ul id="ul_my_nanum" class="list-group"></ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="accordion-item">
                                    <h2 class="accordion-header">
                                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="true" aria-controls="panelsStayOpen-collapseTwo">
                                            이웃 나눔박스
                                        </button>
                                    </h2>
                                    <div id="panelsStayOpen-collapseTwo" class="accordion-collapse collapse">
                                        <div class="accordion-body">
                                            다른 회원으로부터 공유 받은 나눔박스 목록입니다
                                            <ul id="ul_nearby_nanum" class="list-group"></ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="sfmd_by"
                                data-bs-dismiss="modal">닫기</button>
                        </div>
                    </div>
                </div>
            </div>
            <script>
                /**
                * 나눔 즐겨찾기 팝업
                */
                function showFavoriteModal() {
                    const _doc = window.document;

                    var path = "/jbs/nanum";
                    var params = "cmd=nanums";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_store=" + urlparam("seq_store");

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                let resJson = JSON.parse(resMsg);
                                var alertMsg = null;
                                if (resJson.code == "000") {
                                    _doc.nanums_own = resJson.nanums_own;
                                    _doc.nanums_nearby = resJson.nanums_nearby;

                                    //팝업창 초기화
                                    _doc.querySelector("#ul_my_nanum").innerHTML = getNanumElemsStr(resJson.nanums_own);
                                    _doc.querySelector("#ul_nearby_nanum").innerHTML = getNanumElemsStr(resJson.nanums_nearby);
                                    bootstrap.Modal.getOrCreateInstance(document.getElementById('ModalShowFavorite')).show();                                        
                                } else {
                                    alertMsg = "* " + resJson.msg;
                                }

                                if (alertMsg) {
                                    alert(alertMsg);
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                }

                function getNanumElemsStr(nanums) {
                    var li_list = "";
                    for (nanum of nanums) {
                        let fav_val = nanum.favorite;
                        let bmark_fill_display = "none";
                        let bmark_empty_display = "none";
                        if (fav_val == '1') {
                            bmark_fill_display = "block";
                        } else {
                            bmark_empty_display = "block";
                        }
                        li_list += "<li class='list-group-item'>";
                        li_list += nanum.name;
                        li_list += "    <button id='bmark_"+nanum.seq+"' type='button' class='btn btn-outline-success' onclick='javascript:toggleFavorite(" + nanum.seq + ")'>";
                        li_list += "        <svg id='bmark_fill_"+nanum.seq+"' display='"+bmark_fill_display+"' xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-bookmarks-fill' viewBox='0 0 16 16'>";
                        li_list += "            <path d='M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4z' />";
                        li_list += "            <path d='M4.268 1A2 2 0 0 1 6 0h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L13 13.768V2a1 1 0 0 0-1-1H4.268z' />";
                        li_list += "        </svg>";
                        li_list += "        <svg id='bmark_empty_"+nanum.seq+"' display='"+bmark_empty_display+"' xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-bookmarks' viewBox='0 0 16 16'>";
                        li_list += "            <path d='M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5V4zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1H4z' />";
                        li_list += "            <path d='M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1z' />";
                        li_list += "        </svg>";
                        li_list += "    </button>";
                        li_list += "</li>";
                    }
                    return li_list;
                }

                function toggleFavorite(seq_nanum) {
                    const _doc = window.document;

                    var path = "/jbs/nanum";
                    var params = "cmd=toggle";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_nanum=" + seq_nanum;

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                let resJson = JSON.parse(resMsg);
                                var alertMsg = null;
                                if (resJson.code == "000") {
                                    let nanum = resJson.nanum;
                                    let bmark_fill = _doc.querySelector("#bmark_fill_"+nanum.seq);
                                    let bmark_empty = _doc.querySelector("#bmark_empty_"+nanum.seq);
                                    if (nanum.favorite == '1') {
                                        bmark_fill.style.display = "block";
                                        bmark_empty.style.display = "none";
                                    } else {
                                        bmark_fill.style.display = "none";
                                        bmark_empty.style.display = "block";
                                    }
                                } else {
                                    alertMsg = "* " + resJson.msg;
                                }

                                if (alertMsg) {
                                    alert(alertMsg);
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                }
            </script>
            <!-- 나눔 즐겨찾기 팝업 끝 -->

            <!-- 나눔 공유 팝업 시작 -->
            <div id="ModalAddShare" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addShareModal" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <div>
                                <h5 class="modal-title" id="addShareModal">공유 추가</h5>
                            </div>
                            <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <label>나눔함을 함께 사용할 사용자의 아이디를 검색해 주세요.</label>
                            <div class="input-group mb-2">
                                <span class="input-group-text">아이디(이메일)</span>
                                <input id="asmd_juid" type="text" class="form-control" placeholder="">
                                <script>
                                    const validEmail = function () {// email 유효성을 검사하는 함수 validation
                                        const _doc = window.document;
                                        const juid = document.querySelector("#asmd_juid");
                                        const pattern = /^[^·]+@[^·]+\.[a-z]{2,3}$/;   // email 값의 조건

                                        if (juid.value.match(pattern)) {
                                            // console.debug("validEmail check exist email~~");
                                            // 등록된 email 인지 확인
                                            var path = "/jbs/user";
                                            var params = "cmd=existEmail";
                                            params += "&user_email=" + document.querySelector("#asmd_juid").value;

                                            var _ajax = new XMLHttpRequest();
                                            _ajax.onreadystatechange = function () {
                                                if (checkAjaxSuc(_ajax)) {
                                                    var resMsg = _ajax.responseText;
                                                    if (resMsg.length > 0) {
                                                        //alert(resMsg);
                                                        let resJo = JSON.parse(resMsg);
                                                        //console.debug("resJo.result-"+resJo.result)
                                                        let exist_email = resJo.result;
                                                        let rstMsg = "";
                                                        if (exist_email) {
                                                            //OK~
                                                            rstMsg = "* 사용자가 확인되었습니다.";
                                                        } else {
                                                            rstMsg = "* 사용자를 확인할 수 없습니다.";
                                                            juid.className = "form-control";
                                                        }
                                                        _doc.querySelector("#chk_email").innerHTML = rstMsg;
                                                        _doc.valid_email = exist_email;
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
                                    document.querySelector("#asmd_juid").addEventListener("keyup", validEmail);
                                </script>
                            </div>
                            <div class="col">
                                <span id="chk_email" style="font-size: 12px;"></span>
                            </div>
                            <div class="input-group mb-2">
                                <input class="form-check-input" type="checkbox" id="asmd_add_item">
                                &nbsp;<label for="asmd_add_item">아이템 등록 가능</label>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button id="asmd_btn_add" type="button" class="btn btn-primary">추가</button>
                            <button id="asmd_btn_cancel" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>                                
                        </div>
                        <hr class="">
                        <ul id="asmd_invite_list" class="list-group list-group-flush">
                            <%-- <li class="list-group-item d-flex justify-content-between align-items-center">An item<span class="badge bg-primary rounded-pill">14</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span class="badge bg-primary rounded-pill">2</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span class="badge bg-primary rounded-pill">1</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">An item<span class="badge bg-primary rounded-pill">14</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span class="badge bg-primary rounded-pill">2</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span class="badge bg-primary rounded-pill">1</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">An item<span class="badge bg-primary rounded-pill">14</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A second item<span class="badge bg-primary rounded-pill">2</span></li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">A third item<span class="badge bg-primary rounded-pill">1</span></li> --%>
                        </ul>
                        <hr class="">
                        <ul id="asmd_share_list" class="list-group list-group-flush">
                        </ul>
                    </div>
                </div>
            </div>
            <script>
                window.document.querySelector("#asmd_btn_add").addEventListener('click', () => {
                    var _doc = window.document;
                    if (!_doc.valid_email) {
                        alert("사용자를 확인할 수 없습니다.");
                        return -1;
                    } else {
                        //alert("OK~~");

                        var path = "/jbs/invite";
                        var params = "cmd=save";
                        params += "&juid=" + document.querySelector("#asmd_juid").value;
                        if (document.querySelector("#asmd_add_item").checked) {
                            params += "&authority=M";
                        } else {
                            params += "&authority=R";
                        }
                        params += "&seq_nanum="+document.querySelector("#seq_selected_nanum").value;

                        var _ajax = new XMLHttpRequest();
                        _ajax.onreadystatechange = function () {
                            if (checkAjaxSuc(_ajax)) {
                                var resMsg = _ajax.responseText;
                                if (resMsg.length > 0) {
                                    //alert(resMsg);
                                    let resJo = JSON.parse(resMsg);
                                    console.debug("resJo.result-"+resJo.result);
                                    document.querySelector("#asmd_juid").value = "";
                                    document.querySelector("#asmd_add_item").checked = false;
                                    bootstrap.Modal.getOrCreateInstance(document.querySelector('#ModalAddShare')).hide();
                                }
                            }
                        };
                        sendPost(_ajax, path, params);
                    }
                }, false);
            </script>
            <!-- 나눔 공유 팝업 끝 -->

            <!-- 나눔 아이템 상세 정보 팝업 시작 -->
            <div id="ModalNanumItemInfo" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="nanumItemInfoModal" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <div>
                                <h5 class="modal-title" id="nanumItemInfoModal">나눔아이템 상세</h5>
                            </div>
                            <button type="button" class="btn-close" aria-label="Close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="input-group mb-2">
                                <span class="input-group-text">이름</span>
                                <input id="mdnii_input_itemname" type="text" class="form-control" placeholder="아이템명">
                            </div>
                            <div class="input-group mb-2">
                                <span class="input-group-text">수량</span>
                                <input id="mdnii_input_qty" type="text" class="form-control" placeholder="1">
                            </div>
                            <div class="input-group mb-2">
                                <span class="input-group-text">설명</span>
                                <textarea id="mdnii_input_detail" class="form-control" aria-label="With textarea"></textarea>
                            </div>

                            <div id="zzims">
                                <hr class="mb-3">
                                <h6>찜목록</h6>
                                <div id="zzim-list"></div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button id="mdnii_btn_close" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>                                
                        </div>
                    </div>
                </div>
            </div>
            <script>
                /**
                * 나눔아이템 정보 팝업
                */
                function nanumItemInfo(seqNitem, seqNanum) {

                    const _doc = window.document;

                    // BOX 참조값 초기화
                    var nItem = _doc.curr_nanum_items[seqNitem];

                    _doc.querySelector("#mdnii_input_itemname").value = nItem.name;
                    _doc.querySelector("#mdnii_input_qty").value = nItem.qty;
                    _doc.querySelector("#mdnii_input_detail").value = nItem.detail ? nItem.detail : "";

                    showElement(_doc.querySelector("#zzims"));
                    _doc.querySelector("#zzim-list").innerHTML = "";
                    var path = "/jbs/nanum";
                    var params = "cmd=getZZims";
                    params += "&buid=" + urlparam("buid");
                    params += "&authcode=" + urlparam("authcode");
                    params += "&seq_nanum=" + seqNanum;
                    params += "&seq_nitem=" + seqNitem;
                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        
                        if (checkAjaxSuc(_ajax)) {
                            let resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                let resJson = JSON.parse(resMsg);
                                if (resJson.code == "000") {
                                    let keyArr = Object.keys(resJson.zzims);
                                    if (keyArr.length > 0) {                                        
                                        let grid_outer = _doc.createElement("div");
                                        grid_outer.className = "container text-center";

                                        let row_head =  "<div class='row'>";
                                        row_head +=     "  <div class='col border'>";
                                        row_head +=     "    선택";
                                        row_head +=     "  </div>";
                                        row_head +=     "  <div class='col border'>";
                                        row_head +=     "    사용자";
                                        row_head +=     "  </div>";
                                        row_head +=     "  <div class='col border'>";
                                        row_head +=     "    갯수";
                                        row_head +=     "  </div>";
                                        row_head +=     "  <div class='col border'>";
                                        row_head +=     "    신청일자";
                                        row_head +=     "  </div>";
                                        row_head +=     "</div>";
                                        grid_outer.innerHTML = row_head;

                                        let keyArr = Object.keys(resJson.zzims);
                                        for (i = 1; i <= keyArr.length; i++) {
                                            addZZimRow(grid_outer, resJson.zzims[keyArr[i - 1]]);
                                        }
                                        _doc.querySelector("#zzim-list").appendChild(grid_outer);
                                    }
                                } else if (resJson.code == "001") {
                                    hideElement(_doc.querySelector("#zzims"));
                                }
                                //alert(resJson.msg);
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                }

                function addZZimRow(grid_outer, zzim) {
                    const _doc = window.document;
            
                    let row_zzim = _doc.createElement("div");
                    row_zzim.className = "row";

                    let col_sel = _doc.createElement("div");
                    col_sel.className = "col border";
                    row_zzim.appendChild(col_sel);

                    let zzim_chk = _doc.createElement("input");
                    zzim_chk.type = "radio";
                    zzim_chk.className = "form-check-input";
                    zzim_chk.name = "zzims[]";
                    zzim_chk.value = zzim.seq;
                    col_sel.appendChild(zzim_chk);

                    let col_usr = _doc.createElement("div");
                    col_usr.className = "col border";
                    col_usr.innerText = zzim.juname;
                    row_zzim.appendChild(col_usr);

                    let col_qty = _doc.createElement("div");
                    col_qty.className = "col border";
                    col_qty.innerText = zzim.zzim_qty;
                    row_zzim.appendChild(col_qty);

                    let col_reqd = _doc.createElement("div");
                    col_reqd.className = "col border";
                    col_reqd.innerText = zzim.insert_time;
                    row_zzim.appendChild(col_reqd);

                    grid_outer.appendChild(row_zzim);
                }
            </script>
            <!-- 나눔 아이템 정보 팝업 끝 -->
        </main>
    </div>

    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script type="text/javascript">
        window.addEventListener('load', () => {
            const _doc = window.document;

<%
    Object requireAcd = request.getAttribute("require_acd"); //사용자의 접속코드 여부
    if (requireAcd != null && "true".equals(requireAcd)) {
        String shareCode = request.getAttribute("share_code").toString();
%>
            var userInput = prompt("접속코드를 입력해주세요", "");
            if (userInput) {
                var path = "/jbs/nanum";
                var params = "cmd=share";
                params += "&scd=<%=shareCode%>";
                params += "&acd=" + userInput;

                var _ajax = new XMLHttpRequest();
                _ajax.onreadystatechange = function () {
                    if (checkAjaxSuc(_ajax)) {
                        var resMsg = _ajax.responseText;
                        if (resMsg.length > 0) {
                            let resJson = JSON.parse(resMsg);
                            var alertMsg = null;
                            if (resJson.code == "000") {
                                window.location.reload(false);
                            } else {
                                alertMsg = "* " + resJson.msg;
                            }

                            if (alertMsg) {
                                alert(alertMsg);
                            }
                        }
                    }
                };
                sendPost(_ajax, path, params);
            } else {
                alert("조회 권한이 없습니다");
            }
<%
    } else {
%>
            initializeNanums();
<%
            String seqNanum = "0";
            Object seqNanumObj = request.getAttribute("seq_nanum"); //Servlet에서 전달
            if (seqNanumObj != null) {
                seqNanum = seqNanumObj.toString();
            } else {
                seqNanumObj = request.getParameter("seq_nanum"); //JSP에서 전달
                if (seqNanumObj != null) {
                    seqNanum = seqNanumObj.toString();
                }
            }
%>
            _doc.currSeqNanum = <%=seqNanum%>;
            showNanumitemList(<%=seqNanum%>);
<%
    }
%>
        }, false);
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>