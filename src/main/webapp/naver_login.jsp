<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="com.omnibuscode.utils.PropertiesUtil"%>
<%@ page import="com.omnibuscode.base.SafeProps"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
%>
<!DOCTYPE html>
<html>
  <head>
    <title>Naver Id Login</title>
    <meta charset="utf-8" />
    <%
        String clientId = SafeProps.getString("NAVER_CLIENT_ID", "");
        String redirectURI = URLEncoder.encode(SafeProps.getString("NAVER_REDIRECT_URI", ""), "UTF-8");
        String cuid = request.getParameter("cuid"); //clova user id
        String authCode = request.getParameter("ac"); //jiniebox temp authcode
        
        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectURI
            + "&state=" + cuid;
    %>
    <meta http-equiv="Refresh" content="0;url='<%=apiURL%>'">
  </head>
</html>