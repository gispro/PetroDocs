<%-- 
    Document   : error
    Created on : Apr 13, 2012, 12:09:17 PM
    Author     : fkravchenko
--%>

<%@page import="org.apache.log4j.Level"%>
<%@page import="ru.gispro.petrores.doc.util.UserSessions"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.apache.log4j.MDC"%> 
<%
    UserSessions.warn("util.UserSessions", request.getParameter("j_username"), 
                     "LOGIN", "Login", null, false, "Invalid login or password");

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
