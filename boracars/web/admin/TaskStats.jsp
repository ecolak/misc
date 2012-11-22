<%@page import="java.util.List" %>
<%@page import="dao.TaskStat" %>
<%@page import="org.apache.commons.lang.math.NumberUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%
    String taskIdStr = request.getParameter("taskId");
    long taskId = NumberUtils.toLong(taskIdStr);
    if(taskId > 0) {
        List<TaskStat> stats = TaskStat.getByTaskId(taskId);
        request.setAttribute("stats", stats);
    }
%>

<html>
    <head>
        <title>Task Stats</title>
        <link rel="stylesheet" type="text/css" href="../css/displaytagex.css">
    </head>
    <body>
        <h3 align="center">Task Stats</h3>
        <hr />
        <c:if test="${stats ne null}">
             <display:table name="stats" defaultsort="3" defaultorder="descending"
                           pagesize="50" sort="list" decorator="ui.TaskStatsDecorator"
                           class="dataTable">
                <display:column property="taskName" title="Task Name" sortable="true" />
                <display:column property="formattedStartTime" title="Start Time" sortable="true" sortProperty="startTime" />
                <display:column property="formattedEndTime" title="End Time" sortable="true" sortProperty="endTime" />
                <display:column property="formattedDuration" title="Duration" sortable="true" sortProperty="durationInMillis" />
             </display:table>
        </c:if>
    </body>
</html>
