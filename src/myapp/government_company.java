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
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class government_company {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			Hashtable t__grup_type = new Hashtable();
			t__grup_type = basic_user.getSession(executor);	
			
				
			String group_code = (String) t__grup_type.get("group_code");
			String user_type = (String) t__grup_type.get("user_type");
			
			if (functionName.equals("grid")) {			
				String sortname = "government_company.id";
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
				if(basic_user.checkPermission(executor, "520221", session)){

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
				if(basic_user.checkPermission(executor, "520212", session)){
					t = download(
							(String) request.getParameter("search"),
							(String) request.getParameter("pagesize"),
							(String) request.getParameter("page"),
							user_type
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
	
	public static Hashtable loadConfig() throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		
		String sql = "select code,name as value from basic_group where code like  '2102__' order by code";
		ResultSet rs = statement.executeQuery(sql);
		ArrayList a = new ArrayList();
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
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'government_company__type' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("government_company__type", a);		
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'government_company__types' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("government_company__types", a);				
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'government_company__status' order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("government_company__status", a);			
		
		sql = "select code,extend4 as value from basic_memory where extend5 = 'government_company__relation' and (code like '10__' or code = '9001' or code = '00') order by code";
		rs = statement.executeQuery(sql);
		a = new ArrayList();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			t.put("code", rs.getString("code"));
			t.put("value", rs.getString("value"));			
			a.add(t);
		}
		t_return.put("government_company__relation", a);					
		
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
		
		return t_return;
	}
	
	public static ArrayList lowerCodes(String code,String reference) throws SQLException{	
		String sql = "";
		if(code.length()==10){
			sql = "select code,name as value from government_building where code like '"+code+"BD____' order by name ";
		}else if(code.length()==16){
			sql = "select code,name as value from government_family where code like '"+code+"____' order by name ";
		}else{
			sql = "select code,name as value from basic_group where code like '"+code+"__' and type = '11' ";
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
	
	public static Hashtable add(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		
		System.out.println(data);
		data = data.replace("null", "\"\"");
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
		String code_ = (String) t_data.get("family");
		t_data.remove("family");
		
		String sql_select = "select max(code) as thecode from government_company where code like '"+code_+"____';";
		
		ResultSet rs = statement.executeQuery(sql_select);
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
		
		String id = String.valueOf(tools.getTableId("government_company"));
		t_data.put("id", id);
		t_data.put("creater_code", "'"+executor+"'");
		t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

		e = t_data.keys();
		String keys = "insert into government_company (";
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
		t_return.put("msg", "ok");
		t_return.put("code", thecode);
		t_return.put("id", id);		
		
		return t_return;
	}	
	
	public static Hashtable remove(String ids,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		String[] id = ids.split(",");
		String sql = "";
		for(int i=0;i<id.length;i++){
			sql = "delete from oa_person where id = (select person_id from government_company where id = '"+id[i]+"' )";
			statement.executeUpdate(sql);
			sql = "delete from government_company where id = '"+id[i]+"' ;";
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
			if(value.equals("null") || value.length()==0 || value.equals("")){
				t_data.remove(key);
			}else{
				t_data.put(key, "'"+value+"'");
			}
			
		}
		t_data.put("time_lastupdated", "now()");
		t_data.put("count_updated", "count_updated+1");	
		
		e = t_data.keys();
		sql = "update government_company set ";
	
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
		LinkedHashMap t_data = new LinkedHashMap();
		try {
			Statement statement = tools.getConn().createStatement();
			String sql = tools.getConfigItem("government_company__view").replace("__id__", "'"+id+"'");
			System.out.println(sql);
			ResultSet resultset = tools.getConn().createStatement().executeQuery(sql);
			resultset.next();
			ResultSetMetaData m = resultset.getMetaData();
			
			for(int i=1;i<=m.getColumnCount();i++){
				String key  = m.getColumnLabel(i);
				String value = "-";
				if(resultset.getString(m.getColumnLabel(i)) != null && resultset.getString(m.getColumnLabel(i)) != ""){
					value = resultset.getString(m.getColumnLabel(i));
				}
				t_data.put(key, value);			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				where += " and government_company.code like '"+value+"%'";
			}	
			
			else{
				where += " and "+str[0]+"."+str[1]+" = '"+value+"' ";
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
		
		Hashtable t2 = new Hashtable();
		String sql = "";
		String where = search(search,user_type,executor,user_group);	
		sql = tools.getConfigItem("government_company__grid");
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		Statement statement;
		try {
			statement = tools.getConn().createStatement();
			System.out.println(sql);
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
			t2.put("Rows", a);	
			String sql_total = "select count(*) as count_ from government_company "+where;

			rs = statement.executeQuery(sql_total);
			rs.next();
			t2.put("Total", rs.getString("count_"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t2;
	}		
	
	public static Hashtable upload(String path,String executor){
		Hashtable t_return = new Hashtable();
		Statement statement = null;
		try {
			statement = tools.getConn().createStatement();
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

        Sheet sheet = workBook.getSheet("data_government_company");
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
			statement.executeUpdate("START TRANSACTION;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        int personid = tools.getTableId("oa_person");        
        int id__government_company = tools.getTableId("government_company");

        for(int i=1;i<rows;i++){
        	id__government_company++;  
        	
        	sqls[i-1] = "insert into government_company( " +
			"code,name,code2,tax,address,owner,id_owner,cellphone,phone,business,business_code,longitude,latitude,code_building,socialworker,time_in,time_out,path_photo,type,status,count_employee,property,turnover "
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
			+id__government_company+"','"+executor+"','"+group_code+"');";
			System.out.println(sqls[i-1]);

			try {
				statement.executeUpdate(sqls[i-1]);
			} catch (SQLException e) {
				System.out.println(i);
				e.printStackTrace();
		        try {
					statement.executeUpdate("ROLLBACK;");
					break;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}	
        }

		try {
	        statement.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_company") );
	        statement.executeUpdate("COMMIT;");
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
			sql = tools.getConfigItem("government_company__grid");
		}
		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and government_company.name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and government_company.type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and government_company.status = '"+value+"'";
			}
			if(key.equals("zone_10")){
				where += " and government_company.code like '"+value+"%'";
			}
		}
		
		sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		Statement statement = tools.getConn().createStatement();
		System.out.println(sql);
		ResultSet rs = statement.executeQuery(sql);
		WritableWorkbook book;
		int i = 0;
		String filename = String.valueOf(Math.random()*1000);
		try {
			String thepath = tools.getConfigItem("APPPATH")+"\\file\\download\\"+filename+".xls";
			System.out.println(thepath);
			book = Workbook.createWorkbook(new File(thepath));
			WritableSheet sheet = book.createSheet("data_government_company", 0);		
			
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
	
	public static ArrayList data4test(){
		ArrayList a_return = new ArrayList();
		String sql_parent = "select code,type from government_building ";
		Statement statement = null;
		Statement statement2 = null;
		try {
			statement = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			statement2.executeUpdate("START TRANSACTION;");
			ResultSet rs = statement.executeQuery(sql_parent);			
			
	        int id = tools.getTableId("government_company");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin_ = (format.parse("1940-01-01")).getTime();
			long end = (format.parse("2011-12-01")).getTime();	
			int sql_index = 0;
			while (rs.next()) {			
				String code = rs.getString("code");
				String type = rs.getString("type");
				int count = 0;
				if(type.equals("20")||type.equals("22")){
					count = 1000+(int)(Math.random()*5+1);
				}
				if(type.equals("21") || type.equals("30") ){
					count = 1001;
				}
				if((type.equals("10")||type.equals("11")||type.equals("12")))continue;
				
				for(int i=1000;i<count;i++){
					long begin = (long)(begin_ +  (end - begin_)*((i-1000+1+0.0)/(count-1000)));

					
					id++;
					String sql_insert_building = "insert into government_company (code,code_building,name,time_in,time_out,type,status,path_photo,id,creater_code,creater_group_code) values ("
							+ "'"+ code +"-"+ i +"',"
							+ "'"+ code +"',"
							+ "'商铺"+(int)(Math.random()*10000000)+"',"
							+ "'"+ format.format( new Date( begin + (long)(Math.random() * (end - begin)) )) +"',"
							+ "'2051-01-01',"
							+"'"+((Math.random()>0.9)?"20":"10")+"',"
							+"'"+((Math.random()>0.9)?"20":"10")+"',"
							+ "'../file/upload/photo/company/" +(int)(Math.random()*30+1)+ ".jpg',"
							+ "'"+ id+"" +"','admin','10'"
							+ ")";

					sql_index++;
					statement2.executeUpdate(sql_insert_building);
					
					if(sql_index>=30000){
						sql_index = 0;
						statement2.executeUpdate("COMMIT;");
						statement2.executeUpdate("START TRANSACTION;");
					}
				}				
			}
	        statement2.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_company") );
	        statement2.executeUpdate("COMMIT;");			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return a_return;
	}	
	
	
	public static void main(String args[]) throws SQLException{
		government_company.data4test();
//		government_company.upload("D:/eclipse_workspace/jee/myapp/WebContent/file/developer/tables_community.xls", "admin");
	}	
}
