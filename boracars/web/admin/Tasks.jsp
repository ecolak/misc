<%@page import="java.util.List" %>
<%@page import="dao.Task" %>
<%@page import="util.TaskUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String cmd = request.getParameter("cmd");
    String idStr = request.getParameter("id");
    List<Task> allTasks = null;
    int taskId = -1;
    Task task  = null;
    try {
        taskId = Integer.parseInt(idStr);
        task  = Task.getById(taskId);
    } catch(Exception e) { }

    if("save".equals(cmd)) {
        String taskName = request.getParameter("task_name");
        String className = request.getParameter("class_name");
        String parameters = request.getParameter("parameters");
        String delay = request.getParameter("delay");
        String period = request.getParameter("period");

        if(task == null) {
            task = new Task();
        }
    
        task.setTaskName(taskName);
        task.setClassName(className);
        task.setParameters(parameters);
        task.setDelay(Integer.parseInt(delay));
        task.setPeriod(Integer.parseInt(period));
        task.save();
        response.sendRedirect("Tasks.jsp?id=" + task.getTaskId());
    } else if("enable".equals(cmd)) {
        TaskUtil.enableTask(taskId);
        response.sendRedirect("Tasks.jsp");
    } else if("disable".equals(cmd)) {
        TaskUtil.disableTask(taskId);
        response.sendRedirect("Tasks.jsp");
    } else {
        allTasks = Task.getAll();
    }
    
    pageContext.setAttribute("task", task);
    pageContext.setAttribute("taskId", taskId);  
    pageContext.setAttribute("tasks", allTasks);
%>
<html>
    <head></head>
    <body>
        <h3 align="center">Tasks</h3>
        <hr />
        <c:choose>
            <c:when test="${taskId gt 0}">
                <form method="get" action="Tasks.jsp">
                    <table align="center">
                        <tr><td>Task name: </td><td><input type="text" name="task_name" size="50" value="${task.taskName}"></tr>
                        <tr><td>Class name: </td><td><input type="text" name="class_name" size="50" value="${task.className}"></tr>
                        <tr><td>Parameters: </td><td><input type="text" name="parameters" size="50" value="${task.parameters}"></tr>
                        <tr><td>Delay: </td><td><input type="text" name="delay" value="${task.delay}"></tr>
                        <tr><td>Period: </td><td><input type="text" name="period" value="${task.period}"></tr>
                    </table>
                    <input type="hidden" name="cmd" value="save">
                    <input type="hidden" name="id" value="${task.taskId}">
                    <p align="center"><input type="submit" value="Submit"></p>
                </form>
            </c:when>
            <c:when test="${taskId eq 0}">
                <form method="get" action="Tasks.jsp">
                    <table align="center">
                        <tr><td>Task name: </td><td><input type="text" name="task_name" size="50"></tr>
                        <tr><td>Class name: </td><td><input type="text" name="class_name" size="50"></tr>
                        <tr><td>Parameters: </td><td><input type="text" name="parameters" size="50"></tr>
                        <tr><td>Delay: </td><td><input type="text" name="delay"></tr>
                        <tr><td>Period: </td><td><input type="text" name="period"></tr>
                    </table>
                    <input type="hidden" name="cmd" value="save">
                    <p align="center"><input type="submit" value="Submit"></p>
                </form>
            </c:when>
            <c:otherwise>
                <table border="1" align="center" style="width: 750px;">
                    <tr>
                        <th>Task Id</th><th>Task Name</th><th>Class Name</th>
                        <th>Delay</th><th>Period</th><th>Parameters</th>
                    </tr>
                    <c:forEach items="${tasks}" var="t">
                        <tr>
                            <td><a href="Tasks.jsp?id=${t.taskId}">${t.taskId}</a></td>
                            <td>${t.taskName}</td>
                            <td>${t.className}</td>
                            <td>${t.delay}</td>
                            <td>${t.period}</td>
                            <td>${t.parameters}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${t.enabled}">
                                        <a href="Tasks.jsp?cmd=disable&id=${t.taskId}">Disable</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="Tasks.jsp?cmd=enable&id=${t.taskId}">Enable</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><a href="TaskStats.jsp?taskId=${t.taskId}">Stats</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </body>
</html>