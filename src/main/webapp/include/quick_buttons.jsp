<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

<%
    String quickBtnList = request.getParameter("quick_btn_list");
%>
<div id="quick_buttons" class="dropdown position-fixed bottom-0 end-0 mb-3 me-3 bd-mode-toggle">
<%-- <div id="quick_buttons" class="btn-group position-fixed bottom-0 end-0 mb-3 me-3" role="group" style="position: relative;z-index: 1050;"> --%>

    <% if (quickBtnList.indexOf("btn_list_top") > -1) { %>
    <!-- 리스트 상단 -->
    <button id="btn_list_top" title="TOP" type="button" class="btn btn-outline-primary d-flex align-items-center">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-up-circle" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-7.5 3.5a.5.5 0 0 1-1 0V5.707L5.354 7.854a.5.5 0 1 1-.708-.708l3-3a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 5.707V11.5z"/>
        </svg>
    </button>
    <script type="text/javascript">
        window.document.querySelector("#btn_list_top").addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth' // 부드러운 스크롤 효과
            });
        }, false);
    </script>
    <% } %>

    <%-- <% if (quickBtnList.indexOf("btn_item_add") > -1) { %>
    <!-- 아이템 추가 버튼 -->
    <button id="btn_item_add" title="아이템 추가" type="button" class="btn btn-outline-primary d-flex align-items-center" data-bs-toggle="modal" data-bs-target="#ModalAddItem">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bag-plus" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M8 7.5a.5.5 0 0 1 .5.5v1.5H10a.5.5 0 0 1 0 1H8.5V12a.5.5 0 0 1-1 0v-1.5H6a.5.5 0 0 1 0-1h1.5V8a.5.5 0 0 1 .5-.5z"/>
            <path d="M8 1a2.5 2.5 0 0 1 2.5 2.5V4h-5v-.5A2.5 2.5 0 0 1 8 1zm3.5 3v-.5a3.5 3.5 0 1 0-7 0V4H1v10a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V4h-3.5zM2 5h12v9a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V5z"/>
        </svg>
    </button>
    <script>
        window.document.querySelector("#btn_item_add").addEventListener('click', () => {
            initItemAddPopup();
        }, false);
        document.addEventListener('DOMContentLoaded', function() {
            
        });
    </script>
    <% } %> --%>

    <% if (quickBtnList.indexOf("btn_list_collapse") > -1) { %>
    <!-- 아이템 목록 펼치기/접기 버튼 -->
    <button id="btn_list_collapse" title="목록 펼치기/접기" type="button" class="btn btn-outline-primary d-flex align-items-center" data-bs-toggle="collapse" data-bs-target=".multi-collapse" aria-expanded="false" aria-controls="">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bar-chart-steps" viewBox="0 0 16 16">
            <path d="M.5 0a.5.5 0 0 1 .5.5v15a.5.5 0 0 1-1 0V.5A.5.5 0 0 1 .5 0zM2 1.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-4a.5.5 0 0 1-.5-.5v-1zm2 4a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-7a.5.5 0 0 1-.5-.5v-1zm2 4a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-6a.5.5 0 0 1-.5-.5v-1zm2 4a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-7a.5.5 0 0 1-.5-.5v-1z"/>
        </svg>
    </button>
    <% } %>

    <% if (quickBtnList.indexOf("btn_onboarding") > -1) { %>
    <!-- 온보딩 헬프 버튼 -->
    <button id="btn_onboarding" title="온보딩 시작" type="button" class="btn btn-outline-primary d-flex align-items-center">
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
    <% } %>

    <% if (quickBtnList.indexOf("bd-theme") > -1) { %>
    <!-- 테마 변경 버튼 -->
    <button id="bd-theme" title="테마 변경" type="button" class="btn btn-outline-primary dropdown-toggle d-flex align-items-center"
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
    <% } %>

</div>