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

public class government_family {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			
			if (functionName.equals("grid")) {		
				if(basic_user.checkPermission(executor, "520201", session)){
					String sortname = "government_family.id";
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
				if(basic_user.checkPermission(executor, "520221", session)){

					t = add(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}else if(functionName.equals("modify")){
				if(basic_user.checkPermission(executor, "520222", session)){
					t = modify(
							(String)request.getParameter("data"),
							executor
							);
				}				
			}
			else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "520223", session)){
					t = remove(
							(String)request.getParameter("ids"),
							executor
							);
				}				
			}
			else if(functionName.equals("view")){
				t = view((String) request.getParameter("id"));		
			}
			else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}
			else if(functionName.equals("download")){
				if(basic_user.checkPermission(executor, "520212", session)){
					String sortname = "government_family.id";
					String sortorder = "asc";
					if( request.getParameter("sortname") != null ){
						sortname = (String) request.getParameter("sortname");
					}
					if( request.getParameter("sortorder") != null ){
						sortorder = (String) request.getParameter("sortorder");
					}				
					t = download(
						 (String) request.getParameter("search")
						,(String) request.getParameter("pagesize")
						,(String) request.getParameter("page")
						,executor
						,sortname
						,sortorder
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
	
	public static Hashtable loadConfig() {
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;	
		Hashtable t_return = new Hashtable();
		ArrayList a = null;
		String sql = "";
		
		try {
			String zone = tools.getConfigItem("ZONE4");
			sql = "select code,name as value from basic_group where code like  '"+zone+"__' order by code";
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("zone_6", a);				
			
			sql = "select code,value from basic_parameter where reference = 'government_family__types' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_family__types", a);		
			
			sql = "select code,value from basic_parameter where reference = 'government_family__type' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_family__type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'government_family__types' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_family__types", a);				
			
			sql = "select code,value from basic_parameter where reference = 'government_family__status' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("government_family__status", a);	
			
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("msg", e.toString());		
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
			
		return t_return;
	}		
	
	public static Hashtable add(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		
		System.out.println(data);
		data = data.replace("null", "\"\"");
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
		String building = (String) t_data.get("building");
		t_data.remove("building");
		
		String sql_select = "select max(code) as thecode from government_family where code like '"+building+"____';";
		
		ResultSet rs = statement.executeQuery(sql_select);
		String thecode = "";
		while(rs.next()){
			thecode = rs.getString("thecode");
			if(thecode==null){
				 thecode = building+"0001";
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
			thecode = building+thecode + i;
		}
		t_data.put("code", thecode);

		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			
			t_data.put(key, "'"+value+"'");
		}
		
		String id = String.valueOf(tools.getTableId("government_family"));
		t_data.put("id", id);
		t_data.put("creater_code", "'"+executor+"'");
		t_data.put("creater_group_code", "(select group_code from basic_group_2_user where user_code = '"+executor+"' order by group_code limit 1 )");

		e = t_data.keys();
		String keys = "insert into government_family (";
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
		
		return t_return;
	}	
	
	public static Hashtable remove(String ids,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		String[] id = ids.split(",");
		String sql = "";
		for(int i=0;i<id.length;i++){

			sql = "delete from government_family where id = '"+id[i]+"' ;";
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
		sql = "update government_family set ";
	
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
		String sql = tools.getConfigItem("government_family__view").replace("__id__", "'"+id+"'");
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
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and government_family.name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and government_family.type = '"+value+"'";
			}
			if(key.equals("types")){
				where += " and concat(',',"+key+",',') like '%,"+value+",%' ";
			}			
			if(key.equals("status")){
				where += " and government_family.status = '"+value+"'";
			}
			if(key.equals("zone_10")){
				where += " and government_family.code like '"+value+"%'";
			}
			if(key.equals("building")){
				where += " and government_family.code like '"+value+"%'";
			}		
		}
		
		if(group_code.length()>2){
			String[] group_code_ = group_code.split("-");
			where += " and government_family.code like '"+group_code_[0]+"%'";
		}
		
		return where;
	}
	
	/**
	 * grid 函数,一般要根据 用户类型 来做不同的列处理
	 * 但, government_family 的列表操作,只有可能是 超级管理员 ,所以不做处理
	 * */
	public static Hashtable grid(
			 String search
			,String pagesize
			,String pagenum
			,String executor
			,String sortname
			,String sortorder) {
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;			
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		String sql = tools.getConfigItem("government_family__grid");
		String sql_orderby = " order by "+sortname+" "+sortorder;
		String where = government_family.search(search,user_type,executor,user_group);	
		sql += where + sql_orderby + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		
		try {
			statement = conn.createStatement();
			ArrayList a = new ArrayList();
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
			
			t_return.put("Rows", a);
			
			String sql_total = "select count(*) as count_ from government_family "+where;
			rs = statement.executeQuery(sql_total);
			rs.next();
			t_return.put("Total", rs.getString("count_"));
			
		} catch (SQLException e) {
			e.printStackTrace();
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

        Sheet sheet = workBook.getSheet("data_government_family");//这里只取得第一个sheet的值，默认从0开始
        System.out.println(sheet.getColumns());//查看sheet的列
        int rows = sheet.getRows();
        if(rows>2000){
    		t_return.put("status", "1");
    		t_return.put("msg", "row count must be less than 1000 , your rows:"+rows);
    		return t_return;
        }

		Hashtable t__grup_type = basic_user.getSession(executor);
		String group_code = (String) t__grup_type.get("group_code");        
        String[] sqls = new String[rows-1];

        try{
			statement = conn.createStatement();
			statement.executeUpdate("START TRANSACTION;");			
			int id = tools.getTableId("government_family");
			
	        for(int i=1;i<rows;i++){
	        	id++;        	
	        	sqls[i-1] = "insert into government_family( " +
	        			"code,name,owner,owner_person_id,time_founded,time_over,types,income,count_member,type,status,photo,id,creater_code,creater_group_code) values ('"
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
	        			+id+"','"+executor+"','"+group_code+"');";
	        	
	        	System.out.println(sqls[i-1]);

				statement.executeUpdate(sqls[i-1]);

	        }
	        
	        String Sql = tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_family") ;			
	        statement.executeUpdate(Sql );

	        statement.executeUpdate("COMMIT;");
	        
	        t_return.put("status","1");
	        t_return.put("msg","ok");
		} catch (SQLException e) {
			e.printStackTrace();
	        t_return.put("status","2");
			t_return.put("msg", e.toString());		
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
       
        workBook.close();
        try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}        

		return t_return;
	}
	
	public static Hashtable download(			 
			 String search
			,String pagesize
			,String pagenum
			,String executor
			,String sortname
			,String sortorder) {
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;	
		
		Hashtable t__user_session = basic_user.getSession(executor);	
		String user_group = (String) t__user_session.get("group_code");
		String user_type = (String) t__user_session.get("user_type");
		String user_groups = (String) t__user_session.get("groups");
		
		String sql = tools.getConfigItem("government_family__grid");
		String sql_orderby = " order by "+sortname+" "+sortorder;
		String where = government_family.search(search,user_type,executor,user_group);	
		sql += where + sql_orderby + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		
		String filename = String.valueOf(Math.random()*1000);
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			WritableWorkbook book;
			int i = 0;


				String thepath = tools.getConfigItem("APPPATH")+"\\file\\download\\"+filename+".xls";
				System.out.println(thepath);
				book = Workbook.createWorkbook(new File(thepath));
				WritableSheet sheet = book.createSheet("data_government_family", 0);		
				
				sheet.addCell( new Label(0,0,"code"));
				sheet.addCell( new Label(1,0,"name"));
				sheet.addCell( new Label(2,0,"owner"));
				sheet.addCell( new Label(3,0,"owner_person_id"));
				sheet.addCell( new Label(4,0,"time_founded"));
				sheet.addCell( new Label(5,0,"time_over"));
				sheet.addCell( new Label(6,0,"types"));
				sheet.addCell( new Label(7,0,"income"));
				sheet.addCell( new Label(8,0,"count_member"));
				sheet.addCell( new Label(9,0,"type"));
				sheet.addCell( new Label(10,0,"status"));
				sheet.addCell( new Label(11,0,"photo"));
				
				while (rs.next()) {			
					i++;

					sheet.addCell(new Label(0,i,rs.getString("code")));
					sheet.addCell(new Label(1,i,rs.getString("name")));
					sheet.addCell(new Label(2,i,rs.getString("owner")));
					sheet.addCell(new Label(3,i,rs.getString("owner_person_id")));
					sheet.addCell(new Label(4,i,rs.getString("time_founded")));
					sheet.addCell(new Label(5,i,rs.getString("time_over")));
					sheet.addCell(new Label(6,i,rs.getString("types")));
					sheet.addCell(new Label(7,i,rs.getString("income")));
					sheet.addCell(new Label(8,i,rs.getString("count_member")));
					sheet.addCell(new Label(9,i,rs.getString("type")));
					sheet.addCell(new Label(10,i,rs.getString("status")));
					sheet.addCell(new Label(11,i,rs.getString("photo")));
					
				}				
				book.write();		
				book.close();
				
		        t_return.put("status","1");
		        t_return.put("msg","ok");
		        t_return.put("file","../file/download/"+filename+".xls");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
	        t_return.put("status","2");
			t_return.put("msg", e.toString());		
		} finally {
            try { if (rs != null) rs.close(); } catch(Exception e) { }
            try { if (statement != null) statement.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		
		return t_return;
	}
	
	public static ArrayList data4test(int page){
		ArrayList a_return = new ArrayList();
		String sql_parent = "select code,type from government_building limit "+(page-1)*100000+",100000 ";
		Statement statement = null;
		Statement statement2 = null;
		try {
			statement = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement2 = tools.getConn().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			statement2.executeUpdate("START TRANSACTION;");
			ResultSet rs = statement.executeQuery(sql_parent);			
			
	        int id = tools.getTableId("government_family");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long begin_ = (format.parse("1940-01-01")).getTime();
			long end = (format.parse("2011-12-01")).getTime();	
			int sql_index = 0;
			while (rs.next()) {			
				String code = rs.getString("code");
				String type = rs.getString("type");
				int count = 0;
				if(type.equals("10")){//住房楼宇
					count = 1000+(int)(Math.random()*20+1);
				}
				if(type.equals("12") || type.equals("11") ){//农村房 别墅
					count = 1001;
				}
				if(!(type.equals("10")||type.equals("11")||type.equals("12")))continue;
				
				for(int i=1000;i<count;i++){
					long begin = (long)(begin_ +  (end - begin_)*((i-1000+1+0.0)/(count-1000)));//家庭总数逐年递增
					String types = "";
					for(int i2=0;i2<5;i2++){
						if(Math.random()>0.8)types+=i2+",";						
					}
					if(!types.equals(""))types= types.substring(0,types.length()-1);
					String f_type = "1";//核心家庭
					double d_rand = Math.random();
					if(d_rand>0.5)f_type = "2";//主干家庭
					if(d_rand>0.6)f_type = "3";//联合家庭
					if(d_rand>0.7)f_type = "4";//单亲家庭
					if(d_rand>0.8)f_type = "5";//重组家庭
					if(d_rand>0.9)f_type = "6";//丁克家庭
					if(d_rand>0.95)f_type = "7";//独居
					
					id++;
					String sql_insert_building = "insert into government_family (code,name,owner,owner_person_id,time_founded,time_over,types,income,count_member,type,status,photo,id,creater_code,creater_group_code) values ("
							+ "'"+ code+"-"+i +"',"
							+ "'家庭"+(int)(Math.random()*10000000)+"',"
							+ "'"+tools.randomName()+"',"
							+ "'"+i+"',"
							+ "'"+ format.format( new Date( begin + (long)(Math.random() * (end - begin)) )) +"',"
							+ "'2051-01-01',"
							+"'"+types+"',"
							+"'"+(int)(Math.random()*500000)+"',"
							+"'"+(int)(Math.random()*6)+"',"
							+"'"+f_type+"',"
							+"'"+((Math.random()>0.9)?"20":"10")+"',"
							+ "'../file/upload/photo/rooms/" +(int)(Math.random()*30+1)+ ".jpg',"
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
	        statement2.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "government_family") );
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
//		government_building.data4test();
		government_family.data4test(1);
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			long begin_ = (format.parse("1940-01-01")).getTime();
//			long end = (format.parse("1941-01-01")).getTime();	
//			System.out.print(end-begin_);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		;
		//select  md5( concat( md5( 'admin' ),'15' ) )
//		Gson json = new Gson();
		
//		System.out.println(new Gson().toJson(government_family.login("guest", "2c3d9d5b839bbc144cb43313d7ec3dc5", "1", "1")));
//		System.out.println(new Gson().toJson(government_family.grid("{}", "10", "1", "10")));
//		System.out.println(new Gson().toJson(government_family.logout("admin", "3dddc2a1cc04fe2a1e9d974de790ae3b")));
//		System.out.println(new Gson().toJson(government_family.getPermissionTree("admin")));
//		System.out.println(new Gson().toJson(government_family.getGroup("admin")));
//		System.out.println( json.toJson(government_family.login("admin", tools.MD5( tools.MD5("admin")+"17" ), "1.1.1.1", "x")) );
		//System.out.println( json.toJson(obj.logout("admin",  tools.MD5("jseo6usfqh6df0plvopiflr20ryhlhmo11") )) );
		//System.out.println( json.toJson(obj.grid("{}",12,0,1) )) ;
//		System.out.println( new Gson().toJson(government_family.loadConfig()) );		
		//obj.importExcel("D:/workSpace/webs/ligerJAVA/WebContent/file/download/highschool/government_family.xls");
//		System.out.println(government_family.checkPermission("admin", "121111","9dc0e41ddb39e97ed37fed6b56cade5f"));
//		System.out.println(obj.checkPermission("admin", "11"));
//		System.out.println(government_family.updateSession("admin", "8335c8a44e325d04b1d83046c6537969"));	
//		System.out.println(government_family.view("1"));	
//		government_family.upload("D:\\workSpace\\webs\\myapp\\WebContent\\file\\developer\\tables_community.xls", "admin");
//		government_family.upload("D:\\workSpace\\webs\\myapp\\WebContent\\file\\developer\\tables_community.xls", "admin");
//		Hashtable t = government_family.download("{}", "10", "1", "10");
	}	
}
