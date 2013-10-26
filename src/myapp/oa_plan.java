package myapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class oa_plan {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
		t.put("state", "2");
		t.put("msg", "access denied");	
		t.put("user", executor);
		t.put("session", session);
	
		try {	
			if (functionName.equals("grid")) {	
				if(basic_user.checkPermission(executor, "500120", session)){
					String sortname = "id";
					String sortorder = "asc";
					if( request.getParameter("sortname") != null ){
						sortname = (String) request.getParameter("sortname");
					}
					if( request.getParameter("sortorder") != null ){
						sortorder = (String) request.getParameter("sortorder");
					}				
					t = grid(
						 (String) request.getParameter("search")
						,(String) request.getParameter("pagesize")
						,(String) request.getParameter("page")
						,executor
						,sortname
						,sortorder
						);		
				}
			}			
			else if (functionName.equals("gantt")) {			
				if(basic_user.checkPermission(executor, "500101", session)){
					t= gantt(
							(String) request.getParameter("search"),
							(String) request.getParameter("pagesize"),
							(String) request.getParameter("page"),
							executor
							);
				}
			}
			else if (functionName.equals("statistics_time")) {		
				if(basic_user.checkPermission(executor, "500192", session)){
					t= statistics_time( (String) request.getParameter("search") );
				}
			}
			else if (functionName.equals("statistics_attribute")) {		
				if(basic_user.checkPermission(executor, "500192", session)){
					t= statistics_attribute(
						(String) request.getParameter("search")
						,(String) request.getParameter("attribute")
						);
				}
			}
			else if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "500121", session)){
					t = add(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}else if(functionName.equals("modify")){
				if(basic_user.checkPermission(executor, "120221", session)){
					t = modify(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}
			else if(functionName.equals("examine")){
				if(basic_user.checkPermission(executor, "120221", session)){
					t = examine(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}
			else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120223", session)){
					t = remove(
							(String)request.getParameter("ids"),
							executor
							);
				}				
			}else if(functionName.equals("view")){
				if(basic_user.checkPermission(executor, "500102", session)){
					t = view((String) request.getParameter("id"));		
				}
			}else if(functionName.equals("checkCode")){
				t = checkCode((String) request.getParameter("code"));		
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}else if(functionName.equals("usergrid")){
				//TODO 权限编号待设置
				t = usergrid(
						(String) request.getParameter("search"),
						(String) request.getParameter("pagesize"),
						(String) request.getParameter("page"),
						executor
						);				
			}
			else if(functionName.equals("groupgrid")){
				//TODO 权限编号待设置
				t = groupgrid(
						(String) request.getParameter("search"),
						(String) request.getParameter("pagesize"),
						(String) request.getParameter("page"),
						executor
						);				
			}	
			else if(functionName.equals("quotesgrid")){
				//TODO 权限编号待设置
				t = quotesgrid(
						(String) request.getParameter("search"),
						(String) request.getParameter("pagesize"),
						(String) request.getParameter("page"),
						executor
						);				
			}				
			else if(functionName.equals("lowerCodes")){
				out = g.toJson(lowerCodes((String)request.getParameter("code"), (String)request.getParameter("reference")));
				return out;
			}
			else if(functionName.equals("download")){
				if(basic_user.checkPermission(executor, "520212", session)){
					t = download(
							(String) request.getParameter("search"),
							(String) request.getParameter("pagesize"),
							(String) request.getParameter("page"),
							executor
							);		
				}
			}
			else if(functionName.equals("upload")){
				if(basic_user.checkPermission(executor, "520212", session)){
					t = upload(
							(String) request.getParameter("path"),
							executor
							);		
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable checkCode(String code){
		Hashtable t_return = new Hashtable();
		String sql = "";
		try {
			Statement statement = tools.getConn().createStatement();
			sql = "select * from oa_plan where code = '"+code+"' ";
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next()){
				t_return.put("status", "0");
			}else{
				t_return.put("status", "1");
			}
		} catch (SQLException e) {
			t_return.put("status", "0");
			t_return.put("sql", sql);
//			e.printStackTrace();
		}
		return t_return;
	}
	
	public static Hashtable loadConfig() {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();
			
			sql = "select code,name as value from basic_group where code like  '2102__' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_6", a);		
			
			sql = "select code,name as value from basic_group where code like  '210204130_' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_10", a);					
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_plan__type' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_plan__type", a);					
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_plan__status' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_plan__status", a);			
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_plan__relation' and (code like '10__' or code = '9001' or code = '00') order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_plan__relation", a);					
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'industry' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("industry", a);
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}
	
	public static Hashtable lowerCodes(String code,String reference) {	
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();
			
			sql = "";
			if(code.length()==10){
				sql = "select code,name as value from government_building where code like '"+code+"BD____' order by name ";
			}else if(code.length()==16){
				sql = "select code,name as value from government_family where code like '"+code+"____' order by name ";
			}else{
				sql = "select code,name as value from basic_group where code like '"+code+"__' and type = '11' ";
			}
			
			System.out.println(sql);
			statement = tools.getConn().createStatement();
			rs = statement.executeQuery(sql);
			a = new ArrayList();
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
			t_return.put("data", a);
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}		
	
	public static Hashtable add(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		try {
			statement = conn.createStatement();
			System.out.println(data);
			data = data.replace("null", "\"\"");
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
			t_data.put("groups_participate", t_data.get("group_incharge")+","+group_code);
			t_data.put("content", ((String)t_data.get("content")).replaceAll("&amp;", "&")
				     .replaceAll("&lt;", "<")
				     .replaceAll("&gt;", ">")
				     .replaceAll("&apos;", "\'")
				     .replaceAll("&quot;", "\"")
				     .replaceAll("&nbsp;", " ")
				     .replaceAll("&copy;", "@")
				     .replaceAll("&reg;", "?"));
			String theUp = (String) t_data.get("theUp");
			t_data.remove("theUp");
			String sql_select = "";
			if(!theUp.equals("")){
				sql_select = "select max(code) as thecode from oa_plan where code like '"+theUp+"___';";
			}else{
				sql_select = "select max(code) as thecode from oa_plan where code like 'G"+group_code+"N_____';";
			}
			
			rs = statement.executeQuery(sql_select);
			String theMaxCode = "";
			int i;
			while(rs.next()){
				theMaxCode = rs.getString("thecode");
				if(theMaxCode==null){
					 if(theUp.equals("")){
						 theMaxCode = "G"+group_code+"N00001";
					 }else{
						 theMaxCode = theUp+"001";
					 }					 
					 break;
				}
				theMaxCode = theMaxCode.substring(theMaxCode.indexOf("N")+1,theMaxCode.length());				
				i = Integer.valueOf(theMaxCode);
				i++;
				theMaxCode = "";
				if(theUp.equals("")){
					if(i<10000){
						theMaxCode = "0"+theMaxCode;
					}
					if(i<1000){
						theMaxCode = "0"+theMaxCode;
					}				
				}	
				if(i<100){
					theMaxCode = "0"+theMaxCode;
				}
				if(i<10){
					theMaxCode = "0"+theMaxCode;
				}
				break;
			}
			String theNextCode = "";
			if(theUp.equals("")){
				theNextCode = "G"+group_code+"N"+theMaxCode;
			}else{
				theNextCode = theUp+theMaxCode;
			}			
			t_data.put("code", theNextCode);	
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
			
			String id = String.valueOf(tools.getTableId("oa_plan"));
			t_data.put("id", id);
			t_data.put("creater_code", "'"+executor+"'");
			t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");
	
			e = t_data.keys();
			String keys = "insert into oa_plan (";
			String values = ") values (";		
			while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
				keys += key+",";
				values += (String)t_data.get(key)+",";
			}
			keys = keys.substring(0,keys.length()-1);
			values = values.substring(0,values.length()-1);
			
			sql = keys + values + ");";
			System.out.println(sql);		
			statement.executeUpdate(sql);
			
			t_return.put("status", "1");
			t_return.put("msg", "ok");
			t_return.put("code", theNextCode);
			t_return.put("id", id);	
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}	
	
	public static Hashtable grid(
			 String search
			,String pagesize
			,String pagenum
			,String executor
			,String sortname
			,String sortorder) {
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			
		
		String sql = tools.getSQL("oa_plan__grid");
		String sql_orderby = " order by "+sortname+" "+sortorder;
		String where = oa_plan.search(search,executor);	
		String page = " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";
		sql = sql.replace("__WHERE__", where);
		sql = sql.replace("__ORDER__", sql_orderby);
		sql = sql.replace("__PAGE__", page);
		
		ArrayList a = new ArrayList();
		try {
			stmt = conn.createStatement();
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			ResultSetMetaData rsData = rset.getMetaData();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				a.add(t);
			}
			t_return.put("Rows", a);
			
			String sql_total = "select count(*) as count_ from oa_plan " + where;
			rset = stmt.executeQuery(sql_total);
			rset.next();
			t_return.put("Total", rset.getString("count_"));
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e1.toString());
		} finally {
			try { if (rset != null) rset.close(); } catch(Exception ex) { }
			try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
			try { if (conn != null) conn.close(); } catch(Exception ex) { }
       }

		return t_return;
	}		
	
	public static Hashtable remove(String ids,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++){
				sql = "delete from oa_plan where id = '"+id[i]+"' ;";
				statement.executeUpdate(sql);
			}		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }		
		
		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();			
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
			t_data.put("content", ((String)t_data.get("content")).replaceAll("&amp;", "&")
				     .replaceAll("&lt;", "<")
				     .replaceAll("&gt;", ">")
				     .replaceAll("&apos;", "\'")
				     .replaceAll("&quot;", "\"")
				     .replaceAll("&nbsp;", " ")
				     .replaceAll("&copy;", "@")
				     .replaceAll("&reg;", "?"));
			String id = (String) t_data.get("id");
			t_data.remove("id");
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
			sql = "update oa_plan set ";
		
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
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }	
		
		return t_return;
	}	
	
	public static Hashtable examine(String data,String executor)  {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();	
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
	
			String id = (String) t_data.get("id");
			t_data.remove("id");
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
			sql = "update oa_plan set ";
		
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
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		return t_return;
	}		
	
	public static Hashtable view(String id) throws SQLException {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();	
			sql = tools.getSQL("oa_plan__view").replace("__id__", "'"+id+"'");
			System.out.println(sql);
			ResultSet resultset = tools.getConn().createStatement().executeQuery(sql);
			resultset.next();
			ResultSetMetaData m = resultset.getMetaData();
			LinkedHashMap t_data = new LinkedHashMap();
			for(int i=1;i<=m.getColumnCount();i++){
				String key  = m.getColumnLabel(i);
				String value = "-";
				if(resultset.getString(m.getColumnLabel(i)) != null){
					value = resultset.getString(m.getColumnLabel(i));
				}
				t_data.put(key, value);			
			}
			t_return.put("data",t_data);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}
	
	private static String search(
			 String search
			,String executor
			){
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = ","+(String) t__user_session.get("groups")+",";
		
		//一般用户
		String where = " where oa_plan.creater_group_code = '"+user_group+"' and oa_plan.code like '%--%-%-%' ";
		//组管理员
		if(user_groups.contains(",X1")){
			where = " where oa_plan.creater_group_code = '"+user_group+"' or oa_plan.groups_participate like ',"+user_group+",' ";
		}
		//系统用户
		if(user_type.equals("10"))where = " where 1=1 ";
		

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and oa_plan.name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and oa_plan.type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and oa_plan.status = '"+value+"'";
			}
			if(key.equals("name")){
				where += " and oa_plan.name like '%"+value+"%'";
			}			
			if(key.equals("group_incharge")){
				where += " and oa_plan.group_incharge = '"+value+"'";
			}
			
			if(key.equals("plan_time_start")){
				where += " and oa_plan.plan_time_start > '"+value+"'";
			}
			if(key.equals("plan_time_stop")){
				where += " and oa_plan.plan_time_stop < '"+value+"'";
			}			
			
		}
		return where;
	}	
	
	public static Hashtable gantt(
			 String search
			,String pagesize
			,String pagenum
			,String executor
			){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();	
		
			sql =  tools.getSQL("oa_plan__gantt");
			String where = oa_plan.search(search,executor);	
	
			String sql_order = " order by oa_plan.code  ";
			sql += where + sql_order + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";
			System.out.println(sql);
			
			a = new ArrayList();
	
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			
			ResultSetMetaData rsData = rs.getMetaData();
			int index = 0;
			while (rs.next()) {
				index ++;
				Hashtable t = new Hashtable();	
				Hashtable thevalue = new Hashtable();
				Hashtable dataObj = new Hashtable();
				for(int i=1;i<=rsData.getColumnCount();i++){				
					if(rsData.getColumnLabel(i).equals("from_")){
						thevalue.put("from", "/Date("+rs.getString(rsData.getColumnLabel(i))+"000)/");
					}
					if(rsData.getColumnLabel(i).equals("to_")){
						thevalue.put("to", "/Date("+rs.getString(rsData.getColumnLabel(i))+"000)/");
					}	
					if(rsData.getColumnLabel(i).equals("name")){
						thevalue.put("label", rs.getString(rsData.getColumnLabel(i)));
						thevalue.put("desc", rs.getString("name")+"<br/>"+rs.getString("code")+"<br/>"+rs.getString("time_start")+"&nbsp;&nbsp;"+rs.getString("time_stop"));
						String name =  rs.getString(rsData.getColumnLabel(i));
						String code = rs.getString("code");
						String[] arr = code.split("--");
						String code_ = arr[1];
						int len = (code_.length()-2)/3;
						for(int j=0;j<len;j++){
							name = "--"+name;
						}
						t.put("name",name);
						//t.put("desc",rs.getString("code"));
					}	
					if(rs.getString(rsData.getColumnLabel(i)) != null){
						dataObj.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
					}else{
						dataObj.put(rsData.getColumnLabel(i), "nul");
					}
				}
				dataObj.put("index", index);
				thevalue.put("customClass", "ganttRed");
				thevalue.put("dataObj", dataObj);
				ArrayList a2 = new ArrayList();
				a2.add(thevalue);
				t.put("values",a2);
				a.add(t);
			}	
			
			if(a.size()==0){
				Hashtable t = new Hashtable();	
				Hashtable thevalue = new Hashtable();
				thevalue.put("label", "NULL");
				thevalue.put("from", "/Date(946656000000)/");
				thevalue.put("to", "/Date(978278400000)/");
				thevalue.put("customClass", "ganttRed");
				ArrayList a2 = new ArrayList();
				a2.add(thevalue);	
				t.put("values",a2);
				t.put("name", "NULL");
				a.add(t);
				t_return.put("Rows", a);
				t_return.put("Total", "1");
			}else{
				t_return.put("Rows", a);
				String sql_total = "select count(*) as count_ from oa_plan "+where;
				rs = statement.executeQuery(sql_total);
				rs.next();
				t_return.put("Total", rs.getString("count_"));	
			}

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}	
	
	public static Hashtable statistics_time(String search){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();	
			
			sql = tools.getSQL("oa_plan__statistics_time");
			String where = " where 1=1 ";
			sql = sql.replace("__size__", "7");
			search = search.replace("%22", "\"");
			System.out.print(search);
			Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
			for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = search_t.get(key);
				if(key.equals("plan_time_start")){
					where += " and plan_time_start > '"+value+"'";
				}
				if(key.equals("plan_time_stop")){
					where += " and plan_time_stop < '"+value+"'";
				}
				if(key.equals("type")){
					where += " and type = '"+value+"'";
				}
				if(key.equals("status")){
					where += " and status = '"+value+"'";
				}
			}
			ArrayList xAxis = new ArrayList();
			ArrayList series = new ArrayList();
			sql += where;
			sql += " group by left(oa_plan.plan_time_start, 7)  ";

			statement = tools.getConn().createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);

			ResultSetMetaData rsData = rs.getMetaData();
		
			while (rs.next()) {			
				xAxis.add(rs.getString("time_"));
				series.add(rs.getInt("count_"));
			}
	
			t_return.put("xAxis", xAxis);	
			t_return.put("series", series);	

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}	
	
	public static Hashtable statistics_attribute(String search,String attribute){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		try {
			statement = conn.createStatement();	
			
			sql = "select count(*) as count_ , "+attribute+" from oa_plan ";
			String where = " where 1=1 ";
			sql = sql.replace("__size__", "7");
			search = search.replace("%22", "\"");
			System.out.print(search);
			Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
			for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = search_t.get(key);
				if(key.equals("plan_time_start")){
					where += " and plan_time_start > '"+value+"'";
				}
				if(key.equals("plan_time_stop")){
					where += " and plan_time_stop < '"+value+"'";
				}
				if(key.equals("type")){
					where += " and type = '"+value+"'";
				}
				if(key.equals("status")){
					where += " and status = '"+value+"'";
				}
			}
			ArrayList xAxis = new ArrayList();
			ArrayList series = new ArrayList();
			sql += where;
			sql += " group by "+attribute;

			statement = tools.getConn().createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);

			ResultSetMetaData rsData = rs.getMetaData();
		
			while (rs.next()) {			
				xAxis.add(rs.getString("attribute"));
				series.add(rs.getInt("count_"));
			}

			t_return.put("xAxis", xAxis);	
			t_return.put("series", series);	

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}		
	
	public static Hashtable upload(String path,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		String filePath = path;
		InputStream fs = null;
		Workbook workBook = null;
		
		System.out.println(filePath);
		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
         e.printStackTrace();
        } catch (BiffException e) {
         e.printStackTrace();
        } catch (IOException e) {
         e.printStackTrace();
        }

        Sheet sheet = workBook.getSheet("data_oa_plan");
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        if(rows>2000){
    		t_return.put("status", "1");
    		t_return.put("msg", "row count must be less than 2000 , your rows:"+rows);
    		return t_return;
        }
        Cell cell = null;//就是单个单元格
        String[] sqls = new String[rows-1];
        
        int id = 0;
        String remark = String.valueOf(Math.random()*1000);
		try {
			statement = conn.createStatement();	
			statement.executeUpdate("START TRANSACTION;");
			id = tools.getTableId("oa_plan");       
        
	        for(int i=1;i<rows;i++){
	        	id++;  
	        	
	        	sqls[i-1] = "insert into oa_plan( " +
				"code,name,content,files,evaluate,evaluate_remark,requirement,plan_time_start,plan_time_stop,plan_personhour,plan_money,plan_output,result_time_start,result_time_stop,result_personhour,result_money,result_output,count_work,count_plan,group_incharge,user_incharge,groups_participate,groups_weight,quotes,quotes_weight,type,status" +
				",id,creater_code,remark,creater_group_code) values ('" 
	        	+sheet.getCell(0,i).getContents()+"','"
	        	+sheet.getCell(1,i).getContents()+"','"
	        	+sheet.getCell(2,i).getContents()+"','"
	        	+sheet.getCell(3,i).getContents()+"','"
	        	+sheet.getCell(4,i).getContents()+"','"
	        	+sheet.getCell(5,i).getContents()+"','"
	        	+sheet.getCell(6,i).getContents()+"','"
	        	+sheet.getCell(7,i).getContents()+"','"
	        	+sheet.getCell(8,i).getContents()+"','"
	        	+sheet.getCell(9,i).getContents()+"','"
	        	+sheet.getCell(10,i).getContents()+"','"
	        	+sheet.getCell(11,i).getContents()+"','"
	        	+sheet.getCell(12,i).getContents()+"','"
	        	+sheet.getCell(13,i).getContents()+"','"
	        	+sheet.getCell(14,i).getContents()+"','"
	        	+sheet.getCell(15,i).getContents()+"','"
	        	+sheet.getCell(16,i).getContents()+"','"
	        	+sheet.getCell(17,i).getContents()+"','"
	        	+sheet.getCell(18,i).getContents()+"','"
	        	+sheet.getCell(19,i).getContents()+"','"
	        	+sheet.getCell(20,i).getContents()+"','"
	        	+sheet.getCell(21,i).getContents()+"','"
	        	+sheet.getCell(22,i).getContents()+"','"
	        	+sheet.getCell(23,i).getContents()+"','"
	        	+sheet.getCell(24,i).getContents()+"','"	
	        	+sheet.getCell(25,i).getContents()+"','"
	        	+sheet.getCell(26,i).getContents()+"','"	        	
	        	+id+"','"
				+executor+"','"
				+remark+"',(select group_code from basic_user where username = '"+executor+"'));";
	
				statement.executeUpdate(sqls[i-1]);
	        	System.out.println(i);
	        }
	                
			statement.executeUpdate( tools.getSQL("basic_memory__id_update").replace("__code__", "oa_plan") );
	        statement.executeUpdate("COMMIT;");
	
	        t_return.put("status","1");
	        t_return.put("msg","ok");
        
		} catch (SQLException e) {
	        t_return.put("status","2");
	        e.printStackTrace();
	        
	        String sql_callback = "delete from oa_plan where remark = '"+remark+"'";
	        try {
				statement.executeUpdate(sql_callback);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return t_return;
			
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }	
		
        try {
        	workBook.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return t_return;
	}
	
	public static Hashtable download(String search,String pagesize,String pagenum,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;

		String where = oa_plan.search(search,executor);	

		sql = tools.getSQL("oa_plan__grid");

		
		try {
			sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
			statement = conn.createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);
			WritableWorkbook book;
			int i = 0;
			String filename = String.valueOf(Math.random()*1000);

			String thepath = tools.getSQL("APPPATH")+"\\file\\download\\"+filename+".xls";
			System.out.println(thepath);
			book = Workbook.createWorkbook(new File(thepath));
			WritableSheet sheet = book.createSheet("data_oa_plan", 0);		
			
			sheet.addCell( new Label(0,0,"code"));
			sheet.addCell( new Label(1,0,"name"));
			sheet.addCell( new Label(2,0,"content"));
			sheet.addCell( new Label(3,0,"files"));
			sheet.addCell( new Label(4,0,"evaluate"));
			sheet.addCell( new Label(5,0,"evaluate_remark"));
			sheet.addCell( new Label(6,0,"requirement"));
			sheet.addCell( new Label(7,0,"plan_time_start"));
			sheet.addCell( new Label(8,0,"plan_time_stop"));
			sheet.addCell( new Label(9,0,"plan_personhour"));
			sheet.addCell( new Label(10,0,"plan_money"));
			sheet.addCell( new Label(11,0,"plan_output"));
			sheet.addCell( new Label(12,0,"result_time_start"));
			sheet.addCell( new Label(13,0,"result_time_stop"));
			sheet.addCell( new Label(14,0,"result_personhour"));
			sheet.addCell( new Label(15,0,"result_money"));
			sheet.addCell( new Label(16,0,"result_output"));
			sheet.addCell( new Label(17,0,"count_work"));
			sheet.addCell( new Label(18,0,"count_plan"));
			sheet.addCell( new Label(19,0,"group_incharge"));
			sheet.addCell( new Label(20,0,"user_incharge"));
			sheet.addCell( new Label(21,0,"groups_participate"));
			sheet.addCell( new Label(22,0,"groups_weight"));
			sheet.addCell( new Label(23,0,"quotes"));
			sheet.addCell( new Label(24,0,"quotes_weight"));
			sheet.addCell( new Label(25,0,"type"));
			sheet.addCell( new Label(26,0,"status"));
			sheet.addCell( new Label(27,0,"deviation"));
			sheet.addCell( new Label(28,0,"appraise"));

			while (rs.next()) {			
				i++;

				sheet.addCell(new Label(0,i,rs.getString("code")));
				sheet.addCell(new Label(1,i,rs.getString("name")));
				sheet.addCell(new Label(2,i,rs.getString("content")));
				sheet.addCell(new Label(3,i,rs.getString("files")));
				sheet.addCell(new Label(4,i,rs.getString("evaluate")));
				sheet.addCell(new Label(5,i,rs.getString("evaluate_remark")));
				sheet.addCell(new Label(6,i,rs.getString("requirement")));
				sheet.addCell(new Label(7,i,rs.getString("plan_time_start")));
				sheet.addCell(new Label(8,i,rs.getString("plan_time_stop")));
				sheet.addCell(new Label(9,i,rs.getString("plan_personhour")));
				sheet.addCell(new Label(10,i,rs.getString("plan_money")));
				sheet.addCell(new Label(11,i,rs.getString("plan_output")));
				sheet.addCell(new Label(12,i,rs.getString("result_time_start")));
				sheet.addCell(new Label(13,i,rs.getString("result_time_stop")));
				sheet.addCell(new Label(14,i,rs.getString("result_personhour")));
				sheet.addCell(new Label(15,i,rs.getString("result_money")));
				sheet.addCell(new Label(16,i,rs.getString("result_output")));
				sheet.addCell(new Label(17,i,rs.getString("count_work")));
				sheet.addCell(new Label(18,i,rs.getString("count_plan")));
				sheet.addCell(new Label(19,i,rs.getString("group_incharge")));
				sheet.addCell(new Label(20,i,rs.getString("user_incharge")));
				sheet.addCell(new Label(21,i,rs.getString("groups_participate")));
				sheet.addCell(new Label(22,i,rs.getString("groups_weight")));
				sheet.addCell(new Label(23,i,rs.getString("quotes")));
				sheet.addCell(new Label(24,i,rs.getString("quotes_weight")));
				sheet.addCell(new Label(25,i,rs.getString("type")));
				sheet.addCell(new Label(26,i,rs.getString("status")));
				sheet.addCell(new Label(27,i,rs.getString("deviation")));
				sheet.addCell(new Label(28,i,rs.getString("appraise")));

			}				
			book.write();		
			book.close();
			
	        t_return.put("status","1");
	        t_return.put("msg","ok");
	        t_return.put("file","../file/download/"+filename+".xls");
		} catch (IOException e) {
			e.printStackTrace();
	        t_return.put("status","2");
	        t_return.put("msg",e.toString());
		} catch (RowsExceededException e) {
			e.printStackTrace();
	        t_return.put("status","2");
	        t_return.put("msg",e.toString());
		} catch (WriteException e) {
			e.printStackTrace();
	        t_return.put("status","2");
	        t_return.put("msg",e.toString());
		} catch (SQLException e) {
			e.printStackTrace();
	        t_return.put("status","2");
	        t_return.put("msg",e.toString());
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}
	
	public static Hashtable usergrid(String search,String pagesize,String pagenum,String user_type){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		String where = " where  basic_user.id_person = oa_person.id		  ";
		sql = tools.getSQL("oa_plan__usergrid");

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("username")&& (!value.equals("")) ){
				where += " and username like '%"+value+"%'";
			}
			if(key.equals("name")){
				where += " and oa_person.name like '%"+value+"%'";
			}
			if(key.equals("group_code")){
				where += " and group_code = '"+value+"'";
			}
			if(key.equals("type")){
				where += " and type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and status = '"+value+"'";
			}
		}
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		try {
			statement = conn.createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);
			a = new ArrayList();
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
			t_return.put("Rows", a);	
			String sql_total = "select count(*) as count_  FROM basic_user right Join oa_person ON basic_user.id_person = oa_person.id	 "+where;

			rs = statement.executeQuery(sql_total);
			rs.next();
			t_return.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			e.printStackTrace();
	        t_return.put("status","2");
	        t_return.put("msg",e.toString());
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }

		return t_return;
	}
	
	public static Hashtable quotesgrid(String search,String pagesize,String pagenum,String user_type){
		Hashtable t2 = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		String where = " where 1=1  ";
		sql = tools.getSQL("oa_plan__quotesgrid");

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and oa_plan.name like '%"+value+"%'";
			}
		}
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		try {
			statement = conn.createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);
			a = new ArrayList();
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
			t2.put("Rows", a);	
			String sql_total = "select count(*) as count_  FROM oa_plan	 "+where;

			rs = statement.executeQuery(sql_total);
			rs.next();
			t2.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			e.printStackTrace();
			t2.put("status","2");
			t2.put("msg",e.toString());
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }

		return t2;
	}
	
	public static Hashtable groupgrid(String search,String pagesize,String pagenum,String user_type){
		Hashtable t2 = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;
		String sql = null;
		
		String where = " where type = '21'  ";
		sql = tools.getSQL("oa_plan__groupgrid");

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and basic_group.name like '%"+value+"%'";
			}
		}
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		try {
			statement = conn.createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);
			a = new ArrayList();
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
			t2.put("Rows", a);	
			String sql_total = "select count(*) as count_  FROM basic_group	 "+where;

			rs = statement.executeQuery(sql_total);
			rs.next();
			t2.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			e.printStackTrace();
			t2.put("status","2");
			t2.put("msg",e.toString());
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }

		return t2;
	}
	
	public static int index_commit = 0;
	public static int index = 0;
	public static void data4test(int page){
		
		String sql__depts = "select * from basic_group where type like '%-%-%' and type like '30%' limit 0,10000 ";
//		String sql__depts = "select * from basic_group where type like '%-%-%' and type like '30%' limit "+(page-1)*10000+",10000 ";
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection conn = tools.getConn();
		int flag = page*1000;
		try {
			statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement3 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement2 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			if(page==1)statement2.executeUpdate("delete from oa_plan where code like '"+tools.getConfigItem("ZONE")+"%'");
			
			statement2.executeUpdate("START TRANSACTION;");
			System.out.println(sql__depts);
			rs = statement.executeQuery(sql__depts);			
			Hashtable t_data = new Hashtable();
	        int id__oa_plan = tools.getTableId("oa_plan");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin = (format.parse("2010-01-01")).getTime();
			long end = (format.parse("2014-12-01")).getTime();	

			while (rs.next()) {			
				
				String code_group = rs.getString("code");
				
				//顶级单位,三年内总共有6个项目\合同\长期工作发展计划
				//每个项目持续时间为 半年到一年
				//顶级单位下属各个子部门,每个子部门分担项目的各个分支
				//各个部门承担的工作阶段(时间段)分为两段
				long time[]  = new long[4];
				time[0] = begin;
				time[3] = end;
//				long time[0] = (long) (begin + ((end-begin)/6)*(Math.random()*0.4+0.8));  
//				long time2 = (long) (time1 + ((end-begin)/6)*(Math.random()*0.4+0.8));  
//				long time3 = (long) (time2 + ((end-begin)/6)*(Math.random()*0.4+0.8));  
//				long time4 = (long) (time3 + ((end-begin)/6)*(Math.random()*0.4+0.8));  
//				long time5 = (long) (time4 + ((end-begin)/6)*(Math.random()*0.4+0.8));  
//				long time6 = (long) (time5 + ((end-begin)/6)*(Math.random()*0.4+0.8));  
				
				for(int i=1;i<3;i++){
					time[i] = (long) (time[i-1] + ((end-begin)/4)*(Math.random()*0.4+0.8));
					if(time[i]>end)break;
					id__oa_plan++;
//					System.out.println(id__oa_plan);
					String p_code = code_group+"--"+(flag+i);
					String sql_insert = "insert into oa_plan (code,name,content,plan_time_start,plan_time_stop,plan_personhour,plan_money,plan_output,group_incharge,user_incharge,"
							+ "id,creater_code,updater_code,creater_group_code,type,status) values ("
							+ "'"+p_code+"'"
							+ ",'整个公司(单位)级别的跨年工作安排"+(int)(Math.random()*1000000)+"'"
							+ ",'内容很长很长很长,会有 HTML标签,比如回车<br/>或者图片'"
							+ ",'"+format.format( time[i-1] )+"'"
							+ ",'"+format.format( time[i] )+"'"
							+ ",'0'"
							+ ",'0'"
							+ ",'0'"
							+ ",'"+code_group+"'"
							+ ",(select chief_code from basic_group where code = '"+code_group+"')"
							
							+ ",'"+id__oa_plan+"'"
							+ ",(select chief_code from basic_group where code = '"+code_group+"')"
							+ ",'0'"
							+ ",'"+code_group+"'"
							+ ",'"+(int)(Math.random()*4+1)+"0'"
							+ ",'"+(int)(Math.random()*4+1)+"0'"
							+ "); ";
					
					statement2.execute(sql_insert);

					//分摊到每个部门的工作 
					String sql_select_count = "select count(*) as count_ from basic_group where code like '"+code_group+"-%' and type = '40' ";
					rs2 = statement3.executeQuery(sql_select_count);
					rs2.next();
					int count__depts = rs2.getInt("count_");
					
					long time2[] = new long[count__depts];
					time2[0] = time[i-1];
					time2[count__depts-1] = time[i];
					long div = (time2[count__depts-1]-time2[0])/count__depts;
					for(int i2=1;i2<count__depts;i2++){
						time2[i2] = (long) (time2[i2-1] + div*(Math.random()*0.4+0.8));
						if(time2[i2]>time2[count__depts-1])time2[i2] = time2[count__depts-1];
						if(time2[i2-1]>=time2[count__depts-1])break;
						id__oa_plan++;
						String d_code = code_group+"-"+(10+i2-1);
//						p_code = d_code + "-p-"+(1000+count__depts*(i-1)+i2);
						String p_code_d = p_code + "-"+(flag+count__depts*(i-1)+i2);
						String sql_insert_d = "insert into oa_plan (code,name,content,plan_time_start,plan_time_stop,plan_personhour,plan_money,plan_output,group_incharge,user_incharge,"
								+ "id,creater_code,updater_code,creater_group_code,type,status) values ("
								+ "'"+p_code_d+"'"
								+ ",'部门级别的跨年工作安排"+(int)(Math.random()*1000000)+"'"
								+ ",'内容很长很长很长,会有 HTML标签,比如回车<br/>或者图片'"
								+ ",'"+format.format( time2[i2-1] )+"'"
								+ ",'"+format.format( time2[i2] )+"'"
								+ ",'0'"
								+ ",'0'"
								+ ",'0'"
								+ ",'"+d_code+"'"
								+ ",(select chief_code from basic_group where code = '"+d_code+"')"
								
								+ ",'"+id__oa_plan+"'"
								+ ",(select chief_code from basic_group where code = '"+d_code+"')"
								+ ",'0'"
								+ ",'"+d_code+"'"
								+ ",'"+(int)(Math.random()*4+1)+"0'"
								+ ",'"+(int)(Math.random()*4+1)+"0'"
								+ "); ";
//						System.out.println(sql_insert_d);
						statement2.execute(sql_insert_d);
						
						
						//部门内将工作量划分为各个部分,基层的工作内容将直接参照这个来
						int count_mini = (int)(Math.random()*5+5);
						long time3[] = new long[count_mini];
						time3[0] = time2[i2-1];
						time3[count_mini-1] = time2[i2];
						long div_mini = (time3[count_mini-1]-time3[0])/count_mini;
						for(int i3=1;i3<count_mini;i3++){
							time3[i3] = (long) (time3[i3-1] + div_mini*(Math.random()*0.4+0.8));
							if(time3[i3]>time3[count_mini-1])time3[i3] = time3[count_mini-1];
							if(time3[i3-1]>=time3[count_mini-1])break;
							id__oa_plan++;

//							p_code = d_code + "-p-"+(1000+count_mini*(i-1)+i3);
							String p_code_mini = p_code_d + "-"+(flag+count_mini*(i2-1)+i3);
							
							String sql_insert_mini = "insert into oa_plan (code,name,content,plan_time_start,plan_time_stop,plan_personhour,plan_money,plan_output,group_incharge,user_incharge,"
									+ "id,creater_code,updater_code,creater_group_code,type,status) values ("
									+ "'"+p_code_mini+"'"
									+ ",'被细化的元素工作安排"+(int)(Math.random()*1000000)+"'"
									+ ",'内容很长很长很长,会有 HTML标签,比如回车<br/>或者图片'"
									+ ",'"+format.format( time3[i3-1] )+"'"
									+ ",'"+format.format( time3[i3] )+"'"
									+ ",'0'"
									+ ",'0'"
									+ ",'0'"
									+ ",'"+d_code+"'"
									+ ",(select chief_code from basic_group where code = '"+d_code+"')"
									
									+ ",'"+id__oa_plan+"'"
									+ ",(select chief_code from basic_group where code = '"+d_code+"')"
									+ ",'0'"
									+ ",'"+d_code+"'"
									+ ",'"+(int)(Math.random()*4+1)+"0'"
									+ ",'"+(int)(Math.random()*4+1)+"0'"
									+ "); ";
							statement2.execute(sql_insert_mini);
						}						
					}
				}				
			}
			statement2.executeUpdate( tools.getSQL("basic_memory__id_update").replace("__code__", "oa_plan") );
	        statement2.executeUpdate("COMMIT;");			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}		
	}	

	public static void main(String args[]) throws SQLException{
		oa_plan.data4test(1);
		oa_plan.data4test(2);
//		oa_plan.data4test(3);
	}	
}
