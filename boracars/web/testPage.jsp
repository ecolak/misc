<%@page import="java.util.List, java.util.ArrayList" %>
<%@page import="dao.CLCar, dao.MakeModel" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%
List<String> allMakes = MakeModel.getAllMakes();
pageContext.setAttribute("allMakes", allMakes);
String cmd = request.getParameter("cmd");
pageContext.setAttribute("cmd", cmd);
if("search".equals(cmd)) {
    String make = request.getParameter("make");
    String minPrice = request.getParameter("min_price");
    String maxPrice = request.getParameter("max_price");
    String timeBack = request.getParameter("time_back");

    List<CLCar> cars = CLCar.getAll();
    request.setAttribute("cars", cars);
}
%>

<html>
    <head>
        <script src="js/jquery-1.5.1.min.js"></script>
        <link rel="stylesheet" type="text/css" href="css/displaytagex.css">
    </head>
    <body>      
        <h2 align="center">Search for cars on Craigslist</h2>
        <form method="get" action="testPage.jsp">
            <p align="center">
            Make: <select name="make">
                <option value="">---</option>
                <c:forEach items="${allMakes}" var="make">
                    <option value="${make}"><c:out value="${fn:toUpperCase(make)}" /></option>
                </c:forEach>
            </select>
            Model <select name="model">
                <option value="">---</option>
            </select>
            Min price: <input type="text" name="min_price" size="10" maxlength="7">
            Max price: <input type="text" name="max_price" size="10" maxlength="7">
            </p>
            <p align="center">
            Min year <input type="text" name="min_year" size="5" maxlength="4">
            Max year <input type="text" name="max_year" size="5" maxlength="4">
            Min mileage <input type="text" name="min_mileage" size="10" maxlength="6">
            Max mileage <input type="text" name="max_mileage" size="10" maxlength="6">
            Date: <select name="time_back">
                <option value="">---</option>
                <option value="1">Last hour</option>
                <option value="2">Last 3 hours</option>
                <option value="3">Last 12 hours</option>
                <option value="4">Yesterday</option>
                <option value="5">Last 2 days</option>
                <option value="6">Last week</option>
                <option value="7">Last 2 weeks</option>
                <option value="8">Last month</option>
            </select>
            <input type="hidden" name="cmd" value="search">
            <input type="submit" value="Search">
            </p>
        </form>
        <hr/>
        <c:if test="${cmd eq 'search'}">
            <display:table name="cars" defaultsort="1" pagesize="50"
                           decorator="ui.CLCarDecorator" class="dataTable">
                <display:column property="make" title="Make" sortable="true" />
                <display:column property="model" title="Model" sortable="true" />
                <display:column property="year" title="Year" sortable="true" />
                <display:column property="formattedPrice" title="Price" sortable="true"
                                sortProperty="price" />
                <display:column property="formattedMileage" title="Mileage" sortable="true"
                                sortProperty="mileage" />
                <display:column property="location" title="Location" sortable="true" />
                <display:column property="actions" title="" />
            </display:table>
        </c:if>
    </body>
</html>