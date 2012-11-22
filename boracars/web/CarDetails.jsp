<%@page import="dao.CLCar" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    CLCar car = null;
    String c = request.getParameter("car_id");
    try {
        car = CLCar.getById(Integer.parseInt(c));
    } catch(Exception e) {
        // ignore
    }
    pageContext.setAttribute("car", car);
%>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/displaytagex.css">
    </head>
    <body>
        <c:if test="${car ne null}">
            <h2 align="center"><c:out value="${car.listingTitle}" /></h2>
            <table border="1" align="center" style="width: 750px;">
                <tr>
                    <td align="right"><strong>Link</strong></td>
                    <td align="left"><a href="<c:out value="${car.listingUrl}" />"><c:out value="${car.listingUrl}" /></a></td>
                </tr>
                <tr>
                    <td align="right"><strong>Make</strong></td>
                    <td align="left"><c:out value="${car.make}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Model</strong></td>
                    <td align="left"><c:out value="${car.model}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Year</strong></td>
                    <td align="left"><c:out value="${car.year}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Mileage</strong></td>
                    <td align="left"><c:out value="${car.formattedMileage}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Price</strong></td>
                    <td align="left"><c:out value="${car.formattedPrice}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Location</strong></td>
                    <td align="left"><c:out value="${car.location}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Contact email</strong></td>
                    <td align="left"><c:out value="${car.contactEmail}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Contact phone</strong></td>
                    <td align="left"><c:out value="${car.contactPhone}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Listing date</strong></td>
                    <td align="left"><c:out value="${car.listingDate}" /></td>
                </tr>
                <tr>
                    <td align="right"><strong>Sold by</strong></td>
                    <td align="left"><c:out value="${car.soldBy}" /></td>
                </tr>
            </table>
            <p align="center"><a href="javascript: history.go(-1)">Back</a></p>
        </c:if>
    </body>
</html>