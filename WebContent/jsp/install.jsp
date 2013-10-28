<%@page import="com.google.gson.Gson"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="myapp.*,com.google.gson.*"%>
<%
String path = application.getRealPath("/");
String function = request.getParameter("function");
String print = "";
Gson gson = new Gson();


if(function.equals("step1")){
	install.path = path;
	print = gson.toJson( install.step1() );
}

if(function.equals("step2")){
	install.unm = request.getParameter("unm");
	install.host = request.getParameter("host");
	install.pwd = request.getParameter("pwd");
	install.port = request.getParameter("port");
	install.db = request.getParameter("db");
	install.il8n = request.getParameter("il8n");
	install.type = request.getParameter("type");
	install.mode = request.getParameter("mode");
	print = gson.toJson( install.step2() );
}

if(function.equals("step3")){
	install.path = path;
	print = gson.toJson( install.step3() );
}

if(function.equals("step4")){
	install.path = path;
	print = gson.toJson( install.step4() );
}

if(function.equals("basic_group")){
	install.path = path;
	print = gson.toJson( install.basic_group() );
}
if(function.equals("basic_user")){
	install.path = path;
	print = gson.toJson( install.basic_user() );
}
if(function.equals("oa_plan")){
	install.path = path;
	print = gson.toJson( install.oa_plan() );
}
if(function.equals("oa_work")){
	install.path = path;
	print = gson.toJson( install.oa_work() );
}
if(function.equals("government_building")){
	install.path = path;
	print = gson.toJson( install.government_building() );
}
if(function.equals("government_company")){
	install.path = path;
	print = gson.toJson( install.government_company() );
}
if(function.equals("government_family")){
	install.path = path;
	print = gson.toJson( install.government_family() );
}
if(function.equals("government_resident")){
	install.path = path;
	print = gson.toJson( install.government_resident() );
}

out.print(print);
%>