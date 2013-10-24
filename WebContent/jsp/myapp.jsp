<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="myapp.*,java.io.*,java.sql.Connection"%>
<%
request.setCharacterEncoding("UTF-8");
String s_out = "";
String className = request.getParameter("class");

if(className.equals("basic_user"))				s_out = basic_user.function(request);
if(className.equals("basic_group"))				s_out = basic_group.function(request);
if(className.equals("basic_parameter"))			s_out = basic_parameter.function(request);
if(className.equals("oa_person"))				s_out = oa_person.function(request);
if(className.equals("oa_plan"))					s_out = oa_plan.function(request);
if(className.equals("oa_work"))					s_out = oa_work.function(request);
if(className.equals("government_building"))		s_out = government_building.function(request);
if(className.equals("government_family"))		s_out = government_family.function(request);
if(className.equals("government_resident"))		s_out = government_resident.function(request);
if(className.equals("government_company"))		s_out = government_company.function(request);	
if(className.equals("gis_polygon"))				s_out = gis_polygon.function(request);

out.print(s_out);	
%>
