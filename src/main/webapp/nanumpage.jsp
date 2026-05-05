<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.omnibuscode.utils.JSONUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String quickBtnList = "btn_list_top,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">
<head>
    <jsp:include page="./include/head.jsp">
    <jsp:param name="page_name" value="nanumpage" />
    <jsp:param name="title" value="지니박스 나눔아이템 목록" />
    <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
    </jsp:include>
</head>

<body>
    <div class="container-fluid">
        <div class="row">
            <main class="col-md-12 ms-sm-auto col-lg-12 px-md-4">

                <hr class="">
                <div class="container">

                    <div class="mb-2"></div>
                    <div id="nanum-title" class="bg-body shadow-sm">
                        <h6 class="card-title">[<span id="nanum_name"><%=request.getAttribute("nanum_name")%></span>] &nbsp;</h6>
                    </div>
                    <div class="mb-2"></div>

                    <!-- 나눔아이템 목록 시작 -->
                    <div id="card-row-nanumitem" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3"></div>
                    <script>
<%
    String seqNanum = (request.getAttribute("seq_nanum") != null) ? request.getAttribute("seq_nanum").toString() : "";
    List<JSONObject> items = (List) request.getAttribute("nanum_items");
    boolean requireAcd = (request.getAttribute("require_acd") != null) ? Boolean.parseBoolean(request.getAttribute("require_acd").toString()) : false;
    String shareCode = (request.getAttribute("share_code") != null) ? request.getAttribute("share_code").toString() : "";
    
    if (requireAcd) {
%>
                        window.addEventListener('load', () => {
                            var userInput = prompt("접속코드를 입력해주세요", "");
                            if (userInput) {
                                const _doc = window.document;

                                var path = "/jbs/nanum";
                                var params = "scd=<%=shareCode%>";
                                params += "&acd=" + userInput;

                                var _ajax = new XMLHttpRequest();
                                _ajax.onreadystatechange = function () {
                                    if (checkAjaxSuc(_ajax)) {
                                        var resMsg = _ajax.responseText;
                                        if (resMsg.length > 0) {
                                            let resJson = JSON.parse(resMsg);
                                            var alertMsg = null;
                                            if (resJson.code == "000") {
                                                let items = resJson.nanum_items;
                                                showNanumItems(<%=seqNanum%>, items);
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
                        }, false);
                        
<%
    } else if (items != null) {
        String jsonStrItems = items.toString();
%>

                        window.addEventListener('load', () => {
                            
                        	const _doc = window.document;
                            let strJson = '<%=jsonStrItems%>';
                            var alertMsg = null;
                            
                            if (strJson) {
                                let items = JSON.parse(strJson);
                                showNanumItems(<%=seqNanum%>, items);
                            } else {
                                alertMsg = "* " + resJson.msg;
                            }

                            if (alertMsg) {
                                alert(alertMsg);
                            }
                            
                        }, false);

<%
    }
%>
                        /**
                         * 나눔아이템 카드 목록 출력
                         */
                        function showNanumItems(seq_nanum, items) {
                        	const row_itemcardlist = window.document.querySelector("#card-row-nanumitem");
                        	let cardrow_list = "";
                            var cnt_item = 0;
                            
                            for (item of items) {
                                cardrow_list += "<div class='col'>";
                                cardrow_list += "    <div class='card shadow-sm'>";
                                cardrow_list += "        <div class='card-body d-flex'>";
                                cardrow_list += "           <button onclick='javascript:nanumZZim(" + seq_nanum + "," + item.seq + ")' class='btn btn-primary rounded-circle position-relative' style='width: 27px; height: 27px; font-size: 12px; padding: 0;'>찜</button>";
                                //cardrow_list += "            <h5 class='card-title'><img height='24' src='./res/web/images/btn_zzim.png' onclick='javascript:nanumZZim(" + seq_nanum + "," + item.seq + ")'/> " + item.name + "</h5>";
                                                                cardrow_list += "&nbsp;<h5 class='card-title'>" + item.name + "</h5>";
                                cardrow_list += "        </div>";

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
                                cardrow_list += "</div>";
                                cnt_item++;
                            }
                            row_itemcardlist.innerHTML = cardrow_list;
                        }
                        
                        /**
                        * 나눔찜
                        */
                        function nanumZZim(seqNanum, seqNitem) {

                            const _doc = window.document;

                            var path = "/jbs/auth";
                            var params = "cmd=validUser";

                            var _ajax = new XMLHttpRequest();
                            _ajax.onreadystatechange = function () {
                                if (checkAjaxSuc(_ajax)) {
                                    var resMsg = _ajax.responseText;
                                    if (resMsg.length > 0) {
                                        let resJson = JSON.parse(resMsg);

                                        let redirPage = null;
                                        if (resJson.code == "000") {
                                            redirPage = '/jbs/list_nanum.jsp';
                                            resJson.msg = null;
                                        } else if (resJson.code == "001") {
                                            redirPage = '/jbs/signin.jsp';
                                        }

                                        if (seqNanum) {
                                            redirPage += "?seq_nanum="+seqNanum;
                                            redirPage += "&scd=<%=shareCode%>";
                                            redirPage += "&seq_nitem="+seqNitem;
                                        }

                                        if (resJson.msg) {
                                            alert(resJson.msg);
                                        }

                                        if (redirPage) {
                                            window.location.href = redirPage;
                                        }
                                    }
                                }
                            };
                            sendPost(_ajax, path, params);

                        }

                    </script>
                    <!-- 나눔아이템 목록 끝 -->
                </div>
            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>