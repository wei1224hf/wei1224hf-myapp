package myapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;


import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class oa_person {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			Hashtable t__grup_type = new Hashtable();
			if(!functionName.equals("login")){
				t__grup_type = basic_user.getSession(executor);	
			}
				
			String group_code = (String) t__grup_type.get("group_code");
			String user_type = (String) t__grup_type.get("user_type");
			
			if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "520221", session)){
					t = add(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}
			if(functionName.equals("modify")){
				if(basic_user.checkPermission(executor, "120221", session)){
					t = modify(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}
			if(functionName.equals("view")){
				t = view((String) request.getParameter("id"));		
			}
			if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}
			if(functionName.equals("lowerCodes")){
				out = g.toJson(lowerCodes((String)request.getParameter("code"), (String)request.getParameter("reference")));
				return out;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static ArrayList lowerCodes(String code,String reference) throws SQLException{	
		String sql = "";
		if(code.length()==2){
			sql = "select left(code,4) as code,value from basic_parameter where code like '"+code+"__00' and code <> '"+code+"0000' and reference = 'zone' order by value ";		
		}
		if(code.length()==4){
			sql = "select left(code,6) as code,value from basic_parameter where code like '"+code+"__' and code <> '"+code+"00' and reference = 'zone' order by value ";
		}
		if(code.length()==6){
			sql = "select code,name as value from basic_group where code like '"+code+"__' and code < '"+code+"70' order by name ";
		}
		if(code.length()==8){
			sql = "select code,name as value from basic_group where code like '"+code+"__' and code < '"+code+"70' order by name ";
		}	
		
		System.out.println(sql);
		Statement statement = tools.getConn().createStatement();
		ResultSet rs = statement.executeQuery(sql);
		ArrayList a = new ArrayList();
		ResultSetMetaData rsData = rs.getMetaData();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			for(int i=1;i<=rsData.getColumnCount();i++){
				if(rs.getString(rsData.getColumnLabel(i)) != null){
					t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
				}else{
					t.put(rsData.getColumnLabel(i), "nul");
				}
			}
			a.add(t);
		}
		return a;
	}
	
	public static Hashtable loadConfig() throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		
		String sql ;
		ResultSet rs ;
		ArrayList a;
		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__card' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("card", a);		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__gender' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("gender", a);
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__marriage' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("marriage", a);		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__nation' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("nation", a);		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__politically' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("politically", a);		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_person__degree' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("degree", a);		
		
		sql = "select left(code,2) as code,value from basic_parameter where reference = 'zone' and code like '__0000' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("zone_2", a);			
		
		return t_return;
	}
	
	public static Hashtable add(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		
		data = data.replace("null", "\"\"");
		System.out.println(data);
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	

		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			if(value.equals("null") || value.length()==0 || value.equals("")){
				t_data.remove(key);
			}else{
				t_data.put(key, "'"+value+"'");
			}			
		}
		
		String id = String.valueOf(tools.getTableId("oa_person"));
		t_data.put("id", id);
		t_data.put("creater_code", "'"+executor+"'");
		t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

		e = t_data.keys();
		String keys = "insert into oa_person (";
		String values = ") values (";		
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();

			keys += key+",";
			values += (String)t_data.get(key)+",";
		}
		keys = keys.substring(0,keys.length()-1);
		values = values.substring(0,values.length()-1);
		
		String sql = keys + values + ");";
		System.out.println(sql);		
		statement.executeUpdate(sql);
		
		t_return.put("status", "1");
		t_return.put("id", ""+id);
		t_return.put("msg", "ok");
		
		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		
		data = data.replace("null", "\"\"");
		
		String id = (String) t_data.get("id");
		t_data.remove("id");
		String sql = "";
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			if(value.equals("null") || value.length()==0 || value.equals("")){
				t_data.remove(key);
			}else{
				t_data.put(key, "'"+value+"'");
			}
			
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update oa_person set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where id = '"+id+"' ";
		
		System.out.println(sql);		
		tools.getConn().createStatement().executeUpdate(sql);	
		
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		return t_return;
	}		
	
	public static Hashtable view(String id){
		Hashtable t_return = new Hashtable();
		try {
			Statement statement = tools.getConn().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = tools.getConfigItem("oa_person__view").replace("__id__", "'"+id+"'");
		System.out.println(sql);
		ResultSet resultset;
		try {
			resultset = tools.getConn().createStatement().executeQuery(sql);
			if(resultset.next()){
				ResultSetMetaData m = resultset.getMetaData();
				LinkedHashMap t_data = new LinkedHashMap();
				for(int i=1;i<=m.getColumnCount();i++){
					String key  = m.getColumnLabel(i);
					String value = "";
					if(resultset.getString(m.getColumnLabel(i)) != null){
						value = resultset.getString(m.getColumnLabel(i));
		
					}
					t_data.put(key, value);
					
				}
				t_return.put("data",t_data);
				t_return.put("status", "1");
				t_return.put("msg", "ok");
			}else{
				t_return.put("status", "2");
				t_return.put("msg", "No such user");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return t_return;
	}
	
	public static void main(String args[]) throws SQLException{

	}	
}
