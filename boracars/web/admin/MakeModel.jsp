<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="dao.MakeModel"%>
<%
    String cmd = request.getParameter("cmd");
    if("save".equals(cmd)) {
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        if(StringUtils.isBlank(make)) {
            out.println("Make cannot be blank");
        }
        else {
            make = make.trim().toLowerCase();
            if(StringUtils.isNotBlank(model)) {
                model = model.trim().toLowerCase();
            }

            MakeModel mm = MakeModel.getByMakeAndModel(make, model);
            if(mm != null) {
                out.println(make + " " + model + " already exists in the database");
            }
            else {
                mm = new MakeModel();
                mm.setMake(make);
                mm.setModel(model);
                mm.save();
                out.println(make + " " + model + " has been added to the database");
            }
        }
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Makes and Models</title>
    </head>
    <body>
        <form method="get" action="MakeModel.jsp">
            Make: <input type="text" name="make"> <br/><br/>
            Model: <input type="text" name="model"><br/><br/>
            <input type="hidden" name="cmd" value="save">
            <input type="submit" value="Save">
        </form>
    </body>
</html>
