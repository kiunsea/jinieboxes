<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String quickBtnList = request.getParameter("quick_btn_list");
    String pageName = request.getParameter("page_name");
    if (quickBtnList == null) quickBtnList = "";
%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="jiniebox">
    <meta name="generator" content="">
    <title><%= request.getParameter("title") %></title>

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

<% if (quickBtnList.indexOf("btn_item_add") > -1) { %>
    <script src="./res/web/bootstrap/ext/vanillajs-datepicker@1.3.2/datepicker-full.min.js"></script>
    <script src="./res/web/bootstrap/ext/vanillajs-datepicker@1.3.2/locales/ko.js"></script>
<% } %>
    <script src="./res/web/color-modes.js"></script>
<% if (pageName != null) { %>
    <script src="./res/web/onboarding/driver/1.0.1/driver.js.iife.js"></script>
    <script src="/jbs/onboarding?sys_scenario=<%=pageName%>" charset="UTF-8"></script>
    <script src="/jbs/onboarding?sys_scenario=<%=pageName%>_pop" charset="UTF-8"></script>
<% } %>
    <script src="./res/web/jiniebox.js"></script>