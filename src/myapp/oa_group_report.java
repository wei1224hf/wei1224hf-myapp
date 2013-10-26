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

public class oa_group_report {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			
			if (functionName.equals("grid")) {			
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
				t = view((String) request.getParameter("id"));		
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}else if(functionName.equals("lowerCodes")){
				out = g.toJson(lowerCodes((String)request.getParameter("code"), (String)request.getParameter("reference")));
				return out;
			}
			else if(functionName.equals("download")){
				t = download(
						 (String) request.getParameter("search")
						,(String) request.getParameter("pagesize")
						,(String) request.getParameter("page")
						,executor
						);		
			}
			else if (functionName.equals("statistics_time")) {			
				out = g.toJson( statistics_time(
						(String) request.getParameter("search")
						,(String) request.getParameter("attribute")
						,executor
						));
				return out;
			}
			else if (functionName.equals("statistics_attribute")) {			
				out = g.toJson( statistics_attribute(
						(String) request.getParameter("search")
						,(String) request.getParameter("attribute")
						,executor
						));
				return out;
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
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable loadConfig(){
		Hashtable t_return = new Hashtable();
		String sql = "";
		ResultSet rs = null;
		ArrayList a = null;
		Statement statement;
		try {
			statement = tools.getConn().createStatement();
			sql = "select code,value from basic_parameter where reference = 'government_zone_report__type' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_zone_report__type", a);				
			
			sql = "select code,value from basic_parameter where reference = 'government_zone_report__status' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_zone_report__status", a);		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return t_return;
	}
	
	public static ArrayList lowerCodes(String code,String reference) throws SQLException{		
		String sql = "select code,name as value from basic_group where code like '"+code+"__' and type = '11' ";
		
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
	
	public static Hashtable add(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Statement statement = null;
		try {
			statement = tools.getConn().createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
		t_data.put("id", tools.getTableId("government_zone_report"));
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String[] group_ = user_group.split("-");
		String type = (String) t_data.get("type");
		String date = (String) t_data.get("date");
		if(type.equals("10")){
			t_data.put("code", date.substring(0,4));
		}else{
			t_data.put("code", date.substring(0,7));
		}

		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}
		t_data.put("creater_code", "'"+executor+"'");
		t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");		

		e = t_data.keys();
		String keys = "insert into government_zone_report (";
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
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		
		return t_return;
	}	
	
	public static Hashtable remove(String ids,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		String[] id = ids.split(",");
		String sql = "";
		for(int i=0;i<id.length;i++){
			//sql = "delete from gis_polygon where id = (select id_gis_polygon from government_zone where id = '"+id[i]+"') ;";
			//statement.executeUpdate(sql);
			sql = "delete from government_zone_report where id = '"+id[i]+"' ;";
			statement.executeUpdate(sql);
		}		
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
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
		sql = "update government_zone_report set ";
	
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
	
	public static Hashtable view(String id) throws SQLException {
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		String sql = tools.getSQL("government_zone_report__view")+" where id = '"+id+"'";
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
				where += " and government_zone.code like '"+value+"%'";
			}	
			
			else{
				where += " and "+str[0]+"."+str[1]+" = '"+value+"' ";
			}	
		}
		
		if(group_code.length()>2){//非系统用户
			String[] group_code_ = group_code.split("-");
			where += " and government_zone_report.code_zone = '"+group_code_[0]+"'";
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
		
		Hashtable t2 = new Hashtable();
		
		String sql = tools.getSQL("government_zone_report__grid");
		String orderby = " order by "+sortname+" "+sortorder;
		String where = oa_group_report.search(search,user_type,executor,user_group);	
		String page = " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";
		sql = sql+" "+where + " "+orderby+" "+page;
		
		Statement statement = null;
		ResultSet rs;
		ArrayList a = new ArrayList();
		try {
			statement = tools.getConn().createStatement();
			System.out.println(sql);
			rs = statement.executeQuery(sql);
			ResultSetMetaData rsData = rs.getMetaData();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rs.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), " ");
					}
				}
				a.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		t2.put("Rows", a);
		
		String sql_total = "select count(*) as count_ from government_zone_report " + where;

		try {
			rs = statement.executeQuery(sql_total);
			rs.next();
			t2.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t2;
	}		
	
	public static Hashtable upload(String path,String executor){
		Hashtable t_return = new Hashtable();
		Statement statement = null;
		try {
			statement = tools.getConn().createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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

        Sheet sheet = workBook.getSheet("data_government_zone");
        int rows = sheet.getRows();
        System.out.println(sheet.getColumns()+" "+rows);

        if(rows>10000){
    		t_return.put("status", "1");
    		t_return.put("msg", "row count must be less than 10000 , your rows:"+rows);
    		return t_return;
        }
        
        String[] sqls = new String[rows-1];
        try {
			statement.executeUpdate("START TRANSACTION;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}        
        int id = tools.getTableId("government_zone");
		Hashtable t__grup_type = basic_user.getSession(executor);
		String group_code = (String) t__grup_type.get("group_code");

        for(int i=1;i<rows;i++){
        	id++;        	
        	sqls[i-1] = "insert into government_zone( code,name,count_floor,population,owner,owner_type,owner_person_id,id_gis_polygon,time_founded,photo,socialworker,type,status"
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
        	try {
				statement.executeUpdate(sqls[i-1]);
			} catch (SQLException e) {
//		        try {
//					statement.executeUpdate("ROLLBACK;");
//					break;
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}	
				e.printStackTrace();
			}
        	System.out.println(sqls[i-1]);
        }
        
		try {
	        String Sql = tools.getSQL("basic_memory__id_update").replace("__code__", "government_zone") ;			
	        statement.executeUpdate(Sql );
	        
	        statement.executeUpdate("COMMIT;");
		} catch (SQLException e) {
			e.printStackTrace();
		}          
        
        workBook.close();
        try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        t_return.put("status","1");
        t_return.put("msg","ok");
		return t_return;
	}
	
	public static Hashtable download(
			 String search
			,String pagesize
			,String pagenum
			,String executor) {
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		Hashtable t_return = new Hashtable();
		
		String sql= tools.getSQL("government_zone__grid");	
		String where = oa_group_report.search(search,user_type,executor,group_code);
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		System.out.println(sql);
		
		String filename = String.valueOf(Math.random()*1000);
		String thepath = tools.getSQL("APPPATH")+"\\file\\download\\"+filename+".xls";
		
		Statement statement;
		try {
			statement = tools.getConn().createStatement();
			ResultSet rs = statement.executeQuery(sql);
			WritableWorkbook book;
			int i = 0;
			
			try {

				System.out.println(thepath);
				book = Workbook.createWorkbook(new File(thepath));
				WritableSheet sheet = book.createSheet("data_government_zone", 0);		
				
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
				
				while (rs.next()) {			
					i++;

					sheet.addCell(new Label(0,i,rs.getString("code")));
					sheet.addCell(new Label(1,i,rs.getString("name")));
					sheet.addCell(new Label(2,i,rs.getString("count_floor")));
					sheet.addCell(new Label(3,i,rs.getString("population")));
					sheet.addCell(new Label(4,i,rs.getString("owner")));
					sheet.addCell(new Label(5,i,rs.getString("owner_type")));
					sheet.addCell(new Label(6,i,rs.getString("owner_person_id")));
					sheet.addCell(new Label(7,i,rs.getString("id_gis_polygon")));
					sheet.addCell(new Label(8,i,rs.getString("time_founded")));
					sheet.addCell(new Label(9,i,rs.getString("photo")));
					sheet.addCell(new Label(10,i,rs.getString("type")));
					sheet.addCell(new Label(11,i,rs.getString("status")));
					
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
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        t_return.put("status","1");
        t_return.put("msg","ok");
        t_return.put("file","../file/download/"+filename+".xls");
		
		return t_return;
	}
	
	public static Hashtable statistics_time(			
			 String search
			,String attribute
			,String executor
			){
		Hashtable t__user_session = basic_user.getSession(executor);	
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		String time_length = "7";
		if(attribute.equals("day"))time_length = "10";
		if(attribute.equals("month"))time_length = "7";
		if(attribute.equals("year"))time_length = "4";
		Hashtable t2 = new Hashtable();
		
		String sql = "select left(government_zone.time_founded,"+time_length+") as time_, count(*) as count_ from government_zone ";		
		search = URLDecoder.decode(search);
		search = search.replace("\\\"", "\"");
		search = search.replace("\"{", "{");
		search = search.replace("}\"", "}");		
		String where = oa_group_report.search(search,user_type,executor,group_code);
		sql += where;
		sql += " group by left(government_zone.time_founded, "+time_length+")  ";
		
		ArrayList xAxis = new ArrayList();
		ArrayList series = new ArrayList();
		Statement statement;

		try {
			statement = tools.getConn().createStatement();
			System.out.println(sql);
			ResultSet rs = statement.executeQuery(sql);

			ResultSetMetaData rsData = rs.getMetaData();
			int count = 0;
			while (rs.next() && count < 100) {
				count ++;
				xAxis.add(rs.getString("time_"));
				series.add(rs.getInt("count_"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		t2.put("xAxis", xAxis);	
		t2.put("series", series);	

		return t2;
	}		
	
	public static Hashtable statistics_attribute(
			String search
			,String attribute
			,String executor
			){
		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		Hashtable t2 = new Hashtable();
		String sql = "select "+attribute+",(select extend4 from basic_memory where extend5 = 'government_zone__"+attribute+"' and code = government_zone."+attribute+") as name, count(*) as count_,sum(population) as sum_ from government_zone ";
		search = URLDecoder.decode(search);
		search = search.replace("\\\"", "\"");
		search = search.replace("\"{", "{");
		search = search.replace("}\"", "}");
		Hashtable t_search = new Gson().fromJson(search, Hashtable.class);
		String where = oa_group_report.search(search,user_type,executor,group_code);

		ArrayList theData = new ArrayList();
		
		Statement statement;

		try {
			statement = tools.getConn().createStatement();			
			if(attribute.equals("zone")){
				String syszone = tools.getSQL("ZONE");
				int len = syszone.length();
				System.out.println((new Gson()).toJson(t_search));
				if(t_search.containsKey("government_zone__zone")){
					String zonecode = (String) t_search.get("government_zone__zone");
					System.out.println(zonecode);
					len = zonecode.length();
				}
				sql = "select left(code,"+(len+2)+") as code,(select extend4 from basic_memory where extend5 = 'group' and code = left(government_zone.code,"+(len+2)+") ) as name,sum(population) as sum_,count(*) as count_ from government_zone";
				sql += where;
				sql += " group by left(code,"+(len+2)+")";
				System.out.println(sql);
				ResultSet rs = statement.executeQuery(sql);
				ArrayList xAxis = new ArrayList();
				ArrayList series = new ArrayList();
				ArrayList series2 = new ArrayList();
				ResultSetMetaData rsData = rs.getMetaData();
				int count = 0;
				while (rs.next() && count < 100) {
					count ++;
					String thename = rs.getString("name");
					if(thename==null)thename=rs.getString("code");
					xAxis.add(thename);
					series.add(rs.getInt("count_"));
					series2.add(rs.getInt("sum_"));
				}
				t2.put("xAxis", xAxis);	
				t2.put("series", series);	
				t2.put("series2", series2);	
			}else{
				sql += where;
				sql += " group by   "+attribute;
				System.out.println(sql);
				ResultSet rs = statement.executeQuery(sql);
				while (rs.next()) {		
					ArrayList a_ = new ArrayList();
					a_.add(rs.getString("name"));
					a_.add(rs.getInt("count_"));
					theData.add(a_);
				}
				t2.put("data", theData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return t2;
	}	
	
	public static Hashtable statistics_gis(			
			 String search
			,String attribute
			,String executor
			){
		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		
		Hashtable t2 = new Hashtable();

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
		if(t_search.containsKey("government_zone__zone")){
			String zonecode = (String) t_search.get("government_zone__zone");
			System.out.println(zonecode);
			len = zonecode.length();
		}
		String gistable = "gis_polygon_"+(len+6);		
		
		String where = oa_group_report.search(search,user_type,executor,group_code);
		String sql = "select left(government_zone.code,"+(len+2)+") as code,"
				+ "(select name from basic_group where code = left(government_zone.code,"+(len+2)+") ) as name"
				+ ",(select astext(ogc_geom)  from "+gistable+" where "+gistable+".`code` = left(government_zone.code,"+(len+2)+")) as wkt "
				+ ",sum(population) as sum_,count(*) as count_ from government_zone";
		sql += where;
		sql += " group by left(code,"+(len+2)+") order by count(*)";
		ArrayList theData = new ArrayList();
		
		Statement statement;

		try {
			statement = tools.getConn().createStatement();			
			
			System.out.println(sql);
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {		
				Hashtable t = new Hashtable();
				String wkt = rs.getString("wkt");
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
				t.put("count", rs.getString("count_"));
				t.put("code", rs.getString("code"));
				t.put("sum", rs.getString("sum_"));
				t.put("name", rs.getString("name"));
				theData.add(t);
			}
			t2.put("data", theData);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return t2;
	}		
	
	public static Hashtable bind(
			 String socialworker_id
			,String ids
			,String executor
			){

		Hashtable t__user_session = basic_user.getSession(executor);
		String group_code = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
	
		Hashtable t_return = new Hashtable();
		
		try {
			Statement statement = tools.getConn().createStatement();
			String[] ids_ = ids.split(",");
			for(int i=0;i<ids_.length;i++){
				String sql = "update government_zone set socialworker_id ="+socialworker_id+" where government_zone.id="+ids_[i];
				System.out.println(sql);
				statement.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return t_return;
	}
	
	public static int test_index = 0;
	public static ArrayList data4test(int page){
		ArrayList a_return = new ArrayList();
		String sql_basic_group = "select * from government_zone ";
		Statement statement = null;
		Statement statement2 = null;
		try {
			statement = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			ResultSet rs = statement.executeQuery(sql_basic_group);	
			statement2.executeUpdate("START TRANSACTION;");
			while(rs.next()){
				for(int year=2010;year<=2013;year++){
					for(int month=1;month<=12;month++){						
						String themonth = (month>=10)?month+"":"0"+month;
						int len = rs.getString("code").length();
						int data = (int)( 5*( Math.pow(1.083,month))*( Math.pow(10,(10-len)/2)) );
						System.out.println(data);
						oa_group_report.test_index++;
						String sql_insert = "insert into government_zone_report(id,name,code,type,status,code_zone,remark,date,num1,num2,num3,num4,num5,num6,num7,num8,num9,num10,num11,num12,num13,num14,num15,num16,num17,num18,num19,num20) values ("
								+ " '"+oa_group_report.test_index+"'"
								+ ",'政府数据月报 "+oa_group_report.test_index+"'"
								+ ",'"+rs.getString("code")+"-"+year+themonth+"'"
								+ ",'20'"
								+ ",'"+(int)(Math.random()*5+1)+"0'"
								+ ",'"+rs.getString("code")+"'"
								+ ",'说明"+(int)(Math.random()*100000)+"'"
								+ ",'"+year+"-"+themonth+"-01"+"'"
								
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"
								+ ",'"+(int)(Math.random()*data/5+data)+"'"

								+ ");";
						statement2.execute(sql_insert);
						
						if((oa_group_report.test_index%10000)==0){
							statement2.executeUpdate("COMMIT;");
							statement2.executeUpdate("START TRANSACTION;");
						}
					}
					int len = rs.getString("code").length();
					int data = (int)( 50*( Math.pow(10,(10-len)/2))*(Math.pow(1.2, year-2009)) ) ;
					oa_group_report.test_index++;
					String sql_insert = "insert into government_zone_report(id,name,code,type,status,code_zone,remark,date,num1,num2,num3,num4,num5,num6,num7,num8,num9,num10,num11,num12,num13,num14,num15,num16,num17,num18,num19,num20) values ("
							+ " '"+oa_group_report.test_index+"'"
							+ ",'政府数据年报 "+oa_group_report.test_index+"'"
							+ ",'"+rs.getString("code")+"-"+year+"'"
							+ ",'10'"
							+ ",'"+(int)(Math.random()*5+1)+"0'"
							+ ",'"+rs.getString("code")+"'"
							+ ",'说明"+(int)(Math.random()*100000)+"'"
							+ ",'"+year+"-01-01"+"'"
							
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"
							+ ",'"+(int)(Math.random()*data/5+data)+"'"

							+ ");";
					statement2.execute(sql_insert);
				}
			}	
			statement2.executeUpdate("COMMIT;");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return a_return;
	}
	
	public static void main(String args[]) throws SQLException{
		oa_group_report.data4test(1);
//		government_zone.data4test(2);
//		Hashtable t = government_zone.download("{}", "10", "1", "10");/
//		basic_group.upload(tools.getSQL("APPPATH")+"/file/developer/data_buildings.xls", "admin");
//		government_zone.upload(tools.getSQL("APPPATH")+"/file/developer/data_buildings.xls", "admin");
//		System.out.println(Math.pow(1.08333, 3));
	}	
}
