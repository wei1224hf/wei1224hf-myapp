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

public class oa_work {
	
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
				if(basic_user.checkPermission(executor, "500201", session)){
					String sortname = "oa_work.time";
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
						,user_type
						,executor
						,group_code
						,sortname
						,sortorder
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
			}
			else if(functionName.equals("examine")){
				if(basic_user.checkPermission(executor, "120223", session)){
					t = examine(
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
					t = download(
						 (String) request.getParameter("search")
						,(String) request.getParameter("pagesize")
						,(String) request.getParameter("page")
						,user_type
						,executor
						,group_code
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
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement statement = null;
		ResultSet rs = null;
		ArrayList a = null;		
		
		try {
			statement = conn.createStatement();
			String sql = "select code,name as value from basic_group where code like  '2102__' order by code";
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
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_work__type' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_work__type", a);		
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_work__types' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_work__types", a);				
			
			sql = "select code,extend4 as value from basic_memory where extend5 = 'oa_work__status' order by code";
			rs = statement.executeQuery(sql);
			a = new ArrayList();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rs.getString("code"));
				t.put("value", rs.getString("value"));			
				a.add(t);
			}
			t_return.put("oa_work__status", a);				
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
	
	public static Hashtable remove(String ids,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			stmt = conn.createStatement();
			System.out.println(ids);
			String[] id = ids.split(",");
			String sql = "";
			for(int i=0;i<id.length;i++){
				sql = "delete from oa_work where id = '"+id[i]+"' ;";
				System.out.println(sql);
				stmt.executeUpdate(sql);
			}		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
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
	
	public static Hashtable examine(String ids,String executor) {
		Hashtable t_return = new Hashtable();
		Statement statement;
		System.out.println(ids);
		String[] id = ids.split(",");
		String sql = "";
		try {
			statement = tools.getConn().createStatement();
			for(int i=0;i<id.length;i++){
				sql = "update oa_work set status = '20' where id = '"+id[i]+"' ;";
				System.out.println(sql);
				statement.executeUpdate(sql);
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		t_return.put("status", "1");
		t_return.put("msg", "ok");
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
		String where = " where oa_work.creater_code = '"+executor+"' ";
		//组管理员
		if(user_groups.contains(",X1")){
			where = " where oa_work.creater_group_code = '"+user_group+"'  ";
		}
		//系统用户
		if(user_type.equals("10"))where = " where 1=1 ";

		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("time_start")){
				where += " and time > '"+value+"'";
			}
			if(key.equals("time_stop")){
				where += " and time < '"+value+"'";
			}
			if(key.equals("type")){
				where += " and type = '"+value+"'";
			}
			if(key.equals("status")){
				where += " and status = '"+value+"'";
			}
			if(key.equals("plan")){
				where += " and plan = '"+value+"'";
			}		
			if(key.equals("creater_code")){
				where += " and creater_code = '"+value+"'";
			}		
			if(key.equals("creater_group_code")){
				where += " and creater_group_code like '"+value+"%'";
			}				
		}
		return where;
	}
	
	public static Hashtable view(String id) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		try {
			
			stmt = conn.createStatement();
			String sql = tools.getConfigItem("oa_work__view").replace("__id__", "'"+id+"'");
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
			
		} catch (SQLException e) {
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
	
	public static Hashtable grid(
			 String search
			,String pagesize
			,String pagenum
			,String user_type
			,String executor
			,String group_code
			,String sortname
			,String sortorder) {
		
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;	
		
		String sql = tools.getConfigItem("oa_work__grid");
		String sql_orderby = " order by "+sortname+" "+sortorder;
		String where = oa_work.search(search,executor);		
		sql += where + sql_orderby + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
		
		try {
			ArrayList a = new ArrayList();
			stmt = conn.createStatement();
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
			
			String sql_total = "select count(*) as count_ from oa_work "+where;

			rset = stmt.executeQuery(sql_total);
			rset.next();
			t_return.put("Total", rset.getString("count_"));

		} catch (SQLException e) {
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
	
	public static Hashtable upload(String path,String executor){
		return null;
	}	

	public static Hashtable download(String search,String pagesize,String pagenum,String user_type,String executor,String group_code) {
			Hashtable t_return = new Hashtable();
			String sql = "";
			String where = " where 1=1 ";
			sql = tools.getConfigItem("oa_work__grid");
			if(!user_type.equals("10")){
				where = " where creater_code = '"+executor+"' ";
			}
			String sql_orderby = " order by oa_work.time asc";
			Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
			for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = search_t.get(key);
				if(key.equals("name")){
					where += " and oa_work.name like '%"+value+"%'";
				}
				if(key.equals("type")){
					where += " and oa_work.type = '"+value+"'";
				}
				if(key.equals("status")){
					where += " and oa_work.status = '"+value+"'";
				}
				if(key.equals("zone_10")){
					where += " and oa_work.code like '"+value+"%'";
				}
			}
			
			sql += where + sql_orderby + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
			Statement statement = null;
			ResultSet rs = null;
			ArrayList a = new ArrayList();
			
			WritableWorkbook book;
			int i = 0;
			String filename = String.valueOf(Math.random()*1000);

			try {
				statement = tools.getConn().createStatement();
				rs = statement.executeQuery(sql);
				String thepath = tools.getConfigItem("APPPATH")+"\\file\\download\\"+filename+".xls";
				System.out.println(thepath);
				book = Workbook.createWorkbook(new File(thepath));
				WritableSheet sheet = book.createSheet("data_oa_work", 0);
				
				sheet.addCell( new Label(0,0,"code"));
				sheet.addCell( new Label(1,0,"code_from"));
				sheet.addCell( new Label(2,0,"plan"));
				sheet.addCell( new Label(3,0,"title"));
				sheet.addCell( new Label(4,0,"time"));
				sheet.addCell( new Label(5,0,"hour"));
				sheet.addCell( new Label(6,0,"content"));
				sheet.addCell( new Label(7,0,"businesstype"));
				sheet.addCell( new Label(8,0,"type"));
				sheet.addCell( new Label(9,0,"status"));
				
				while (rs.next()) {			
					i++;

					sheet.addCell(new Label(0,i,rs.getString("code")));
					sheet.addCell(new Label(1,i,rs.getString("code_from")));
					sheet.addCell(new Label(2,i,rs.getString("plan")));
					sheet.addCell(new Label(3,i,rs.getString("title")));
					sheet.addCell(new Label(4,i,rs.getString("time")));
					sheet.addCell(new Label(5,i,rs.getString("hour")));
					sheet.addCell(new Label(6,i,rs.getString("content")));
					sheet.addCell(new Label(7,i,rs.getString("businesstype")));
					sheet.addCell(new Label(8,i,rs.getString("type")));
					sheet.addCell(new Label(9,i,rs.getString("status")));
				}				
				book.write();		
				book.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
	
	public static int index_commit = 0;
	public static int index = 0;
	public static void data4test(int page){
		
		Statement statement = null;
		Statement statement2 = null;
		Statement statement3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection conn = tools.getConn();
		try {
			statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement3 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
			statement2 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
//			statement2.executeUpdate("delete from oa_work where code like '"+tools.getConfigItem("ZONE")+"%'");
			statement2.executeUpdate("START TRANSACTION;");

	        int id__oa_work = tools.getTableId("oa_work");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			int p_start = (page-1)*10000+1;
			if(page==0)p_start = 1;
			String sql_plan = "SELECT oa_plan.`code`,oa_plan.plan_time_start,oa_plan.plan_time_stop,basic_user.username,basic_user.group_code FROM oa_plan INNER JOIN basic_user ON oa_plan.creater_group_code = basic_user.group_code limit "+p_start+",9500 ";
			int plan_work_count = 0;
			rs = statement.executeQuery(sql_plan);	
			int div_ = 0;
			while (rs.next()) {			
				
				String code_plan = rs.getString("code");
				String plan_time_start = rs.getString("plan_time_start");
				String plan_time_stop = rs.getString("plan_time_stop");
				String code_user = rs.getString("username");
				String group_code = rs.getString("group_code");
				
				String code_user_a[] = code_user.split("-");
				
				long begin = (format.parse(plan_time_start)).getTime();
				long end = (format.parse(plan_time_stop)).getTime();
				
	
				int count_user = 5; 
				long div = (end-begin)/count_user;
				if(code_user_a[code_user_a.length-1].equals("0")){
					div_=0;
					plan_work_count = 0;
				}
				


					long thedate_s = begin + div_*div;
					long thedate_e = begin + (div_+1)*div;
//					System.out.println(code_plan+" "+plan_time_start+" "+plan_time_stop+" "+code_user+" "+group_code);
					
					int work_u = 0;
					for(long i=thedate_s;i<=thedate_e;i+=86400000){
						plan_work_count ++;
						work_u++;
						if(work_u>=7)break;
						id__oa_work ++;
						oa_work.index_commit ++;
						String code_work = code_plan+"--"+(1000+plan_work_count);
						String sql_insert = "insert into oa_work(code,code_from,plan,title,address,time,hour,content,businesstype"
								+ ",id,creater_code,updater_code,creater_group_code,type,status) values ("
								+" '"+code_work+"'"
								+",'0'"
								+",'"+code_plan+"'"
								+",'工作记录"+(int)(Math.random()*100000)+"'"
								+",'地点"+(int)(Math.random()*100000)+"'"
								+",'"+format.format( i )+"'"
								+",'"+(int)(Math.random()*8+1)+"'"
								+",'工作内容"+(int)(Math.random()*100000)+"'"
								+",'1'"
								
								+ ",'"+id__oa_work+"'"
								+ ",'"+code_user+"'"
								+ ",'0'"
								+ ",'"+group_code+"'"
								+ ",'"+(int)(Math.random()*4+1)+"0'"
								+ ",'"+(int)(Math.random()*4+1)+"0'"
								+ "); ";
//						System.out.println(sql_insert);
						statement2.executeUpdate(sql_insert);
						
						if(oa_work.index_commit>=15000){
							oa_work.index_commit = 0;
							statement2.executeUpdate("COMMIT;");
							statement2.executeUpdate("START TRANSACTION;");
						}
					}
					
					div_++;
			}
			statement2.executeUpdate( tools.getConfigItem("basic_memory__id_update").replace("__code__", "oa_work") );
	        statement2.executeUpdate("COMMIT;");			
		} catch (SQLException e1) {
			System.out.println(page);
			e1.printStackTrace();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}		
	}	
	
	public static void main(String args[]) throws SQLException{
		oa_work.data4test(1);
		oa_work.data4test(2);
		oa_work.data4test(3);
		oa_work.data4test(4);
		oa_work.data4test(5);
		oa_work.data4test(6);
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		long l1;
//		try {
//			l1 = (format.parse("2000-01-01")).getTime();
//			long l2 = (format.parse("2000-01-02")).getTime();
//			System.out.println(l2-l1);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}	
}
;