<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
  Object titleObj = request.getAttribute("title");
  Object gmailObj = request.getAttribute("gmail");
  Object rstCdObj = request.getAttribute("code");
  Object msgObj = request.getAttribute("msg");
  
  String headTitle = null;
  String bodyTitle = null;
  if (titleObj == null) {
    headTitle = "JINIEBOX";
    bodyTitle = "";
  } else {
    bodyTitle = headTitle = titleObj.toString();
  }  
  String gmail = (gmailObj == null) ? null : gmailObj.toString();
  String rstCd = (rstCdObj == null) ? null : rstCdObj.toString();
  String msg = null;
  if (msgObj == null) {
    msg = null;
  } else {
    msg = bodyTitle = msgObj.toString();
  }
%>
<html>
  <head>
    <title><%=headTitle%></title>
    <meta charset="utf-8" />
  </head>
  <body>
    <p><%=bodyTitle%></p>

    <script type="text/javascript">
      <%
        if (gmail != null && rstCd != null) {
      %>
        var _opener_doc = window.opener.document;
        _opener_doc.store.gauthcd = "<%=rstCd%>"; //구글 인증 결과값 설정
        _opener_doc.querySelector("#gmail").value = "<%=gmail%>"; //구글 계정 설정
        _opener_doc.querySelector("#storage_google").checked = true;
        _opener_doc.querySelector("#gmail").setAttribute("disabled", "");
        _opener_doc.querySelector("#gmail").setAttribute("readonly", "");
        _opener_doc.querySelector("#btn_get_token").innerText = "해제";
      <%
        }

        if (msg != null) {
      %>
        alert("<%=msg%>");
      <%
        }
      %>
        window.close();
    </script>
  </body>
</html>