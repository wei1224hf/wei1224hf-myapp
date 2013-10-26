package myapp;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.google.gson.Gson;

public class basic_user {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			Hashtable t__group_type = new Hashtable();
			if(!functionName.equals("login")){
				t__group_type = getSession(executor);	
			}
				
			String group_code = (String) t__group_type.get("group_code");
			String user_type = (String) t__group_type.get("user_type");
			
			if (functionName.equals("grid")) {			
				if(basic_user.checkPermission(executor, "120201", session)){
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
			}else if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "120221", session)){
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
			}else if(functionName.equals("modify_myself")){
				if(basic_user.checkPermission(executor, "1123", session)){
					t = modify_myself(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120223", session)){
					t = remove(
							(String)request.getParameter("usernames"),
							executor
							);
				}				
			}else if(functionName.equals("view")){
				if(basic_user.checkPermission(executor, "120202", session)){
					t = view((String) request.getParameter("id"));		
				}
			}else if(functionName.equals("login")){
				t = login(
						(String)request.getParameter("username"),
						(String)request.getParameter("password"),
						request.getRemoteAddr(),
						request.getHeader("user-agent")
						);				
			}else if(functionName.equals("login_mobile")){
				t = login_mobile(
						(String)request.getParameter("username"),
						(String)request.getParameter("password"),
						request.getRemoteAddr(),
						request.getHeader("user-agent"),
						(String)request.getParameter("gis_lat"),
						(String)request.getParameter("gis_lot")
						);				
			}else if(functionName.equals("logout")){
				t = logout(
						(String) request.getParameter("username"),
						(String)request.getParameter("session")
						);				
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}else if(functionName.equals("updateSession")){
				t.put("session", updateSession(executor,session) );				
			}else if(functionName.equals("checkUsernameUsed")){
				//TODO
				t.put("status", "1");
				t.put("msg", "ok");			
			}else if(functionName.equals("group_get")){
				if(basic_user.checkPermission(executor, "120241", session)){
					t = group_get((String) request.getParameter("username"));
				}				
			}else if(functionName.equals("group_set")){
				if(basic_user.checkPermission(executor, "120241", session)){
					t = group_set(
							(String) request.getParameter("username")
							,(String) request.getParameter("group_codes")
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
	
	public static Hashtable loadConfig(){
		Hashtable t_return = new Hashtable();
		
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			String sql = "select code,value from basic_parameter where reference = 'basic_user__type' order by code";
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("basic_user__type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'basic_user__status' order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("basic_user__status", a);		
			
			sql = "select code,name from basic_group order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				String name = rs.getString("name");
				int len = rs.getString("code").length();
				for(int i=0;i<len;i+=2){
					name = "-"+name;
				}
				t.put("code", rs.getString("code"));
				t.put("value",name);			
				a.add(t);
			}
			t_return.put("group", a);		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}
	
	public static Hashtable add(String data,String executor){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
			if(checkUsernameUsed((String) t_data.get("username"))){
				t_return.put("status", "2");
				t_return.put("msg", "Username Used");
				return t_return;
			}		
			Enumeration e = t_data.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = (String)t_data.get(key);
				t_data.put(key, "'"+value+"'");
			}
			
			String id = String.valueOf(tools.getTableId("basic_user"));
			t_data.put("id", id);
			t_data.put("creater_code", "'"+executor+"'");
			t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

			e = t_data.keys();
			String keys = "insert into basic_user (";
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
			stmt.executeUpdate(sql);
			
			String user_code = (String) t_data.get("username");
			String group_code = (String) t_data.get("group_code");
			
			sql = "insert into basic_group_2_user (user_code,group_code) values ("+user_code+","+group_code+");";
			System.out.println(sql);		
			stmt.executeUpdate(sql);
			
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e1) {
			e1.printStackTrace();
			
			t_return.put("status", "2");
			t_return.put("msg", e1.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }	
		
		return t_return;
	}	
	
	public static Hashtable add_register(String data) {
		Hashtable t_return = new Hashtable();
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try{
			stmt = conn.createStatement();
			String keys = "username,password";
			
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
			if(checkUsernameUsed((String) t_data.get("username"))){
				t_return.put("status", "2");
				t_return.put("msg", "Username Used");
				return t_return;
			}		
			Enumeration e = t_data.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if(!keys.contains(key)){
					t_return.put("status", "2");
					t_return.put("msg", "wrong data " +key);
					return t_return;
				}
				String value = (String)t_data.get(key);
				t_data.put(key, "'"+value+"'");
			}
			
			String id = String.valueOf(tools.getTableId("basic_user"));
			t_data.put("id", id);
	
			e = t_data.keys();
			String sql = "insert into basic_user (";
			String sql_ = ") values (";		
			while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
				sql += key+",";
				sql_ += (String)t_data.get(key)+",";
			}
			sql = sql.substring(0,sql.length()-1);
			sql_ = sql_.substring(0,sql_.length()-1);
			
			sql += sql_ + ");";
			System.out.println(sql);		
			stmt.executeUpdate(sql);
			
			String user_code = (String) t_data.get("username");		
			sql = "insert into basic_group_2_user (user_code,group_code) values ('"+user_code+"','98');";
			System.out.println(sql);		
			stmt.executeUpdate(sql);
			
			t_return.put("status", "1");
			t_return.put("msg", "done");
		} catch (SQLException e){
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}	
	
	public static Hashtable remove(String usernames,String executor) {
		Hashtable t_return = new Hashtable();
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		String[] username = usernames.split(",");
		String sql = "";
		try{
			stmt = conn.createStatement();
			for(int i=0;i<username.length;i++){
				sql = "delete from basic_user where username = '"+username[i]+"' ;";
				stmt.executeUpdate(sql);
				sql = "delete from basic_group_2_user where user_code = '"+username[i]+"' ;";
				stmt.executeUpdate(sql);
			}	
			
			t_return.put("status", "1");
			t_return.put("msg", "ok");			
		} catch (SQLException e){
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}	
	
	/**
	 * 修改一个用户的信息
	 * 管理员操作
	 * */
	public static Hashtable modify(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		String username = (String) t_data.get("username");
		t_data.remove("username");
		String sql = "";
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
			
			if(key.equals("group_code")){				
				try {
					stmt = conn.createStatement();
					sql = "delete from basic_group_2_user where user_code = '"+username+"' ;";
					stmt.executeUpdate(sql);	
					sql = "insert into basic_group_2_user (user_code,group_code) values ('"+username+"','"+value+"');";
					stmt.executeUpdate(sql);	
				} catch (SQLException e1) {
					e1.printStackTrace();
				} finally {
		            try { if (rset != null) rset.close(); } catch(Exception ex) { }
		            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
		            try { if (conn != null) conn.close(); } catch(Exception ex) { }
		        }
			}
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update basic_user set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where username = '"+username+"' ";
		
		System.out.println(sql);		

		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);	
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		return t_return;
	}		
	
	/**
	 * 用户修改自己的信息
	 * 密码,邮箱,照片
	 * */
	public static Hashtable modify_myself(String data,String executor) {
		Hashtable t_return = new Hashtable();
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;		
		
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		String keys = "password,email,photo";
		
		String sql = "";
		Enumeration e = t_data.keys();
		String password_old = (String) t_data.get("password_old");
		t_data.remove("password_old");
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update basic_user set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where username = '"+executor+"' and password = '"+password_old+"' ";
		
		System.out.println(sql);		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
			
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		return t_return;
	}	
	
	public static Hashtable view(String id) {
		Hashtable t_return = new Hashtable();
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getSQL("basic_user__view").replace("__id__", "'"+id+"'");
			rset = stmt.executeQuery(sql);
			rset.next();
			ResultSetMetaData m = rset.getMetaData();
			LinkedHashMap t_data = new LinkedHashMap();
			for(int i=1;i<=m.getColumnCount();i++){
				String key  = m.getColumnLabel(i);
				String value = "";
				if(rset.getString(m.getColumnLabel(i)) != null){
					value = rset.getString(m.getColumnLabel(i));
				}else{
					value = "";
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
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}	
	
	public static String search(
			 String search
			,String user_type
			,String executor
			,String group_code
			){
		String where = " where 1=1 ";

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("username")){
				where += " and username like '%"+value+"%'";
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
		
		if(group_code.length()>2){//非系统用户
			String[] group_code_ = group_code.split("-");
			where += " and government_company.code like '"+group_code_[0]+"%'";
		}
		
		return where;
	}	
	
	public static Hashtable grid(
			 String search
			,String pagesize
			,String pagenum
			,String executor
			,String sortname
			,String sortorder) {
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");		
		
		Hashtable t_return = new Hashtable();
		
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;		
		
		String sql = "";
		String where = search(search,user_type,executor,user_group);	
		sql = tools.getSQL("basic_user__grid");
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		t_return.put("sql", sql);
		try {
			conn = tools.getConn();
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
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
			t_return.put("Rows", a);	
			String sql_total = "select count(*) as count_ from basic_user "+where;

			rs = stmt.executeQuery(sql_total);
			rs.next();
			t_return.put("Total", rs.getString("count_"));
			
			rs.close();
			stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}			
	
	public static Hashtable login(
			 String username
			,String md5PasswordTime
			,String ip
			,String client) {
		
		Hashtable t_return = login_mobile(username,md5PasswordTime,ip,client,"0","0");
		return t_return;
	}

	public static Hashtable login_mobile(
			 String username
			,String md5PasswordTime
			,String ip
			,String client
			,String gis_lat
			,String gis_lot){
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		if(conn==null){
			t_return.put("status", "2");
			t_return.put("msg", "Can not connect the Database");
			return t_return;
		}
		
		try {
			stmt = conn.createStatement();
			String sql = "";
			
			//判断系统内存表状态,如果为空,则说明需要初始化内存数据
			sql = "select count(*) as total from basic_memory ";
			rset = stmt.executeQuery(sql);
			rset.next();
			if(rset.getString("total").equals("0")){
				tools.initMemory();
			}
			rset.close();
			
			if(username.equals("guest")){
				md5PasswordTime = "md5(concat(password, hour(now()) ))";
			}else{
				md5PasswordTime = "'"+md5PasswordTime+"'";
			}
			sql = tools.getSQL("basic_user__login_check").replace("__username__", "'"+username+"'").replace("__password__", md5PasswordTime);
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			if(!rset.next()){
				t_return.put("status", "2");
				t_return.put("msg", "username or password wrong");
			}else{
				t_return.put("msg", "ok");
				t_return.put("status", "1");
				if( (rset.getString("session")!=null) && (!username.equals("guest")) ){
					if(!( rset.getString("ip").equals(ip) && rset.getString("client").equals(client) )){
						t_return.put("msg", ((HashMap)tools.il8n.get("basic_user")).get("kickOff"));
						t_return.put("status", "3");
					}
				}
				Hashtable t_data = new Hashtable();
				String session = tools.MD5( String.valueOf(Math.random()*1000) );
				final TimeZone zone = TimeZone.getTimeZone("GMT+8"); //获取中国时区 
				TimeZone.setDefault(zone); 			

				
				ResultSetMetaData rsData = rset.getMetaData();
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t_data.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t_data.put(rsData.getColumnLabel(i), "-");
					}
				}		
				
				
				t_return.put("permissions", basic_user.getPermissionTree(username));
				t_return.put("il8n", tools.readIl8n());
				t_return.put("zone", tools.getConfigItem("ZONE"));
				
				sql = tools.getSQL("basic_user__login_logout").replace("__user_code__", "'"+username+"'");
				stmt.executeUpdate(sql);
				sql = tools.getSQL("basic_user__login_session");
				sql = sql.replace("__username__", "'"+username+"'" );
				sql = sql.replace("__permissions__", "'"+basic_user.getPermission(username)+"'" );
				sql = sql.replace("__session__", "'"+session+"'" );
				sql = sql.replace("__ip__", "'"+ip+"'" );
				sql = sql.replace("__client__", "'"+client+"'" );
				sql = sql.replace("__gis_lat__", "'"+gis_lat+"'" );
				sql = sql.replace("__gis_lot__", "'"+gis_lot+"'" );
				sql = "insert into basic_user_session (user_id,user_code,group_code,groups,user_type,permissions,session,ip,client,gis_lat,gis_lot) values ((select id from basic_user where username = '"+username+"'),'"+username+"',(select group_code from basic_user where username = '"+username+"'),(select group_all from basic_user where username = '"+username+"'),(select type from basic_user where username = '"+username+"'),'"+basic_user.getPermission(username)+"','"+session+"','"+ip+"','"+client+"','"+gis_lat+"','"+gis_lot+"');";
				
				stmt.executeUpdate(sql);
				
				String session_return = tools.MD5(session+ (new Date()).getHours());
				t_data.put("session", session_return);

				t_return.put("logindata", t_data);

				System.out.println(session_return+" "+(new Date()).getHours());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		
		return t_return;
	}
	
	public static Hashtable logout(String username,String session) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getSQL("basic_user__logout").replace("__user_code__", "'"+username+"'").replace("__session__", "'"+session+"'");
			stmt.executeUpdate(sql);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}
	
	public static String updateSession(String user_code,String session) {
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		String r_session = tools.MD5( String.valueOf( Math.random()*1000 ) );
		String sql = tools.getSQL("basic_user__session_update")
				.replace("__user_code__", "'"+user_code+"'")
				.replace("__r_session__", "'"+r_session+"'")
				.replace("__session__", "'"+session+"'");
		System.out.println(sql);

		try {
			stmt = conn.createStatement();
			if(stmt.executeUpdate(sql)==0){
				return "";
			}
			r_session = tools.MD5(r_session+ (new Date()).getHours() );
		} catch (SQLException e) {
			e.printStackTrace();
			r_session = e.toString();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return r_session;		
	}
	
	public static String getPermission(String username) {
		String s_return = "";
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;		
		String sql = tools.getSQL("basic_user__getPermission").replace("__username__", "'"+username+"'");
		
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			while(rset.next()){
				s_return += rset.getString("code")+",";
			}
			s_return = s_return.substring(0, s_return.length()-1);
		} catch (SQLException e) {
			e.printStackTrace();
			s_return = e.toString();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return s_return;
	}
	
	public static ArrayList getPermissionTree(String username ) {
		ArrayList a_return = new ArrayList();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		String sql = tools.getSQL("basic_user__getPermission");
		sql = sql.replace("__username__", "'"+username+"'");

		try{
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			ResultSetMetaData rsData = rset.getMetaData();		
			
			while (rset.next()) {			
				Hashtable t = new Hashtable();
				for (int i = 1; i <= rsData.getColumnCount(); i++) {
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}	
				
				a_return.add(t);
			}
			a_return = tools.list2Tree(a_return);	
		} catch (SQLException e) {
			e.printStackTrace();
			a_return.add(e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return a_return;
	}
	
	public static Hashtable group_set(String username,String group_codes) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			

		try {
			stmt = conn.createStatement();
			String[] codes = group_codes.split(",");
			String sql = "delete from basic_group_2_user where user_code = '"+username+"' ";
			stmt.executeUpdate(sql);
			
			for(int i=0;i<codes.length;i++){
				sql = "insert into basic_group_2_user (user_code,group_code) values ( '"+username+"','"+codes[i]+"'); ";
				stmt.executeUpdate(sql);
			}	
			t_return.put("status", "1");
			t_return.put("msg", "ok");
			
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }	

		return t_return;
	}
	
	public static Hashtable group_get(String username) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;		
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getSQL("basic_user__group_get").replace("__username__", "'"+username+"'");
			rset = stmt.executeQuery(sql);	
			
			ArrayList array = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();
				t.put("name", rset.getString("name"));	
				t.put("code", rset.getString("code").replace("-", ""));
				t.put("code_", rset.getString("code"));
				
				if(rset.getString("user_code") != null){
					t.put("ischecked", 1);
				}
				
				array.add(t);
			}
			array = tools.list2Tree(array);	
			
			t_return.put("groups", array);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }	

		return t_return;
	}	
	
	/**
	 * 会被非常频繁的调用的查询
	 * 用于操作内存表
	 * */
	public static boolean checkPermission(String user_code,String actioncode,String session) throws SQLException {
		if(user_code.equals("guest")){
			session = "md5( concat( session, hour(now()) ) )";
		}else{
			session = "'"+session+"'";
		}
		String sql = tools.getSQL("basic_user__checkPermission")
				.replace("__user_code__", "'"+user_code+"'")
				.replace("__session__", session)
				.replace("__actioncode__", actioncode);
		System.out.println(sql);
		ResultSet rs = tools.getGlobalConn().createStatement().executeQuery(sql);
		if(rs.next()){

			sql = "update basic_user_session set lastaction = '"+actioncode+"' , lastactiontime = now() , count_actions = count_actions + 1 where user_code = '"+user_code+"' ;";
			tools.getGlobalConn().createStatement().executeUpdate(sql);
			rs.close();
			return true;
		}else{
			System.out.println("permission wrong"+" "+user_code+" "+actioncode+" "+session);
			rs.close();
			return false;	
		}	
	}
	
	/**
	 * 不需要进行 SESSION 匹配的权限验证
	 * 比如 业务逻辑权限 验证
	 * */
	public static Boolean checkPermission(String user_code,String actioncode) throws SQLException  {
		String sql = tools.getSQL("basic_user__checkPermission")
				.replace("__user_code__", "'"+user_code+"'")
				.replace("__session__", "session")
				.replace("__actioncode__", actioncode);

		ResultSet rs = tools.getGlobalConn().createStatement().executeQuery(sql);
		if(rs.next()){
			return true;
		}
		rs.close();
		return false;		
	}	
	
	public static Boolean checkUsernameUsed(String username){
		String sql = "select id from basic_user where username = '"+username+"' ";
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		try {
			conn = tools.getConn();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }	

		return false;
	}
	
	public static Hashtable getSession(String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getGlobalConn();
		Statement stmt = null;
		ResultSet rset = null;	
		try {
			stmt = tools.getConn().createStatement();
			String sql = "select user_type,group_code,groups from basic_user_session where user_code = '"+executor+"' ;";
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			rset.next();		
			t_return.put("user_type", rset.getString("user_type"));
			t_return.put("group_code", rset.getString("group_code"));
			String extgroups = "";
			if(rset.getString("groups")!=null)extgroups=rset.getString("groups");
			t_return.put("groups", extgroups);

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
//            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return t_return;
	}
	
	public static int data4test_commit = 0;
	//每个部门1个负责人,8个左右的员工
	public static void data4test(){
		Connection conn = tools.getConn();
		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rset = null;
		
		try {
			String zone = tools.getSQL("ZONE");
			stmt = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			stmt2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			stmt2.executeUpdate("DELETE FROM basic_user where username like '"+zone+"%';");
			stmt2.executeUpdate("DELETE FROM basic_group_2_user where user_code like '"+zone+"%';");
			stmt2.executeUpdate("DELETE FROM oa_person where remark = '"+zone+"';");
			stmt2.executeUpdate("START TRANSACTION;");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin = (format.parse("1940-01-01")).getTime();
			long end = (format.parse("2011-12-01")).getTime();
			
	        int id__oa_person = tools.getTableId("oa_person");
	        int id__basic_user = tools.getTableId("basic_user");
			
			String sql_select = "select * from basic_group where type like '30%' or type like '40%' ";
			rset = stmt.executeQuery(sql_select);
			while(rset.next()){
				
				String group_code = rset.getString("code");
				String group_code_[] = group_code.split("-");
				
				int len = group_code_[0].length();

				int count_person = (int)(Math.random()*4+4);
				for(int i=0;i<count_person;i++){	
					String username = group_code+"-"+i;
					String group_all = group_code;
					String type = "";
					if(len==10)type="1";
					if(len==8)type="2";
					if(len==6)type="3";
					if(group_code_.length==2){
						type = type+"3";
						i = count_person;
						stmt2.executeUpdate("update basic_group set chief_code = '"+username+"' where code = '"+group_code+"'");
						group_all = group_code+",X1";
						String sql__basic_group_2_user = "insert into basic_group_2_user(user_code,group_code) values ('"+username+"','X1');";
						stmt2.executeUpdate(sql__basic_group_2_user);
					}else{
						type = (i==0?type+"2":type+"1");
					}
					if(i==0){
						stmt2.executeUpdate("update basic_group set chief_code = '"+username+"' where code = '"+group_code+"'");
						group_all = group_code+",X1";
						String sql__basic_group_2_user = "insert into basic_group_2_user(user_code,group_code) values ('"+username+"','X1');";
						stmt2.executeUpdate(sql__basic_group_2_user);
					}
					
					id__oa_person ++;
					id__basic_user ++;
					
					String sql__oa_person = "insert into oa_person (name,birthday,photo,gender,nation,marriage,degree,politically,address_birth_code,id,creater_code,creater_group_code,remark) values ("
							+ " '"+tools.randomName()+"'"
							+ ",'"+format.format( new Date( begin + (long)(Math.random() * (end - begin)) ))+"'"
							+ ",'../file/upload/photo/"+(int)(Math.random()*2+1)+"/"+(int)(Math.random()*30+1)+".jpg'"
							+ ",'"+(int)(Math.random()*2+1)+"'"
							+ ",'"+(Math.random()>0.5?1:(int)(Math.random()*56+1))+"'"
							+ ",'"+(Math.random()>0.8?1:2)+"'"
							+ ",'"+(int)(Math.random()*9+1)+"0'"
							+ ",'"+(int)(Math.random()*13+1)+"'"
							+ ",'00'"
							+ ",'"+id__oa_person+"'"
							+ ",'admin'"
							+ ",'10'"
							+ ",'"+zone+"'"
							+ ");";
					//System.out.println(sql__oa_person);
					stmt2.executeUpdate(sql__oa_person);
					
					String sql__basic_user = "insert into basic_user(username,password,id,group_code,group_all,type,status,id_person) values ('"+username+"',md5('"+username+"'),'"+id__basic_user+"','"+group_code+"','"+group_all+"','"+type+"','10','"+id__oa_person+"');";
					stmt2.executeUpdate(sql__basic_user);
					String sql__basic_group_2_user = "insert into basic_group_2_user(user_code,group_code) values ('"+username+"','"+group_code+"');";
					stmt2.executeUpdate(sql__basic_group_2_user);
					
					basic_user.data4test_commit++;
					if(basic_user.data4test_commit>=15000){
						basic_user.data4test_commit = 0;
						stmt2.executeUpdate("COMMIT;");
						stmt2.executeUpdate("START TRANSACTION;");
					}
				}

			}
			stmt2.executeUpdate("COMMIT;");
			stmt2.executeUpdate( tools.getSQL("basic_memory__id_update").replace("__code__", "basic_user") );
			stmt2.executeUpdate( tools.getSQL("basic_memory__id_update").replace("__code__", "oa_person") );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (stmt2 != null) stmt2.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
	}
	
	public static void main(String args[]) {
		//select  md5( concat( md5( 'admin' ),'15' ) )		
//		System.out.println(new Gson().toJson(basic_user.login("guest", "2c3d9d5b839bbc144cb43313d7ec3dc5", "1", "1")));
//		System.out.println(new Gson().toJson(basic_user.grid("{}", "10", "1", "10")));
//		System.out.println(new Gson().toJson(basic_user.logout("admin", "3dddc2a1cc04fe2a1e9d974de790ae3b")));
//		System.out.println(new Gson().toJson(basic_user.getPermissionTree("admin")));
//		System.out.println(new Gson().toJson(basic_user.getGroup("admin")));
//		System.out.println( json.toJson(basic_user.login("admin", tools.MD5( tools.MD5("admin")+"17" ), "1.1.1.1", "x")) );
		//System.out.println( json.toJson(obj.logout("admin",  tools.MD5("jseo6usfqh6df0plvopiflr20ryhlhmo11") )) );
		//System.out.println( json.toJson(obj.grid("{}",12,0,1) )) ;
//		System.out.println( new Gson().toJson(basic_user.loadConfig()) );		
		//obj.importExcel("D:/workSpace/webs/ligerJAVA/WebContent/file/download/highschool/basic_user.xls");
//		System.out.println(basic_user.checkPermission("admin", "121111","9dc0e41ddb39e97ed37fed6b56cade5f"));
//		System.out.println(obj.checkPermission("admin", "11"));
//		System.out.println(basic_user.updateSession("admin", "8335c8a44e325d04b1d83046c6537969"));	
//		System.out.println(basic_user.view("1"));	
		
//		basic_group.upload( tools.getSQL("APPPATH")+"/file/developer/tables_community_data__jxgz.xls", "admin");
//		basic_user.login("admin", tools.MD5(tools.MD5("admin")+(new Date()).getHours()), "1", "1");
//		oa_plan.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		community_socialworker.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		government_building.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		government_family.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		government_resident.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		government_company.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");

//		community_visit.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		community_affair.upload(tools.getSQL("APPPATH")+"/file/developer/tables_community_data.xls", "admin");
//		tools.initMemory();
		basic_user.data4test();
	}	
}
