<%-- 
    Document   : error
    Created on : Apr 13, 2012, 12:09:17 PM
    Author     : fkravchenko
--%>

<%@page import="org.apache.log4j.Logger"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.apache.log4j.Logger"%> 
<%@page import="org.apache.log4j.MDC"%> 
<%
    Logger lgr = Logger.getLogger("util.UserSessions");
    MDC.put("user", request.getParameter("j_username"));
    MDC.put("OP_CODE", "LOGIN");
    MDC.put("OP_NAME", "Login");
    MDC.put("DOC_ID", "-");
    MDC.put("OP_STATUS", "ERROR");
    lgr.warn("Invalid login or password");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Error '${pageContext.request.userPrincipal.name}'</h1>
    </body>
</html>
