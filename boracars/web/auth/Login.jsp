<%@page import="util.AuthUtil"%>
<%@page import="dao.User"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String error = null;
    String cmd = request.getParameter("cmd");
    String username = null;
    String password = null;
    if("login".equals(cmd)) {
        try {
            username = request.getParameter("user_name");
            pageContext.setAttribute("username", username);
            if(StringUtils.isBlank(username)) {
                throw new Exception("Username cannot be blank");
            }

            password = request.getParameter("pass_word");
            if(StringUtils.isBlank(password)) {
                throw new Exception("Password cannot be blank");
            }

            String encPassword = AuthUtil.encryptWithMD5(password);
            User user = User.getByUsername(username);
            if(user == null) {
                throw new Exception("Username does not exist");
            }
            if(!user.getPassword().equals(encPassword)) {
                throw new Exception("Password does not match the given username");
            }

            if(session == null) {
                session = request.getSession();
            }
            session.setAttribute("user", user);
            String redirectUrl = (String)session.getAttribute("redirectUrl");
            if(redirectUrl != null) {
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch(Exception e) {
            error = e.getMessage();
            pageContext.setAttribute("error", error);
        }

    }
%>

<html>
    <head>
        <title>Login</title>
    </head>
    <body>
        <c:if test="${error ne null}">
            <p align="center">${error}</p>
        </c:if>
        <form method="post" action="Login.jsp">
            <input type="hidden" name="cmd" value="login">
            <table align="center" style="border: solid 1px;">
                <tr>
                    <td align="right">Username: </td>
                    <td align="left"><input type="text" name="user_name" value="${username}"></td>
                </tr>
                <tr>
                    <td align="right">Password: </td>
                    <td align="left"><input type="password" name="pass_word"></td>
                </tr>
                <tr><td colspan="2"><input type="submit" value="Login"></td></tr>
            </table>
        </form>
    </body>
</html>
