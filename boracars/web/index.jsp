<%@page import="java.util.List, java.util.ArrayList, java.util.Date" %>
<%@page import="dao.CLCar, dao.MakeModel" %>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="org.apache.commons.lang.time.DateUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%
List<String> allMakes = MakeModel.getAllMakes();
List<String> models = new ArrayList<String>();
pageContext.setAttribute("allMakes", allMakes);
String cmd = request.getParameter("cmd");
pageContext.setAttribute("cmd", cmd);
String make = null;
String model = null;
String minPriceStr = null;
String maxPriceStr = null;
String minYearStr = null;
String maxYearStr = null;
String minMileageStr = null;
String maxMileageStr = null;
String hoursBackStr = null;
String soldBy = null;
boolean includeDeleted = false;
boolean includeFlagged = false;

if("search".equals(cmd)) {
    make = request.getParameter("make");
    model = request.getParameter("model");
    minPriceStr = request.getParameter("min_price");
    maxPriceStr = request.getParameter("max_price");
    minYearStr = request.getParameter("min_year");
    maxYearStr = request.getParameter("max_year");
    minMileageStr = request.getParameter("min_mileage");
    maxMileageStr = request.getParameter("max_mileage");
    hoursBackStr = request.getParameter("hours_back");
    soldBy = request.getParameter("sold_by");
    includeDeleted = "on".equals(request.getParameter("includeDeleted"));
    includeFlagged = "on".equals(request.getParameter("includeFlagged"));

    pageContext.setAttribute("make", make);
    pageContext.setAttribute("model", model);
    pageContext.setAttribute("minPriceStr", minPriceStr);
    pageContext.setAttribute("maxPriceStr", maxPriceStr);
    pageContext.setAttribute("minYearStr", minYearStr);
    pageContext.setAttribute("maxYearStr", maxYearStr);
    pageContext.setAttribute("minMileageStr", minMileageStr);
    pageContext.setAttribute("maxMileageStr", maxMileageStr);
    pageContext.setAttribute("hoursBackStr", hoursBackStr);
    pageContext.setAttribute("soldBy", soldBy);
    pageContext.setAttribute("includeDeleted", includeDeleted);
    pageContext.setAttribute("includeFlagged", includeFlagged);

    if(StringUtils.isNotBlank(make)) {
        models = MakeModel.getModelsByMake(make);
        pageContext.setAttribute("models", models);
    }

    Integer minPrice = null;
    try {
        minPrice = Integer.parseInt(minPriceStr);
    } catch(Exception e) { }

    Integer maxPrice = null;
    try {
        maxPrice = Integer.parseInt(maxPriceStr);
    } catch(Exception e) { }

    Integer minYear = null;
    try {
        minYear = Integer.parseInt(minYearStr);
    } catch(Exception e) { }

    Integer maxYear = null;
    try {
        maxYear = Integer.parseInt(maxYearStr);
    } catch(Exception e) { }

    Integer minMileage = null;
    try {
        minMileage = Integer.parseInt(minMileageStr);
    } catch(Exception e) { }

    Integer maxMileage = null;
    try {
        maxMileage = Integer.parseInt(maxMileageStr);
    } catch(Exception e) { }

    Date sListingTime = null;
    if(StringUtils.isNotBlank(hoursBackStr)) {
        try {
            int hoursBack = Integer.parseInt(hoursBackStr);
            sListingTime = DateUtils.addHours(new Date(), -hoursBack);
        } catch(Exception e) { }
    }

    List<CLCar> cars = CLCar.getCars(make, model, minPrice, maxPrice, minYear, maxYear,
            minMileage, maxMileage, sListingTime, null, soldBy, includeDeleted, includeFlagged);
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
        <form method="get" action="index.jsp">
            <p align="center">
            Make: <select name="make" id="make">
                <option value="">---</option>
                <c:forEach items="${allMakes}" var="m">
                    <option value="${m}" <c:if test="${m eq make}">selected</c:if> ><c:out value="${fn:toUpperCase(m)}" /></option>
                </c:forEach>
            </select>
            Model <select name="model" id="model">
                <option value="">---</option>
                <c:forEach items="${models}" var="m">
                    <option value="${m}" <c:if test="${m eq model}">selected</c:if> ><c:out value="${fn:toUpperCase(m)}" /></option>
                </c:forEach>
            </select>
            Min price: <input type="text" name="min_price" size="10" maxlength="7" value="${minPriceStr}">
            Max price: <input type="text" name="max_price" size="10" maxlength="7" value="${maxPriceStr}">
            Min year: <input type="text" name="min_year" size="5" maxlength="4" value="${minYearStr}">
            Max year: <input type="text" name="max_year" size="5" maxlength="4" value="${maxYearStr}">
            </p>
            <p align="center">
            Min mileage <input type="text" name="min_mileage" size="10" maxlength="6" value="${minMileageStr}">
            Max mileage <input type="text" name="max_mileage" size="10" maxlength="6" value="${maxMileageStr}">
            Sold by: <select name="sold_by">
                <option value="">---</option>
                <option value="owner" <c:if test="${'owner' eq soldBy}">selected</c:if>>Owner</option>
                <option value="dealer" <c:if test="${'dealer' eq soldBy}">selected</c:if>>Dealer</option>
            </select>
            Time frame: <select name="hours_back">
                <option value="">---</option>
                <option value="1" <c:if test="${'1' eq hoursBackStr}">selected</c:if> >Last hour</option>
                <option value="3" <c:if test="${'3' eq hoursBackStr}">selected</c:if> >Last 3 hours</option>
                <option value="12" <c:if test="${'12' eq hoursBackStr}">selected</c:if> >Last 12 hours</option>
                <option value="24" <c:if test="${'24' eq hoursBackStr}">selected</c:if> >Last 24 hours</option>
                <option value="48" <c:if test="${'48' eq hoursBackStr}">selected</c:if> >Last 2 days</option>
                <option value="84" <c:if test="${'84' eq hoursBackStr}">selected</c:if> >Last week</option>
                <option value="168" <c:if test="${'168' eq hoursBackStr}">selected</c:if> >Last 2 weeks</option>
                <option value="720" <c:if test="${'720' eq hoursBackStr}">selected</c:if> >Last 30 days</option>
            </select>
            Include deleted: <input type="checkbox" name="includeDeleted" <c:if test="${includeDeleted eq true}">checked</c:if> >
            Include flagged <input type="checkbox" name="includeFlagged" <c:if test="${includeFlagged eq true}">checked</c:if>>
            <input type="hidden" name="cmd" value="search">
            <input type="submit" value="Search">
            </p>
        </form>
        <hr/>
        <c:if test="${cmd eq 'search'}">
            <display:table name="cars" defaultsort="7" defaultorder="descending"
                           pagesize="50" sort="list" decorator="ui.CLCarDecorator"
                           class="dataTable">
                <display:column property="make" title="Make" sortable="true" />
                <display:column property="model" title="Model" sortable="true" />
                <display:column property="year" title="Year" sortable="true" />
                <display:column property="formattedPrice" title="Price" sortable="true"
                                sortProperty="price" />
                <display:column property="formattedMileage" title="Mileage" sortable="true"
                                sortProperty="mileage" />
                <display:column property="location" title="Location" sortable="true" />
                <display:column property="listingTimestamp" title="Listing time" sortable="true"
                                sortProperty="listingDate" />
                <display:column property="actions" title="" />
            </display:table>
        </c:if>
    </body>
</html>
<script type="text/javascript">
    $("select#make").change(function(){
        $.ajax({
            url: 'ajax/LoadModels.jsp',
            data: ({make: $(this).val()}),
            success: function(options) {
                $("select#model").html(options);
            }
        });
    });
</script>