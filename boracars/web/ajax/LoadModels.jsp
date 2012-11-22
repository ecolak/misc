<%@page import="java.util.List, java.util.ArrayList" %>
<%@page import="dao.CLCar, dao.MakeModel" %>
<%
    String make = request.getParameter("make");
    List<String> models = MakeModel.getModelsByMake(make);
%>
    <option value="">---</option>
<%
    for(String model : models) {
%>
        <option value="<%=model%>"><%=model.toUpperCase()%></option>
<%
    }
%>
