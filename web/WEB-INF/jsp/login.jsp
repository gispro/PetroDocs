<%-- 
    Document   : login
    Created on : Apr 13, 2012, 10:32:14 AM
    Author     : fkravchenko
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Petroresource Documents System</title>
    </head>
    <body>
        <h1>Please Log In</h1>
        
        <form method="POST" action="j_security_check"/>
        Login: <input type="text" name="j_username"/><br/>
        Password: <input type="password" name="j_password"/><br/>
        <input type="submit"/>
        </form>        
        
    </body>
</html>
