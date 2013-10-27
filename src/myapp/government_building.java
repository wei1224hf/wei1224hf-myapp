package myapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

public class government_building {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			
			if (functionName.equals("grid")) {	
				if(basic_user.checkPermission(executor, "520101", session)){
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
			}else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120223", session)){
					t = remove(
							(String)request.getParameter("ids"),
							executor
							);
				}				
			}else if(functionName.equals("view")){
				if(basic_user.checkPermission(executor, "520102", session)){
					t = view((String) request.getParameter("id"));		
				}
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}else if(functionName.equals("lowerCodes")){
				out = g.toJson(lowerCodes((String)request.getParameter("code"), (String)request.getParameter("reference")));
				return out;
			}else if(functionName.equals("bound")){
				if(basic_user.checkPermission(executor, "520151", session)){
					t = bound((String) request.getParameter("data"),executor);		
				}
			}
			else if(functionName.equals("download")){
				if(basic_user.checkPermission(executor, "520112", session)){
					t = download(
							 (String) request.getParameter("search")
							,(String) request.getParameter("pagesize")
							,(String) request.getParameter("page")
							,executor
							);		
				}
			}
			else if (functionName.equals("statistics_time")) {		
				if(basic_user.checkPermission(executor, "520192", session)){
					out = g.toJson( statistics_time(
							(String) request.getParameter("search")
							,(String) request.getParameter("attribute")
							,executor
							));
					return out;
				}
			}
			else if (functionName.equals("statistics_attribute")) {		
				if(basic_user.checkPermission(executor, "520192", session)){
					out = g.toJson( statistics_attribute(
							(String) request.getParameter("search")
							,(String) request.getParameter("attribute")
							,executor
							));
					return out;
				}
			}	
			else if (functionName.equals("statistics_gis")) {			
				out = g.toJson( statistics_gis(
						 (String) request.getParameter("search")
						,(String) request.getParameter("attribute")
						,executor
						));
				return out;
			}	
			else if (functionName.equals("bind")) {			
				out = g.toJson( bind(
						 (String) request.getParameter("socialworker_id")
						,(String) request.getParameter("ids")
						,executor				
						));
				return out;
			}				
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} 	
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable loadConfig() {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		ArrayList a = null;
		
		try {
			stmt = conn.createStatement();
			String sql = "select left(code,2) as code,value from basic_parameter where code like  '__0000' and reference = 'zone' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_2", a);		
			
			String zone_4 = tools.getSQL("ZONE4");
			sql = "select code,name as value from basic_group where code like  '"+zone_4+"__' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_6", a);		
			
			String zone_8 = tools.getSQL("ZONE8");		
			sql = "select code,name as value from basic_group where code like  '"+zone_8+"__' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_10", a);				
			
			sql = "select code,value from basic_parameter where reference = 'government_building__owner_type' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("government_building__owner_type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'government_building__type' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("government_building__type", a);				
			
			sql = "select code,value from basic_parameter where reference = 'government_building__status' order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("government_building__status", a);		
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("msg", e.toString());		
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }		
		
		return t_return;
	}
	
	public static ArrayList lowerCodes(String code,String reference) {	
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		ArrayList a_return = new ArrayList();
		
		String sql = "select code,name as value from basic_group where code like '"+code+"__' and type = '11' ";
		
		System.out.println(sql);
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			ResultSetMetaData rsData = rs.getMetaData();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rs.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				a_return.add(t);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }

		return a_return;
	}		
	
	public static Hashtable add(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
			String zone_10 = (String) t_data.get("zone_10");
			t_data.remove("zone_10");
			
			String sql_select = "select max(code) as thecode from government_building where code like '"+zone_10+"-____';";
			
			rset = stmt.executeQuery(sql_select);
			String thecode = "";
			while(rset.next()){
				thecode = rset.getString("thecode");
				if(thecode==null){
					 thecode = zone_10+"-0001";
					 break;
				}
				thecode = thecode.substring(12, 16);
				int i = Integer.valueOf(thecode);
				i++;
				thecode = "";
				if(i<1000){
					thecode = "0"+thecode;
				}
				if(i<100){
					thecode = "0"+thecode;
				}
				if(i<10){
					thecode = "0"+thecode;
				}
				thecode = zone_10+"-"+thecode + i;
			}
			t_data.put("code", thecode);

			Enumeration e = t_data.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = (String)t_data.get(key);
				
				t_data.put(key, "'"+value+"'");
			}
			
			String id = String.valueOf(tools.getTableId("government_building"));
			t_data.put("id", id);
			t_data.put("creater_code", "'"+executor+"'");
			t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

			e = t_data.keys();
			String keys = "insert into government_building (";
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
			
			t_return.put("status", "1");
			t_return.put("msg", "ok");
			t_return.put("id", id);
			t_return.put("code", thecode);
		} catch (SQLException ex) {
			ex.printStackTrace();
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
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			String[] id = ids.split(",");
			String sql = "";
			for(int i=0;i<id.length;i++){
				//sql = "delete from gis_polygon where id = (select id_gis_polygon from government_building where id = '"+id[i]+"') ;";
				//stmt.executeUpdate(sql);
				sql = "delete from government_building where id = '"+id[i]+"' ;";
				stmt.executeUpdate(sql);
			}		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}	
	
	public static Hashtable bound(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			StringMap t_data = new Gson().fromJson(data, StringMap.class);
			String sql = "update government_building set id_gis_polygon = '"+t_data.get("id_gis_polygon")+"' where id = '"+t_data.get("id")+"';";
			stmt.executeUpdate(sql);

			sql = "update gis_polygon_16 set name = '"+t_data.get("name")+"' where id = '"+t_data.get("id_gis_polygon")+"';";
			stmt.executeUpdate(sql);
		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		
		String id = (String) t_data.get("id");
		t_data.remove("id");
		String sql = "";
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
			
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update government_building set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where id = '"+id+"' ";
		
		System.out.println(sql);		
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);				
			t_return.put("status", "1");
			t_return.put("msg", "ok");
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
	
	public static Hashtable view(String id) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getSQL("government_building__view").replace("__id__", "'"+id+"'");
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			rset.next();
			ResultSetMetaData m = rset.getMetaData();
			LinkedHashMap t_data = new LinkedHashMap();
			for(int i=1;i<=m.getColumnCount();i++){
				String key  = m.getColumnLabel(i);
				String value = "-";
				if(rset.getString(m.getColumnLabel(i)) != null){
					value = rset.getString(m.getColumnLabel(i));

				}
				t_data.put(key, value);
				
			}
			t_return.put("data",t_data);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
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
	
	private static String search(
			 String search
			,String user_type
			,String executor
			,String group_code
			){
		String where = " where 1=1 ";

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String[] str = key.split("__");
			Object value = search_t.get(key);
			
			if(str[1].equals("name")){
				where += " and "+str[0]+"."+str[1]+" like '%"+value+"%' ";
			}
			else if(str[1].contains("_small")){
				String thekey = str[1].replace("_small", "");
				if(thekey.equals("time_founded")){
					where += " and "+str[0]+"."+thekey+" > '"+value+"' ";
				}else{
					where += " and "+str[0]+"."+thekey+" > "+value+" ";
				}
			}
			else if(str[1].contains("_big")){
				String thekey = str[1].replace("_big", "");
				if(thekey.equals("time_founded")){
					where += " and "+str[0]+"."+thekey+" < '"+value+"' ";
				}else{
					where += " and "+str[0]+"."+thekey+" < "+value+" ";
				}
			}
			else if(str[1].equals("zone")){
				where += " and government_building.code like '"+value+"%'";
			}	
			
			else{
				where += " and "+str[0]+"."+str[1]+" = '"+value+"' ";
			}	
		}
		
		if(group_code.length()>2){//非系统用户
			String[] group_code_ = group_code.split("-");
			where += " and government_building.code like '"+group_code_[0]+"%'";
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
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		String sql = tools.getSQL("government_building__grid");
		String sql_orderby = " order by "+sortname+" "+sortorder;
		String where = government_building.search(search,user_type,executor,user_group);	
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
				t.put("color", "#666666");
				a.add(t);
			}
			t_return.put("Rows", a);
			
			String sql_total = "select count(*) as count_ from government_building " + where;
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
	
	public static Hashtable upload(String path,String executor){
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		String filePath = path;
		InputStream fs = null;
		Workbook workBook = null;
		     
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

        Sheet sheet = workBook.getSheet("data_government_building");
        int rows = sheet.getRows();
        System.out.println(sheet.getColumns()+" "+rows);

        if(rows>10000){
    		t_return.put("status", "1");
    		t_return.put("msg", "row count must be less than 10000 , your rows:"+rows);
    		return t_return;
        }
        
        String[] sqls = new String[rows-1];
        try {
        	stmt = conn.createStatement();
			stmt.executeUpdate("START TRANSACTION;");
			
			int id = tools.getTableId("government_building");
			Hashtable t__grup_type = basic_user.getSession(executor);
			String group_code = (String) t__grup_type.get("group_code");

	        for(int i=1;i<rows;i++){
	        	id++;        	
	        	sqls[i-1] = "insert into government_building( code,name,count_floor,population,owner,owner_type,owner_person_id,id_gis_polygon,time_founded,photo,socialworker,type,status"
	        			+ ",id,creater_code,creater_group_code) values ('" 
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
	        			+sheet.getCell(12,i).getContents()+"'"
	        			
	        			+",'"+id+"','"+executor+"','"+group_code+"');";
	        	stmt.executeUpdate(sqls[i-1]);
	        }
        
	        String Sql = tools.getSQL("basic_memory__id_update").replace("__code__", "government_building") ;			
	        stmt.executeUpdate(Sql );	        
	        stmt.executeUpdate("COMMIT;");
	        
	        t_return.put("status","1");
	        t_return.put("msg","ok");
		} catch (SQLException e1) {
			e1.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e1.toString());
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }               
        workBook.close();

		return t_return;
	}
	
	public static Hashtable download(
			 String search
			,String pagesize
			,String pagenum
			,String executor) {
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		String sql= tools.getSQL("government_building__grid");	
		String where = government_building.search(search,user_type,executor,group_code);
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		System.out.println(sql);
		
		String filename = String.valueOf(Math.random()*1000);
		String thepath = tools.getSQL("APPPATH")+"\\file\\download\\"+filename+".xls";
		
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			WritableWorkbook book;
			int i = 0;
			
			try {
				System.out.println(thepath);
				book = Workbook.createWorkbook(new File(thepath));
				WritableSheet sheet = book.createSheet("data_government_building", 0);		
				
				sheet.addCell( new Label(0,0,"code"));
				sheet.addCell( new Label(1,0,"name"));
				sheet.addCell( new Label(2,0,"count_floor"));
				sheet.addCell( new Label(3,0,"population"));
				sheet.addCell( new Label(4,0,"owner"));
				sheet.addCell( new Label(5,0,"owner_type"));
				sheet.addCell( new Label(6,0,"owner_person_id"));
				sheet.addCell( new Label(7,0,"id_gis_polygon"));
				sheet.addCell( new Label(8,0,"time_founded"));
				sheet.addCell( new Label(9,0,"photo"));
				sheet.addCell( new Label(10,0,"type"));
				sheet.addCell( new Label(11,0,"status"));
				
				while (rset.next()) {			
					i++;

					sheet.addCell(new Label(0,i,rset.getString("code")));
					sheet.addCell(new Label(1,i,rset.getString("name")));
					sheet.addCell(new Label(2,i,rset.getString("count_floor")));
					sheet.addCell(new Label(3,i,rset.getString("population")));
					sheet.addCell(new Label(4,i,rset.getString("owner")));
					sheet.addCell(new Label(5,i,rset.getString("owner_type")));
					sheet.addCell(new Label(6,i,rset.getString("owner_person_id")));
					sheet.addCell(new Label(7,i,rset.getString("id_gis_polygon")));
					sheet.addCell(new Label(8,i,rset.getString("time_founded")));
					sheet.addCell(new Label(9,i,rset.getString("photo")));
					sheet.addCell(new Label(10,i,rset.getString("type")));
					sheet.addCell(new Label(11,i,rset.getString("status")));					
				}				
				book.write();		
				book.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			
	        t_return.put("status","1");
	        t_return.put("msg","ok");
	        t_return.put("file","../file/download/"+filename+".xls");
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
	
	public static Hashtable statistics_time(			
			 String search
			,String attribute
			,String executor
			){
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		String time_length = "7";
		if(attribute.equals("day"))time_length = "10";
		if(attribute.equals("month"))time_length = "7";
		if(attribute.equals("year"))time_length = "4";
		
		String sql = "select left(government_building.time_founded,"+time_length+") as time_, count(*) as count_ from government_building ";		
		search = URLDecoder.decode(search);
		search = search.replace("\\\"", "\"");
		search = search.replace("\"{", "{");
		search = search.replace("}\"", "}");		
		String where = government_building.search(search,user_type,executor,group_code);
		sql += where;
		sql += " group by left(government_building.time_founded, "+time_length+")  ";
		
		ArrayList xAxis = new ArrayList();
		ArrayList series = new ArrayList();

		try {
			stmt = conn.createStatement();
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);

			ResultSetMetaData rsData = rs.getMetaData();
			int count = 0;
			while (rs.next() && count < 100) {
				count ++;
				xAxis.add(rs.getString("time_"));
				series.add(rs.getInt("count_"));
			}
			
			t_return.put("xAxis", xAxis);	
			t_return.put("series", series);	
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
	
	public static Hashtable statistics_attribute(
			String search
			,String attribute
			,String executor
			){

		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;				
		
		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		String sql = "select "+attribute+",(select extend4 from basic_memory where extend5 = 'government_building__"+attribute+"' and code = government_building."+attribute+") as name, count(*) as count_,sum(population) as sum_ from government_building ";
		search = URLDecoder.decode(search);
		search = search.replace("\\\"", "\"");
		search = search.replace("\"{", "{");
		search = search.replace("}\"", "}");
		Hashtable t_search = new Gson().fromJson(search, Hashtable.class);
		String where = government_building.search(search,user_type,executor,group_code);

		ArrayList theData = new ArrayList();

		try {
			stmt = conn.createStatement();			
			if(attribute.equals("zone")){
				String syszone = tools.getSQL("ZONE");
				int len = syszone.length();
				System.out.println((new Gson()).toJson(t_search));
				if(t_search.containsKey("government_building__zone")){
					String zonecode = (String) t_search.get("government_building__zone");
					System.out.println(zonecode);
					len = zonecode.length();
				}
				sql = "select left(code,"+(len+2)+") as code,(select extend4 from basic_memory where extend5 = 'group' and code = left(government_building.code,"+(len+2)+") ) as name,sum(population) as sum_,count(*) as count_ from government_building";
				sql += where;
				sql += " group by left(code,"+(len+2)+")";
				System.out.println(sql);
				rset = stmt.executeQuery(sql);
				ArrayList xAxis = new ArrayList();
				ArrayList series = new ArrayList();
				ArrayList series2 = new ArrayList();
				ResultSetMetaData rsData = rset.getMetaData();
				int count = 0;
				while (rset.next() && count < 100) {
					count ++;
					String thename = rset.getString("name");
					if(thename==null)thename=rset.getString("code");
					xAxis.add(thename);
					series.add(rset.getInt("count_"));
					series2.add(rset.getInt("sum_"));
				}
				t_return.put("xAxis", xAxis);	
				t_return.put("series", series);	
				t_return.put("series2", series2);	
			}else{
				sql += where;
				sql += " group by "+attribute;
				System.out.println(sql);
				rset = stmt.executeQuery(sql);
				while (rset.next()) {		
					ArrayList a_ = new ArrayList();
					a_.add(rset.getString("name"));
					a_.add(rset.getInt("count_"));
					theData.add(a_);
				}
				t_return.put("data", theData);
			}
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
	
	public static Hashtable statistics_gis(			
			 String search
			,String attribute
			,String executor
			){
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");

		search = URLDecoder.decode(search);
		search = search.replace("\\\"", "\"");
		search = search.replace("\"{", "{");
		search = search.replace("}\"", "}");
		search = search.replace("'{", "{");
		search = search.replace("}'", "}");
		
		Hashtable t_search = new Gson().fromJson(search, Hashtable.class);
		
		String syszone = tools.getSQL("ZONE");
		int len = syszone.length();
		System.out.println((new Gson()).toJson(t_search));
		if(t_search.containsKey("government_building__zone")){
			String zonecode = (String) t_search.get("government_building__zone");
			System.out.println(zonecode);
			len = zonecode.length();
		}
		String gistable = "gis_polygon_"+(len+6);		
		
		String where = government_building.search(search,user_type,executor,group_code);
		String sql = "select left(government_building.code,"+(len+2)+") as code,"
				+ "(select name from basic_group where code = left(government_building.code,"+(len+2)+") ) as name"
				+ ",(select astext(ogc_geom)  from "+gistable+" where "+gistable+".`code` = left(government_building.code,"+(len+2)+")) as wkt "
				+ ",sum(population) as sum_,count(*) as count_ from government_building";
		sql += where;
		sql += " group by left(code,"+(len+2)+") order by count(*)";
		ArrayList theData = new ArrayList();

		try {
			stmt = conn.createStatement();			
			
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			while (rset.next()) {		
				Hashtable t = new Hashtable();
				String wkt = rset.getString("wkt");
				wkt = wkt.replace("MULTIPOLYGON(((", "").replace(")))", "");
				wkt = wkt.replace("POLYGON((", "").replace("))", "");
				String[] wkt_ = wkt.split(",");
				String wkt__ = "";
				int length = wkt_.length;
				if(length>250){
					int flag = length/250;
					System.out.println(flag);
					for(int j=0;j<wkt_.length;j+=flag){
						wkt__ += wkt_[j]+",";
					}
					wkt__+= wkt_[wkt_.length-1];
				}else{
					wkt__ = wkt;
				}
				wkt = "POLYGON(("+wkt__+"))";
				t.put("wkt", wkt);
				t.put("count", rset.getString("count_"));
				t.put("code", rset.getString("code"));
				t.put("sum", rset.getString("sum_"));
				t.put("name", rset.getString("name"));
				theData.add(t);
			}
			t_return.put("data", theData);
			
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
	
	public static Hashtable bind(
			 String socialworker_id
			,String ids
			,String executor
			){
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;			

		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		try {
			stmt = conn.createStatement();
			String[] ids_ = ids.split(",");
			for(int i=0;i<ids_.length;i++){
				String sql = "update government_building set socialworker_id ="+socialworker_id+" where government_building.id="+ids_[i];
				System.out.println(sql);
				stmt.executeUpdate(sql);
			}
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
	
	public static ArrayList data4test(int page){
		ArrayList a_return = new ArrayList();
		String sql_zone_10 = "select code from basic_group where LENGTH(code) = 10 and code not like '%-%' limit "+(page-1)*3000+",3000 ";
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			stmt = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			stmt2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			stmt2.executeUpdate("START TRANSACTION;");
			System.out.println(sql_zone_10);
			ResultSet rs = stmt.executeQuery(sql_zone_10);			
			Hashtable t_data = new Hashtable();
	        int id = tools.getTableId("government_building");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin_ = (format.parse("1940-01-01")).getTime();
			long end = (format.parse("2011-12-01")).getTime();	
			System.out.println(begin_);
			while (rs.next()) {			
				
				String code = rs.getString("code");
				int count = 1000+(int)(Math.random()*150+50);
				
				for(int i=1000;i<count;i++){
					long begin = (long)(begin_ +  (end - begin_)*((i-1000+1+0.0)/(count-1000)));//家庭总数逐年递增

					String b_type = "10";//群体楼宇住房
					double d_rand = Math.random();
					if(d_rand>0.29)b_type = "11";//别墅
					if(d_rand>0.4)b_type = "12";//郊区农民房
					if(d_rand>0.5)b_type = "20";//商业群办公大楼
					if(d_rand>0.7)b_type = "21";//单商业办公大楼
					if(d_rand>0.8)b_type = "22";//沿街商铺
					if(d_rand>0.83)b_type = "30";//厂房
					if(d_rand>0.90)b_type = "40";//政府办公楼
					
					id++;
					String sql_insert_building = "insert into government_building (code,name,count_floor,population,owner,owner_type,owner_person_id,time_founded,photo,type,status,id,creater_code,creater_group_code) values ("
							+ "'"+ code+"-"+i +"',"
							+ "'"+ "楼宇"+ (int)(Math.random()*100000) +"',"
							+ "'"+ ""+(int)(Math.random()*10) +"',"
							+ "'"+ ""+(int)(Math.random()*1000) +"',"
							+ "'"+ tools.randomName() +"',"
							+ "'"+ ((Math.random()>0.8)?"20":"10") +"',"
							+ "'"+ i+"" +"',"
							+ "'"+ format.format( new Date( begin + (long)(Math.random() * (end - begin)) )) +"',"
							+ "'"+ "../file/upload/photo/buildings/"+((int)(Math.random()*17)+1)+".jpg" +"',"
							+"'"+b_type+"',"
							+"'"+((Math.random()>0.9)?"20":"10")+"',"
							+ "'"+ id+"" +"','admin','10'"
							+ ")";
//					a_return.add(sql_insert_building);
					stmt2.executeUpdate(sql_insert_building);
				}	
				
			}
	        stmt2.executeUpdate( tools.getSQL("basic_memory__id_update").replace("__code__", "government_building") );
	        stmt2.executeUpdate("COMMIT;");			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return a_return;
	}
	
	public static void main(String args[]) throws SQLException{
		government_building.data4test(1);
//		government_building.data4test(2);
//		Hashtable t = government_building.download("{}", "10", "1", "10");/
//		basic_group.upload(tools.getSQL("APPPATH")+"/file/developer/data_buildings.xls", "admin");
//		government_building.upload(tools.getSQL("APPPATH")+"/file/developer/data_buildings.xls", "admin");
	}	
}
