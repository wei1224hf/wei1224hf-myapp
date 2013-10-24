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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.google.gson.Gson;

public class basic_group {
	
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
				if(basic_user.checkPermission(executor, "120101", session)){

					String sortname = "code";
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
				}else{
					t.put("action", "120101");
				}
			}else if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "120121", session)){
					t = add(
							(String)request.getParameter("data"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("modify")){
				if(basic_user.checkPermission(executor, "120122", session)){
					t = modify(
							(String)request.getParameter("data"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("view")){
				t = view(
						(String)request.getParameter("code")						
						);							
			}else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120123", session)){
					t = remove(
							(String)request.getParameter("codes"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("permission_set")){
				t.put("action", "120140");
				if(basic_user.checkPermission(executor, "120140", session)){
					t = permission_set(
							 (String)request.getParameter("code")
							,(String)request.getParameter("codes")
							,(String)request.getParameter("cost_")
							,(String)request.getParameter("credits_")
							);
				}				
			}else if(functionName.equals("permission_get")){
				t.put("action", "120140");
				if(basic_user.checkPermission(executor, "120140", session)){
					t = permission_get(
							(String)request.getParameter("code")
							);
				}				
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}
			

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			out = e.toString();
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
			String sql = "select code,value from basic_parameter where reference = 'basic_group__type' and code not in ('1','9')  order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'basic_group__status' ";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("status", a);	
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
			,String executor
			,String sortname
			,String sortorder) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		String where = " where 1=1 ";
		String sql = tools.getConfigItem("basic_group__grid");
		Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and type = '"+value+"'";
			}		
			if(key.equals("code")){
				where += " and ( ( code like '"+value+"__' ) or (code = '"+value+"') )";
			}			
		}
		sql += where + " order by "+sortname+" "+sortorder+" limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList a = new ArrayList();
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
			t_return.put("Rows", a);	
			String sql_total = "select count(*) as Total from basic_group "+where;

			rs = stmt.executeQuery(sql_total);
			rs.next();
			t_return.put("Total", rs.getString("Total"));			
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
	
	public static Hashtable add(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;

		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
	
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}

		e = t_data.keys();
		String keys = "insert into basic_group (";
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
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			t_return.put("status", "1");
			t_return.put("msg", "ok");	
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return t_return;
	}
	
	public static Hashtable remove(String codes,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String[] code = codes.split(",");
			String sql = "";
			for(int i=0;i<code.length;i++){
				sql = "delete from basic_group where code = '"+code[i]+"' ;";
				stmt.executeUpdate(sql);
			}		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		String code = (String) t_data.get("code");
		t_data.remove("code");
		
		String sql = "";
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}
		
		e = t_data.keys();
		sql = "update basic_group set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where code = '"+code+"' ";
		
		System.out.println(sql);		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);				
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }		

		return t_return;
	}
	
	public static Hashtable view(String code) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = "select * from basic_group where code = '"+code+"'";
			rset = tools.getConn().createStatement().executeQuery(sql);
			rset.next();
			Hashtable t_data = new Hashtable();
			ResultSetMetaData m = rset.getMetaData();
			for(int i=1;i<=m.getColumnCount();i++){
				if(rset.getString(m.getColumnLabel(i)) != null){
					t_data.put(m.getColumnLabel(i), rset.getString(m.getColumnLabel(i)));
				}else{
					t_data.put(m.getColumnLabel(i), "-");
				}
			}
			
			t_return.put("data",t_data);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }	

		return t_return;
	}	
	
	public static Hashtable permission_set(String group_code,String permission_codes,String cost_,String credits_) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = tools.getConn().createStatement();
			String sql = "delete from basic_group_2_permission where group_code = '"+group_code+"' ";
			stmt.executeUpdate(sql);
			
			String[] codes = permission_codes.split(",");
			String[] cost = cost_.split(",");
			String[] credits = credits_.split(",");		
			for(int i=0;i<codes.length;i++){
				sql = "insert into basic_group_2_permission (group_code,permission_code,cost,credits) values ( '"+group_code+"','"+codes[i]+"','"+cost[i]+"','"+credits[i]+"' ); ";
				stmt.executeUpdate(sql);
			}	
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return t_return;
	}	
	
	public static Hashtable permission_get(String code){
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getConfigItem("basic_group__permission_get").replace("__group_code__", "'"+code+"'");
			rset = stmt.executeQuery(sql);	
			System.out.println(sql);
			ArrayList array = new ArrayList();
			while (rset.next()) {		
				ResultSetMetaData rsData = rset.getMetaData();	
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				if(rset.getString("cost") != null){
					t.put("ischecked", 1);
				}
				array.add(t);
			}	
			array = tools.list2Tree(array);	
			
			t_return.put("permissions", array);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}
	
	/**
	 * only for test
	 * */
	public static Hashtable upload(String path,String executor) {
		Hashtable t_return = new Hashtable();
		Statement stmt = null;
		try {
			stmt = tools.getConn().createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return t_return;
		}
		String filePath = path;
		InputStream fs = null;
		Workbook workBook = null;
		Sheet sheet = null;
		int columns,rows = 0;
		String[] sqls = null;
		
		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	 		t_return.put("status", "2");
	 		t_return.put("msg", "Excel path wrong");
	 		return t_return;
        } catch (BiffException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }		
		
        try {
        	String[] sql_user = new String[9]; 
    		sql_user[0] = "delete from basic_user;";
    		sql_user[1] = "insert into basic_user(username,password,group_code,group_all,id,type,status) values ('admin',md5('admin'),'10','10',1,'10','10');";
    		sql_user[2] = "insert into basic_user(username,password,group_code,group_all,id,type,status) values ('guest',md5('guest'),'99','99',2,'10','10');";
    		sql_user[3] = "delete from basic_group_2_user;"; 
    		sql_user[4] = "insert into basic_group_2_user(user_code,group_code) values ('admin','10');";	
    		sql_user[5] = "insert into basic_group_2_user(user_code,group_code) values ('guest','99');";
    		
    		sql_user[6] = "delete from basic_group_2_permission;"; 
    		sql_user[7] = "delete from basic_permission;"; 
    		sql_user[8] = "delete from basic_group "; 
    		
    		for(int i=0;i<9;i++){
    			stmt.executeUpdate(sql_user[i]);
    		}			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

        sheet = workBook.getSheet("data_basic_group");//这里只取得第一个sheet的值，默认从0开始
        //System.out.println(sheet.getColumns());//查看sheet的列
        rows = sheet.getRows();
        if(rows>20000){
    		t_return.put("status", "2");
    		t_return.put("msg", "row count must be less than 20000 , your rows:"+rows);
    		return t_return;
        }
        Cell cell = null;//就是单个单元格
        sqls = new String[rows-1];
        
        try {
			stmt.executeUpdate("START TRANSACTION;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        
        for(int i=1;i<rows;i++){
        	
        	sqls[i-1] = "insert into basic_group(name,code,type,status) values ('" 
			+sheet.getCell(0,i).getContents()+"','"
			+sheet.getCell(1,i).getContents()+"','"
			+sheet.getCell(2,i).getContents()+"','"
			+sheet.getCell(3,i).getContents()+"'"
			+");";
			//System.out.println(sqls[i-1]);
			
			try {
				System.out.println("line: "+(i+1));
				stmt.executeUpdate(sqls[i-1]);	
			} catch (SQLException e) {
				e.printStackTrace();
				
//		        try {
//					stmt.executeUpdate("ROLLBACK;");
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//	    		t_return.put("status", "2");
//	    		t_return.put("msg", "Wrong data , check line "+(i+1));
//	    		return t_return;
			}	
        }
        
        
        sheet = workBook.getSheet("data_basic_permission");//这里只取得第一个sheet的值，默认从0开始
        System.out.println(sheet.getColumns());//查看sheet的列
        rows = sheet.getRows();
        sqls = new String[rows-1];    

        for(int i=1;i<rows;i++){
        	
        	sqls[i-1] = "insert into basic_permission (name,type,code,icon,path) values('" 
			+sheet.getCell(0,i).getContents().trim()+"','"
			+sheet.getCell(1,i).getContents()+"','"
			+sheet.getCell(2,i).getContents()+"','"
			+sheet.getCell(3,i).getContents()+"','"
			+sheet.getCell(4,i).getContents()+"'"
			+");";
			System.out.println(sqls[i-1]);
			
			try {
				System.out.println("line: "+(i+1));
				stmt.executeUpdate(sqls[i-1]);	
			} catch (SQLException e) {
				e.printStackTrace();
				
		        try {
					stmt.executeUpdate("ROLLBACK;");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	    		t_return.put("status", "2");
	    		t_return.put("msg", "Wrong data , check line "+(i+1));
	    		return t_return;
			}	
        }
        

        sheet = workBook.getSheet("data_basic_group_2_permission");//这里只取得第一个sheet的值，默认从0开始
        columns = sheet.getColumns();
        rows = sheet.getRows();

        sqls = new String[columns*rows];    
        
        for(int i=2;i<rows;i++){
        	String permission = sheet.getCell(1,i).getContents();
        	for(int i2 = 2;i2<columns;i2++){
        		String group = sheet.getCell(i2,1).getContents();
        		if( sheet.getCell(i2,i).getContents() != null && sheet.getCell(i2,i).getContents().equals("1") ){
        			sqls[(i2-1)*rows+i] = "insert into basic_group_2_permission (permission_code,group_code) values('"+permission+"','"+group+"');";
        			
        			try {
        				System.out.println("line: "+(i+1)+" column: "+(i2+1));
        				stmt.executeUpdate(sqls[(i2-1)*rows+i]);	
        			} catch (SQLException e) {
        				e.printStackTrace();
        				
        		        try {
        					stmt.executeUpdate("ROLLBACK;");
        				} catch (SQLException e1) {
        					e1.printStackTrace();
        				}
        	    		t_return.put("status", "2");
        	    		t_return.put("msg", "Wrong data , check line "+(i+1));
        	    		return t_return;
        			}	
        		}
        	}
        }
        
        try {        
	        stmt.executeUpdate("COMMIT;");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}  

        try {
			stmt.executeUpdate("DELETE from basic_group_2_permission where basic_group_2_permission.group_code not in('10','99');");
			stmt.executeUpdate("insert into basic_group_2_permission (permission_code,group_code) SELECT basic_permission.`code` as permission_code ,basic_group.`code` as group_code FROM basic_permission , basic_group WHERE (basic_permission.`code` like '50%' or basic_permission.`code` like '11%' or basic_permission.`code` like '52%' )  AND basic_group.`code` >= '30' and basic_group.`code` <> '99' and basic_permission.`code` not like '%9_'; ");
			stmt.executeUpdate("insert into basic_group_2_permission (permission_code,group_code) SELECT basic_permission.`code` as permission_code ,'X1' as group_code FROM basic_permission  WHERE (basic_permission.`code` like '50%' or basic_permission.`code` like '11%' or basic_permission.`code` like '52%' )  AND basic_permission.`code` like '%9_'; ");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	
	//根据地图数据模拟组织
	public static void data4test(){
		try {			
			Connection conn = tools.getConn();
			Connection conn2 = tools.getConn();
			
			Statement st = conn.createStatement();
			Statement st2 = conn2.createStatement();
			st2.executeUpdate("START TRANSACTION;");
			String sql = "select code,name from gis_polygon_12 union select code,name from gis_polygon_14 union select code,name from gis_polygon_10 union select code,name from gis_polygon_8 order by code";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				String sql_insert = "insert into basic_group(code,name,status,type) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','10','11');";
				st2.executeUpdate(sql_insert);
				String[] str = {"行政中心","卫生院"};
				String[] str2 = {"社会事务科","党建科","民政科"};
				for(int i=0;i<str.length;i++){
					String sql_insert2 = "insert into basic_group(code,name,status,type) values ('"+rs.getString("code")+"-100"+i+"','"+str[i]+(int)(Math.random()*100000)+"','10','20');";
					st2.executeUpdate(sql_insert2);
					for(int j=0;j<str2.length;j++){
						String sql_insert3 = "insert into basic_group(code,name,status,type) values ('"+rs.getString("code")+"-100"+i+"-1"+j+"','"+str2[j]+(int)(Math.random()*100000)+"','10','21');";
						st2.executeUpdate(sql_insert3);
					}
				}				
			}
			st2.executeUpdate("COMMIT;");
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//模拟一个县级市
	//市的行政区划编码为6位
	//每一级下级为6个到10个子区划
	//市级区划中: 行政中心,公安,法院,检察院,
	public static void data4test2(){
		String code__zone_6 = tools.getConfigItem("ZONE");//余姚
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String code = "";
		String name = "";
		String chinese_number = "一二三四五六七八九十";
		
		try {
			stmt = conn.createStatement();
			
			//根据国标 4754 国名经济分类标准 , 国家权利机构的编码为 942 国家行政机构
			//9421 综合事务管理机构  9423 公共安全 9424 社会事务 9425 经济管理 9427 行政监督
			//9431 人民法院 9432 人民检察院
			//在系统中,组的分类: 10 系统 20 节点 30 单位 40 部门
			//对于政府部门的组编码: 前两位表示系统分类类型 后几位参考国民经济编码 后两位为随意递增编码
			String departments__zone_6[] = 			{"行政中心","公安","法院","工商局","城市管理","大医院A","大医院B","社保","国土规划局","房产管理局"};
			String departments__zone_6__type[] = 	{"3010-9421-10","3010-9423-10","3010-9431-10","3010-9425-10","3010-8021-10","3010-8511-10","3010-8511-10","3010-9424-10","3010-9424-11","3010-9424-12"};
			
			String sql_delete = "delete from basic_group where code like '"+code__zone_6+"%' and code <>  '"+code__zone_6+"';";
			stmt.execute(sql_delete);
			
			stmt.execute("START TRANSACTION; ");
			for(int i=0;i<departments__zone_6.length;i++){
				String code__z6dp = code__zone_6+"-"+(10+i);
				
				String sql_zone6_dep = "insert into basic_group ("
					+"name,"
					+"code,"
					+"count_users,"
					+"type,"
					+"status,"
					+"remark,"
					+"chief,"
					+"chief_cellphone,"
					+"phone"
					+")values("
					+"'余姚市"+departments__zone_6[i]+"',"
					+"'"+code__z6dp+"',"
					+"0,"
					+"'"+departments__zone_6__type[i]+"',"
					+"10,"
					+"'说明描述,内容应该很长很长,含有HTML标签,比如回车<br/>还有图片<img src=\"http://img.baidu.com/img/iknow/docshare/icon_s_vip.png\"/>',"
					+"'"+tools.randomName()+"',"
					+"'13456"+(int)(Math.random()*1000000+1000000)+"',"
					+"'111111111'"
					+")";
				//System.out.println(sql_zone6_dep);
				stmt.execute(sql_zone6_dep);
			}
			
			//镇或街道,6个到10个
			int count__zone_8 = (int) Math.floor(Math.random()*4+6);
			for(int i=0;i<count__zone_8;i++){
				
				String code__z8 = code__zone_6 + (10+i);
				//先插入行政区划
				name = chinese_number.charAt((int) (chinese_number.length()*Math.random())) 
					+""+chinese_number.charAt((int) (chinese_number.length()*Math.random()))
					+""+chinese_number.charAt((int) (chinese_number.length()*Math.random())) +(Math.random()>0.5?"镇":"街道");
				String sql_insert_zone8 = "insert into basic_group(name,code,type) values ('"+name+"','"+code__z8+"','2010');";
				stmt.execute(sql_insert_zone8);				
				
				String departments__zone_8[] = {"行政中心","派出所","法院","工商所","卫生院"};
				String departments__zone_8__type[] = {"3010-9421-10","3010-9423-10","3010-9431-10","3010-9425-10","3010-8511-10"};
				
				for(int i1=0;i1<departments__zone_8.length;i1++){
					String code__z8dp = code__z8+"-"+(10+i1);
					
					String sql_zone8_dep = "insert into basic_group ("
						+"name,"
						+"code,"
						+"count_users,"
						+"type,"
						+"status,"
						+"remark,"
						+"chief,"
						+"chief_cellphone,"
						+"phone"
						+")values("
						+"'"+name+departments__zone_8[i1]+"',"
						+"'"+code__z8dp+"',"
						+"0,"
						+"'"+departments__zone_8__type[i1]+"',"
						+"10,"
						+"'说明描述,内容应该很长很长,含有HTML标签,比如回车<br/>还有图片<img src=\"http://img.baidu.com/img/iknow/docshare/icon_s_vip.png\"/>',"
						+"'"+tools.randomName()+"',"
						+"'13456"+(int)(Math.random()*1000000+1000000)+"',"
						+"'111111111'"
						+")";
					stmt.execute(sql_zone8_dep);
				}
				
				//社区或村庄,7个到12个
				int count__zone_10 = (int) Math.floor(Math.random()*5+7);
				
				for(int i2=0;i2<count__zone_10;i2++){
					
					String code__z10 = code__z8 + (10+i2);
					//先插入行政区划
					name = chinese_number.charAt((int) (chinese_number.length()*Math.random())) 
						+""+chinese_number.charAt((int) (chinese_number.length()*Math.random()))
						+""+chinese_number.charAt((int) (chinese_number.length()*Math.random())) +(Math.random()>0.5?"村":"小区");
					String sql_insert_zone10 = "insert into basic_group(name,code,type) values ('"+name+"','"+code__z10+"','2010');";
					stmt.execute(sql_insert_zone10);				
					
					String departments__zone_10[] = {"行政中心"};
					String departments__zone_10__type[] = {"3010-9421-10"};
					
					for(int i3=0;i3<departments__zone_10.length;i3++){
						String code__z10dp = code__z10+"-"+(10+i3);
						
						String sql_zone8_dep = "insert into basic_group ("
							+"name,"
							+"code,"
							+"count_users,"
							+"type,"
							+"status,"
							+"remark,"
							+"chief,"
							+"chief_cellphone,"
							+"phone"
							+")values("
							+"'"+name+departments__zone_10[i3]+"',"
							+"'"+code__z10dp+"',"
							+"0,"
							+"'"+departments__zone_10__type[i3]+"',"
							+"10,"
							+"'说明描述,内容应该很长很长,含有HTML标签,比如回车<br/>还有图片<img src=\"http://img.baidu.com/img/iknow/docshare/icon_s_vip.png\"/>',"
							+"'"+tools.randomName()+"',"
							+"'13456"+(int)(Math.random()*1000000+1000000)+"',"
							+"'111111111'"
							+")";
						stmt.execute(sql_zone8_dep);
					}
				}
			}
			
			stmt.execute("COMMIT; ");
			
			
			//每个单位4个到6个部门(科室)
			String sql_select = "select * from basic_group where type like '30%'";
			Statement stmt2 = conn.createStatement();
			rset = stmt2.executeQuery(sql_select);
			stmt.execute("START TRANSACTION; ");
			while(rset.next()){
				int count = (int)(Math.random()*3+5);
				for(int i=0;i<count;i++){
					String sql_division = "insert into basic_group ("
							+"name,"
							+"code,"
							+"count_users,"
							+"type,"
							+"status,"
							+"remark,"
							+"chief,"
							+"chief_cellphone,"
							+"phone"
							+")values("
							+"'"+rset.getString("name")+"科室"+(int)(Math.random()*10000)+"',"
							+"'"+rset.getString("code")+"-"+(10+i)+"',"
							+"0,"
							+"'40',"
							+"10,"
							+"'说明描述,内容应该很长很长,含有HTML标签,比如回车<br/>还有图片<img src=\"http://img.baidu.com/img/iknow/docshare/icon_s_vip.png\"/>',"
							+"'"+tools.randomName()+"',"
							+"'13456"+(int)(Math.random()*1000000+1000000)+"',"
							+"'111111111'"
							+")";
						stmt.execute(sql_division);
				}
			}
			stmt.execute("COMMIT; ");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
	}

	public static void main(String args[]) throws SQLException{
//		System.out.println(new Gson().toJson(basic_group.grid("{}", "20", "1", "1")));
//		System.out.println(new Gson().toJson(basic_group.loadConfig()));
//		System.out.println(new Gson().toJson(basic_group.view("1")));	
//		System.out.println(new Gson().toJson(basic_group.permission_get("10")));		
//		System.out.println(new Gson().toJson(basic_group.remove("10","admin")));	
//		basic_group.upload( tools.getConfigItem("APPPATH")+"/file/developer/tables_community_data_2.xls", "admin");
//		basic_group.data4test();
//		System.out.println( 
//				new Gson().toJson( (basic_group.grid("{}", "10", "10", "admin", "id", "desc") ) )
//				);
		basic_group.data4test2();
//		basic_user.data4test();
//		oa_plan.data4test(1);
	}	
}