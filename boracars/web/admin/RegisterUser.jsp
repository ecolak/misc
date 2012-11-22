<%@page import="util.AuthUtil"%>
<%@page import="dao.User"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String error = null;
    String message = null;
    String cmd = request.getParameter("cmd");
    String username = null;
    String password1 = null;
    String password2 = null;
    String reset = null;
    if("register".equals(cmd)) {
        try {
            username = request.getParameter("user_name");
            pageContext.setAttribute("username", username);
            if(StringUtils.isBlank(username)) {
                throw new Exception("Username cannot be blank");
            }

            password1 = request.getParameter("pass_word1");
            if(StringUtils.isBlank(password1)) {
                throw new Exception("Password cannot be blank");
            }

            password2 = request.getParameter("pass_word2");
            if(StringUtils.isBlank(password2)) {
                throw new Exception("You need to repeat the password");
            }

            if(!password1.equals(password2)) {
                throw new Exception("Passwords do not match");
            }

            if(password1.length() < 6 || password1.length() > 12) {
                throw new Exception("Password needs to be between 6 and 12 characters");
            }

            reset = request.getParameter("reset");
            User user = User.getByUsername(username);
            if(user != null) {
                if(!"on".equals(reset)) {
                    throw new Exception("Username already exists. Please choose another one");
                }
            } else {
                if("on".equals(reset)) {
                    throw new Exception("Cannot reset password because user does not exist.");
                }
                user = new User();
            }

            String encPassword = AuthUtil.encryptWithMD5(password1);
            user.setUsername(username);
            user.setPassword(encPassword);
            user.setDateModified(new Date());
            user.save();

            message = "User " + username + " has been registered successfully";
            pageContext.setAttribute("message", message);
            
        } catch(Exception e) {
            error = e.getMessage();
            pageContext.setAttribute("error", error);
        }

    }
%>

<html>
    <head>
        <title>Register User</title>
    </head>
    <body>
        <c:if test="${error ne null}">
            <p align="center">${error}</p>
        </c:if>
            <c:if test="${message ne null}">
            <p align="center">${message}</p>
        </c:if>
        <form method="post" action="RegisterUser.jsp">
            <input type="hidden" name="cmd" value="register">
            <table align="center" style="border: solid 1px;">
                <tr>
                    <td align="right">Username: </td>
                    <td align="left"><input type="text" name="user_name" value="${username}"></td>
                </tr>
                <tr>
                    <td align="right">Password: </td>
                    <td align="left"><input type="password" name="pass_word1"></td>
                </tr>
                <tr>
                    <td align="right">Repeat password: </td>
                    <td align="left"><input type="password" name="pass_word2"></td>
                </tr>
                <tr>
                    <td align="right">
                        <input type="checkbox" name="reset"
                               <%="on".equals(reset) ? "checked" : ""%>>
                    </td>
                    <td align="left">Reset password for existing user</td>
                </tr>
                <td colspan="2"><font size="2">* Password needs to be between 6 and 12 characters</font></td>
                <tr><td></td><td><input type="submit" value="Register"></td></tr>
            </table>
        </form>
    </body>
</html>
