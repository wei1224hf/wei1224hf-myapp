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

public class government_resident {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String) request.getParameter("executor");
		String session = (String) request.getParameter("session");
		Gson g = new Gson();

		Hashtable t__grup_type = new Hashtable();
		String group_code = (String) t__grup_type.get("group_code");
		String user_type = (String) t__grup_type.get("user_type");
		
		Hashtable t = new Hashtable();
		t.put("state", "2");
		t.put("msg", "access denied");			

		try {
			if (functionName.equals("grid")) {
				String sortname = "id";
				String sortorder = "asc";
				if (request.getParameter("sortname") != null) {
					sortname = (String) request.getParameter("sortname");
				}
				if (request.getParameter("sortorder") != null) {
					sortorder = (String) request.getParameter("sortorder");
				}
				t = grid((String) request.getParameter("search"),
						(String) request.getParameter("pagesize"),
						(String) request.getParameter("page"), executor,
						sortname, sortorder);
			}else if (functionName.equals("add")) {
				if (basic_user.checkPermission(executor, "520221", session)) {
					t = add((String) request.getParameter("data"), executor);
				}
			} else if (functionName.equals("modify")) {
				if (basic_user.checkPermission(executor, "120221", session)) {
					t = modify((String) request.getParameter("data"),
							executor);
				}
			} else if (functionName.equals("remove")) {
				if (basic_user.checkPermission(executor, "120223", session)) {
					t = remove((String) request.getParameter("ids"),
							executor);
				}
			} else if (functionName.equals("view")) {
				t = view((String) request.getParameter("id"));
			} else if (functionName.equals("loadConfig")) {
					t = loadConfig();
			} else if (functionName.equals("lowerCodes")) {
				out = g.toJson(lowerCodes(
						(String) request.getParameter("code"),
						(String) request.getParameter("reference")));
				return out;
			} else if (functionName.equals("download")) {
				if (basic_user.checkPermission(executor, "520212", session)) {
					t = download((String) request.getParameter("search"),
							(String) request.getParameter("pagesize"),
							(String) request.getParameter("page"),
							user_type);
				}
			}else{
				System.out.println(functionName);
			}

			out = g.toJson(t);

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
			String sql = "select code,name as value from basic_group where code like  '2102__' order by code";
			ResultSet rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_6", a);		
			
			sql = "select code,name as value from basic_group where code like  '210204130_' order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_10", a);					
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'government_resident__type' order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_resident__type", a);		
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'government_resident__types' order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_resident__types", a);				
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'government_resident__status' order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_resident__status", a);			
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'government_resident__relation' and (code like '10__' or code = '9001' or code = '00') order by code";
			rs = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_resident__relation", a);					
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'industry' order by code";
			rs = stmt.executeQuery(sql);
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
			t_return.put("msg", e.toString());		
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }			
		
		return t_return;
	}
	
	public static ArrayList lowerCodes(String code,String reference) throws SQLException {	
		String sql = "";
		if(code.length()==10){
			sql = "select code,name as value from government_building where code like '"+code+"BD____' order by name ";
		}else if(code.length()==16){
			sql = "select code,name as value from government_family where code like '"+code+"____' order by name ";
		}else{
			sql = "select code,name as value from basic_group where code like '"+code+"__' and type = '11' ";
		}
		
		System.out.println(sql);
		Statement stmt = tools.getConn().createStatement();
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
		return a;
	}		
	
	public static Hashtable add(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement stmt = tools.getConn().createStatement();
		
		System.out.println(data);
		data = data.replace("null", "\"\"");
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
		String code_ = (String) t_data.get("family");
		t_data.remove("family");
		
		String sql_select = "select max(code) as thecode from government_resident where code like '"+code_+"____';";
		
		ResultSet rs = stmt.executeQuery(sql_select);
		String thecode = "";
		while(rs.next()){
			thecode = rs.getString("thecode");
			if(thecode==null){
				 thecode = code_+"0001";
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
			thecode = code_+thecode + i;
		}
		t_data.put("code", thecode);

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
		
		String id = String.valueOf(tools.getTableId("government_resident"));
		t_data.put("id", id);
		t_data.put("creater_code", "'"+executor+"'");
		t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

		e = t_data.keys();
		String keys = "insert into government_resident (";
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
		t_return.put("code", thecode);
		t_return.put("id", id);		
		
		return t_return;
	}	
	
	public static Hashtable remove(String ids,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement stmt = tools.getConn().createStatement();
		String[] id = ids.split(",");
		String sql = "";
		for(int i=0;i<id.length;i++){
			sql = "delete from oa_person where id = (select person_id from government_resident where id = '"+id[i]+"' )";
			stmt.executeUpdate(sql);
			sql = "delete from government_resident where id = '"+id[i]+"' ;";
			stmt.executeUpdate(sql);
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
			if(value.equals("null") || value.length()==0 || value.equals("")){
				t_data.remove(key);
			}else{
				t_data.put(key, "'"+value+"'");
			}
			
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update government_resident set ";
	
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
		Statement stmt = tools.getConn().createStatement();
		String sql = tools.getConfigItem("government_resident__view").replace("__id__", "'"+id+"'");
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

		Hashtable search_t = (Hashtable) new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and government_resident.name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and government_resident.type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and government_resident.status = '"+value+"'";
			}
			if(key.equals("zone_10")){
				where += " and government_resident.code like '"+value+"%'";
			}
			if(key.equals("building")){
				where += " and government_resident.code like '"+value+"%'";
			}		
			if(key.equals("family")){
				where += " and government_resident.code like '"+value+"%'";
			}
		}
		
		if(group_code.length()>2){//非系统用户
			String[] group_code_ = group_code.split("-");
			where += " and government_resident.code like '"+group_code_[0]+"%'";
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
		
		String sql = tools.getConfigItem("government_resident__grid");
		String orderby = " order by "+sortname+" "+sortorder;
		String where = government_resident.search(search,user_type,executor,user_group);	
		String page = " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";
		sql = sql + where + orderby + page; 
		
		Statement stmt = null;
		ResultSet rs;
		ArrayList a = new ArrayList();
		try {
			stmt = tools.getConn().createStatement();
			System.out.println(sql);
			rs = stmt.executeQuery(sql);
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
				a.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		t2.put("Rows", a);
		
		String sql_total = "select count(*) as count_ from government_resident " + where;

		try {
			rs = stmt.executeQuery(sql_total);
			rs.next();
			t2.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t2;
	}	
	
	public static Hashtable upload(String path,String executor){
		Hashtable t_return = new Hashtable();
		Statement stmt = null;
		try {
			stmt = tools.getConn().createStatement();
		} catch (SQLException e2) {
			e2.printStackTrace();
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

        Sheet sheet = workBook.getSheet("data_government_resident");
        int rows = sheet.getRows();
        System.out.println(sheet.getColumns()+" "+rows);
        
        if(rows>20000){
    		t_return.put("status", "1");
    		t_return.put("msg", "row count must be less than 2000 , your rows:"+rows);
    		return t_return;
        }
        Cell cell = null;//就是单个单元格
        String[] sqls = new String[rows-1];
        String[] sqls2 = new String[rows-1];
		Hashtable t__grup_type = new Hashtable();

		t__grup_type = basic_user.getSession(executor);
		String group_code = (String) t__grup_type.get("group_code");        
        
        try {
			stmt.executeUpdate("START TRANSACTION;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        int personid = tools.getTableId("oa_person");        
        int id = tools.getTableId("government_resident");

        for(int i=1;i<rows;i++){
        	id++;  
        	personid++;
        	
        	sqls[i-1] = "insert into oa_person( " +
			"name,birthday,card,cardid,photo,height,nationality,gender,nation,marriage,degree,degree_school,degree_school_code,politically,address_birth,address_birth_code,cellphone,email,qq,address,address_code  ,id,creater_code,creater_group_code) values ('" 
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
			+sheet.getCell(27,i).getContents()+"','"
			+sheet.getCell(28,i).getContents()+"','"
			+sheet.getCell(29,i).getContents()+"','"
			+sheet.getCell(30,i).getContents()+"','"
			+sheet.getCell(31,i).getContents()+"','"			
			+personid+"','"+executor+"','"+group_code+"');";		
        	
			sqls2[i-1] = "insert into government_resident( " +
        			"name,code,time_in,time_out,person_id,types,job,job_code,relation,type,status, id,creater_code,creater_group_code) values ('"
        			+sheet.getCell(11,i).getContents()+"','"
        			+sheet.getCell(0,i).getContents()+"','"
        			+sheet.getCell(1,i).getContents()+"','"
        			+sheet.getCell(2,i).getContents()+"','"
        			+personid+"','"
        			+sheet.getCell(4,i).getContents()+"','"
        			+sheet.getCell(5,i).getContents()+"','"
        			+sheet.getCell(6,i).getContents()+"','"
        			+sheet.getCell(7,i).getContents()+"','"
        			+sheet.getCell(8,i).getContents()+"','"
        			+sheet.getCell(9,i).getContents()+"','"
        			
        			+id+"','"+executor+"','"+group_code+"');";
        	
        	
			try {
				stmt.executeUpdate(sqls[i-1]);
				stmt.executeUpdate(sqls2[i-1]);
			} catch (SQLException e) {
				System.out.println(sqls2[i-1]);
				System.out.println(sqls[i-1]);
				System.out.println(i);
				e.printStackTrace();
//		        try {
//					stmt.executeUpdate("ROLLBACK;");
//					break;
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
			}	
        }

		try {
	        String Sql = tools.getConfigItem("basic_memory__id_update").replace("__code__", "oa_person") ;			
	        stmt.executeUpdate(Sql );
	        stmt.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_resident") );
	        
	        stmt.executeUpdate("COMMIT;");
		} catch (SQLException e) {
			e.printStackTrace();
		}	     
        
        workBook.close();//记得关闭
        try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        t_return.put("status","1");
        t_return.put("msg","ok");
		return t_return;
	}
	
	public static Hashtable download(String search,String pagesize,String pagenum,String user_type) throws SQLException{
		Hashtable t_return = new Hashtable();
		String sql = "";
		String where = " where 1=1 ";
		if(user_type.equals("10")){
			sql = tools.getConfigItem("government_resident__grid");
		}
		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and government_resident.name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and government_resident.type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and government_resident.status = '"+value+"'";
			}
			if(key.equals("zone_10")){
				where += " and government_resident.code like '"+value+"%'";
			}
		}
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		Statement stmt = tools.getConn().createStatement();
		System.out.println(sql);
		ResultSet rs = stmt.executeQuery(sql);
		WritableWorkbook book;
		int i = 0;
		String filename = String.valueOf(Math.random()*1000);
		try {
			String thepath = tools.getConfigItem("APPPATH")+"\\file\\download\\"+filename+".xls";
			System.out.println(thepath);
			book = Workbook.createWorkbook(new File(thepath));
			WritableSheet sheet = book.createSheet("data_government_resident", 0);		
			
			sheet.addCell( new Label(0,0,"code"));
			sheet.addCell( new Label(1,0,"time_in"));
			sheet.addCell( new Label(2,0,"time_out"));
			sheet.addCell( new Label(3,0,"person_id"));
			sheet.addCell( new Label(4,0,"types"));
			sheet.addCell( new Label(5,0,"job"));
			sheet.addCell( new Label(6,0,"job_code"));
			sheet.addCell( new Label(7,0,"relation"));
			sheet.addCell( new Label(8,0,"type"));
			sheet.addCell( new Label(9,0,"status"));

			sheet.addCell( new Label(11,0,"name"));
			sheet.addCell( new Label(12,0,"birthday"));
			sheet.addCell( new Label(13,0,"card"));
			sheet.addCell( new Label(14,0,"cardid"));
			sheet.addCell( new Label(15,0,"photo"));
			sheet.addCell( new Label(16,0,"height"));
			sheet.addCell( new Label(17,0,"nationality"));
			sheet.addCell( new Label(18,0,"gender"));
			sheet.addCell( new Label(19,0,"nation"));
			sheet.addCell( new Label(20,0,"ismarried"));
			sheet.addCell( new Label(21,0,"degree"));
			sheet.addCell( new Label(22,0,"degree_school"));
			sheet.addCell( new Label(23,0,"degree_school_code"));
			sheet.addCell( new Label(24,0,"politically"));
			sheet.addCell( new Label(25,0,"address_birth"));
			sheet.addCell( new Label(26,0,"address_birth_code"));
			sheet.addCell( new Label(27,0,"cellphone"));
			sheet.addCell( new Label(28,0,"email"));
			sheet.addCell( new Label(29,0,"qq"));
			sheet.addCell( new Label(30,0,"ddress"));
			sheet.addCell( new Label(31,0,"address_code"));

			
			while (rs.next()) {			
				i++;

				sheet.addCell(new Label(0,i,rs.getString("code")));
				sheet.addCell(new Label(1,i,rs.getString("time_in")));
				sheet.addCell(new Label(2,i,rs.getString("time_out")));
				sheet.addCell(new Label(3,i,rs.getString("person_id")));
				sheet.addCell(new Label(4,i,rs.getString("types")));
				sheet.addCell(new Label(5,i,rs.getString("job")));
				sheet.addCell(new Label(6,i,rs.getString("job_code")));
				sheet.addCell(new Label(7,i,rs.getString("relation")));
				sheet.addCell(new Label(8,i,rs.getString("type")));
				sheet.addCell(new Label(9,i,rs.getString("status")));
				
				//TODO 真的很麻烦
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
		
		return t_return;
	}
	
	public static ArrayList data4test(int page){
		ArrayList a_return = new ArrayList();
		String sql_parent = "select code,type from government_family limit "+(page-1)*100000+",100000 ";
		Statement stmt = null;
		Statement stmt2 = null;
		String address_birth = "";
		String address_birth_code = "";

		try {
			stmt = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			String sql_birthplace = "select code,value from basic_parameter where basic_parameter.reference = 'zone'";
			ResultSet rs = stmt.executeQuery(sql_birthplace);
			ArrayList a_birthplace = new ArrayList();
			while(rs.next()){
				Hashtable t = new Hashtable();
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));
				a_birthplace.add(t);
			}
			rs.close();
			rs = stmt.executeQuery(sql_parent);			
			
			stmt2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			stmt2.executeUpdate("START TRANSACTION;");			
			
	        int id = tools.getTableId("government_resident");
	        int id__oa_person = tools.getTableId("oa_person");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin_ = (format.parse("1940-01-01")).getTime();
			long end = (format.parse("2011-12-01")).getTime();	
			int sql_index = 0;
			while (rs.next()) {		
				
				String code = rs.getString("code");
				String type = rs.getString("type");
				
				int count = 0;
				if(type.equals("1"))count = 3;//核心家庭: 爸爸 妈妈 孩子
				if(type.equals("2"))count = 5;//主干家庭: 爸爸 妈妈 孩子 爷爷奶奶
				if(type.equals("3"))count = (int)(Math.random()*6+6);//联合家庭: 很多人一起
				if(type.equals("4"))count = 2;//单亲家庭: 一个家长 一个孩子
				if(type.equals("5"))count = (int)(Math.random()*3+2);//重组家庭: 离异后结合
				if(type.equals("6"))count = 2;//丁克家庭: 夫妻,不愿生孩子
				if(type.equals("7"))count = 1;//独居
				count += 1000;

				for(int i=1000;i<count;i++){
					
					String r_type = (Math.random()>0.7?"1":"2");					
					
					long begin = (long)(begin_ +  (end - begin_)*((i-1000+1+0.0)/(count-1000)));//家庭总数逐年递增
					long birthday = begin + (long)(Math.random() * (end - begin)) ;
					long time_in = birthday;
					int birth_i = (int)(Math.random()*(1444-1422)+1422);

					if(r_type.equals("2")){
						time_in = birthday + 316224*100000 * (long)(Math.random()*20+15);
						birth_i = (int)(Math.random()*3500);
					}
					address_birth = (String)((Hashtable)a_birthplace.get( birth_i )).get("value");
					address_birth_code = (String)((Hashtable)a_birthplace.get( birth_i )).get("code");
					long time_out = time_in + 316224*100000 * (long)(Math.random()*20+15);
					String types = "";
					for(int i2=11;i2<19;i2++){
						if(Math.random()>0.8)types+=i2+",";						
					}
					if(!types.equals(""))types= types.substring(0,types.length()-1);
					id++;
					id__oa_person++;
					
					String sql_insert_person = "insert into oa_person (name,birthday,card,cardid,photo,height,nationality,gender,nation,marriage,degree,degree_school,degree_school_code,politically,address_birth,address_birth_code,cellphone,email,qq,address,address_code,id,creater_code,creater_group_code) values ("
							+ "'"+tools.randomName()+"',"
							+ "'"+format.format( new Date( birthday ))+"',"
							+ "'"+(Math.random()>0.2?1:(int)(Math.random()*4+1))+"',"
							+ "'X111111111111111111',"
							+ "'../file/upload/photo/"+(Math.random()>0.5?1:2)+"/"+(int)(Math.random()*29+1)+".jpg',"
							+ "'"+(int)(Math.random()*90+120)+"',"
							+ "'中国',"
							+ "'"+(Math.random()>0.5?1:2)+"',"
							+ "'"+(Math.random()>0.2?1:(int)(Math.random()*55+2))+"',"
							+ "'"+(int)(Math.random()*4+1)+"0',"
							+ "'"+(int)(Math.random()*9+1)+"0',"
							+ "'XXX学校',"
							+ "'0',"
							+ "'"+(Math.random()>0.3?13:(int)(Math.random()*11+1))+"',"
							+ "'"+address_birth+"',"
							+ "'"+address_birth_code+"',"
							+ "'11111111111',"
							+ "'1111@qq.com',"
							+ "'1111111',"
							+ "'目前所在的住址"+(Math.random()*10000)+"',"
							+ "'0',"
							+ "'"+ id__oa_person+"" +"','admin','10'"
							+ ")";
					String sql_insert_resident = "insert into government_resident (code,name,time_in,time_out,person_id,types,job,job_code,relation,type,status,id,creater_code,creater_group_code) values ("
							+ "'"+ code+"-"+i +"',"
							+ "'"+tools.randomName()+"',"
							+ "'"+format.format( time_in )+"',"
							+ "'"+format.format( time_out )+"',"
							+ "'"+id__oa_person+"',"
							+ "'"+ types +"',"
							+ "'工作岗位',"
							+"'A',"
							+"'00',"
							+"'"+r_type+"',"
							+"'"+((Math.random()>0.9)?"20":"10")+"',"
							+ "'"+ id+"" +"','admin','10'"
							+ ")";

					sql_index++;
					stmt2.executeUpdate(sql_insert_person);
					stmt2.executeUpdate(sql_insert_resident);
					
					if(sql_index>=15000){
						sql_index = 0;
						stmt2.executeUpdate("COMMIT;");
						stmt2.executeUpdate("START TRANSACTION;");
					}
				}				
			}
	        stmt2.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_resident") );
	        stmt2.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "oa_person") );
	        stmt2.executeUpdate("COMMIT;");		
	        
	        stmt2.close();
	        stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return a_return;
	}		
	
	
	public static void main(String args[]) throws SQLException{
//		government_building.data4test(1);
//		government_family.data4test();
		government_resident.data4test(1);
//		government_company.data4test();
	}	
}
