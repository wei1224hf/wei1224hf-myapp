package myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.google.gson.Gson;

public class tools {
	
	public static Connection getMySQLConn() {
		Connection conn = null;
		try {
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);
			String url = tools.getConfigItem("DB_URL");		
			conn = DriverManager.getConnection(url);			
			conn.createStatement().execute("SET NAMES UTF8");
			conn.createStatement().execute("set time_zone='+8:00';");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  

		return conn;		
	}
	
	public static Connection getPostGreSqlConn() {
		Connection conn = null;
		try {
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			String url = tools.getConfigItem("DB_URL");		
			conn = DriverManager.getConnection(url);				
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  

		return conn;		
	}	
	
	/**
	 * 系统并发大,先优化 链接方式
	 * 然后再搞搞集群,分布式什么的
	 * 
	 * 但是.....
	 * 
	 * 次奥!!DBCP链接池实在是搞不懂,他娘的
	 * TODO
	 * */
	public static BasicDataSource DS = null;
	public static void initDataSource(){
		String driver = "com.mysql.jdbc.Driver";
		String url = tools.getConfigItem("DB_URL");
		String user = tools.getConfigItem("DB_UNM");
		String password = tools.getConfigItem("DB_PWD");	
		
	    BasicDataSource ds = new BasicDataSource();  
        ds.setDriverClassName(driver);  
        ds.setUsername(user);  
        ds.setPassword(password);  
        ds.setUrl(url);  
        ds.setInitialSize(5); // 初始的连接数；  
        ds.setMaxActive(10);  
        ds.setMaxIdle(6);  
        ds.setMaxWait(1000);  
//        ds.setRemoveAbandoned(true);
//        ds.setRemoveAbandonedTimeout(5);
        DS = ds;  
	}
	
	public static void shutdownDataSource(){  
        BasicDataSource bds = (BasicDataSource) DS;  
        try {
			bds.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
    }	
	
	public static Connection getDBCPConn() {
		if (tools.DS != null){
			Connection conn = null;
			try {
				conn = DS.getConnection();
				conn.createStatement().execute("SET NAMES UTF8");
				conn.createStatement().execute("set time_zone='+8:00';");
			} catch (SQLException e) {
				e.printStackTrace();
			}  
	
			return conn;
		}else{
			tools.initDataSource();
			return getDBCPConn();
		}
	} 		
	
	private static String dbType = null;
	public static Connection getConn(){
		if(tools.dbType==null){
			tools.dbType = tools.getConfigItem("DB_TYPE");
		}
		
		if(tools.dbType.equals("mysql")){
			return tools.getMySQLConn();
		}
		else if(tools.dbType.equals("postgresql")){
			return tools.getPostGreSqlConn();
		}
		else{
			return null;
		}
	}

	public static HashMap il8n = null;
	public static HashMap readIl8n() {
		if (tools.il8n == null) {
			tools.il8n = new HashMap();
			String path = tools.class.getClassLoader().getResource("")
					+ "../../language/zh-cn/";
			path = "/" + path.substring(6, path.length());
			try {
				File file = new File(path);
				File[] files = file.listFiles();
				for (File fl : files) {
					if (fl.isDirectory())continue;
						
					String path2 = path + fl.getName();
					System.out.println(path2);

					BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(path2), "utf-8"));

					String line;
					HashMap current = new HashMap();
					
					String currentSecion = "nothing";

					while ((line = reader.readLine()) != null) {
						line = line.trim();
						// System.out.println(line.toString());
						if (line.matches("\\[.*\\]")) {
							currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
							// System.out.println(currentSecion);

						} else if (line.matches(".*=.*")) {
							if (current != null) {
								int i = line.indexOf('=');
								String name = line.substring(0, i);
								String value = line.substring(i + 1);
								value = value.replace("\"", "");
								// System.out.println(name+" "+value);
								current.put(name, value);
							}
						}
					}
					il8n.put(currentSecion, current);
					reader.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tools.il8n;
	}

	public static void importIl8n2DB(){
		tools.il8n = null;
		tools.readIl8n();
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute("delete from basic_memory where extend6 = 'il8n';");
			for (Iterator it = tools.il8n.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				HashMap value = (HashMap) tools.il8n.get(key);
				for (Iterator it2 = value.keySet().iterator(); it2.hasNext();) {
					String key_ = (String) it2.next();
					String value_ = (String) value.get(key_);

					String sql = "insert into basic_memory (code,extend4,extend5,extend6) values ('"
							+ key_ + "','" + value_ + "','" + key + "','il8n');";
					System.out.println(sql);
					stmt.execute(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
	}

	public static void initMemory(){
		Statement stmt = null;
		Connection conn = tools.getConn();
		String sql = "delete from basic_memory;";
		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tools.importIl8n2DB();

		sql = tools.getSQL("basic_memory__init");
		String[] sql_ = sql.split(";");
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("delete from basic_memory ;");
			for (int i = 0; i < (sql_.length - 1); i++) {
				System.out.println(sql_[i]);
				stmt.executeUpdate(sql_[i]);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
	}

	public static ArrayList list2Tree(ArrayList a_list) {
		ArrayList a_return = new ArrayList();

		for (int i = 0; i < a_list.size(); i++) {
			Hashtable t = (Hashtable) a_list.get(i);
			int len = ((String) t.get("code")).length();
			if(len==2){
				a_return.add(t);
				continue;
			}
			
			ArrayList aa = new ArrayList();
			aa.add(a_return);
			
			for(int i2=2;i2<len;i2+=2){
				ArrayList a = (ArrayList) aa.get(aa.size()-1);
				int p = a.size()-1;
				
				Hashtable item = (Hashtable) a.get(p);
				if(!item.containsKey("children")){
					item.put("children", new ArrayList());
				}
				
				aa.add(item.get("children"));
			}
			((ArrayList) aa.get(aa.size()-1)).add(t);

			for(int i3=aa.size()-1;i3>0;i3--){
			    ((Hashtable) ((ArrayList)(aa.get(i3-1))).get(((ArrayList)aa.get(i3-1)).size()-1)).put("children", aa.get(i3));
            }
			a_return = (ArrayList) aa.get(0);
		}

		return a_return;
	}

	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Document configXML = null;
	public static String configXMLFileName = "config.xml";
	public static String getConfigItem(String id) {
		String item = "";
		if (tools.configXML == null || id.equals("reLoad")) {
			tools.dbType = null;
			try {
				String path = tools.class.getClassLoader().getResource("")
						+ "../../"+tools.configXMLFileName;
				System.out.println(path);
				if(System.getProperty("os.name").contains("Windows")){
					path = path.substring(6);
				}else{
					path = path.substring(5);
				}
				File file = new File(path);
				StringBuffer buffer = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(
						new FileInputStream(file), "utf-8");
				BufferedReader br = new BufferedReader(isr);
				int s;
				while ((s = br.read()) != -1) {
					buffer.append((char) s);
				}
				tools.configXML = DocumentHelper.parseText( buffer.toString() );			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		item = tools.configXML.elementByID(id).getText();

		return item;
	}
	
	public static Document sqlXML = null;
	public static String getSQL(String id) {
		String item = "";
		if (tools.sqlXML == null) {
			try {
				String path = tools.class.getClassLoader().getResource("")
						+ "../../sql.xml";
				if(System.getProperty("os.name").contains("Windows")){
					path = path.substring(6);
				}else{
					path = path.substring(5);
				}
				File file = new File(path);
				StringBuffer buffer = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(
						new FileInputStream(file), "utf-8");
				BufferedReader br = new BufferedReader(isr);
				int s;
				while ((s = br.read()) != -1) {
					buffer.append((char) s);
				}
				tools.sqlXML = DocumentHelper.parseText( buffer.toString() );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		item = tools.sqlXML.elementByID(id).getText();
		return item;
	}
	
	
	public static void updateTableId(String table){
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			tools.getSQL("basic_memory__id_update").replace("__code__",table);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
	}
	public static int getTableId(String table){
		return tools.getTableId(table,true);
	}
	public static int getTableId(String table,Boolean update) {
		int id = 0;
		String sql = tools.getSQL("basic_memory__id").replace("__code__", table );
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			rset.next();
			id = rset.getInt("id");			
			if(update){
				sql = tools.getSQL("basic_memory__id_update").replace("__code__",table);
				stmt.executeUpdate(sql);
			}
			id++;
		} catch (SQLException e) {		
			id = 0;
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
		return id;
	}

	private final static String[] hex = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B",
			"1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C",
			"3D", "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D",
			"5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68",
			"69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73",
			"74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E",
			"7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94",
			"95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
			"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA",
			"AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5",
			"B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0",
			"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB",
			"CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6",
			"D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1",
			"E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC",
			"ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7",
			"F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

	private final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01,
			0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

	/**
	 * @param s
	 * @return
	 */
	public static String escape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') {
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {
				sbuf.append((char) ch);
			} else if (ch == '-' || ch == '_' || ch == '.' || ch == '!'
					|| ch == '~' || ch == '*' || ch == '\'' || ch == '('
					|| ch == ')') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007F) {
				sbuf.append('%');
				sbuf.append(hex[ch]);
			} else {
				sbuf.append('%');
				sbuf.append('u');
				sbuf.append(hex[(ch >>> 8)]);
				sbuf.append(hex[(0x00FF & ch)]);
			}
		}
		return sbuf.toString();
	}

	/**
	 * @param s
	 * @return
	 */
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') {
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {
				sbuf.append((char) ch);
			} else if (ch == '-' || ch == '_' || ch == '.' || ch == '!'
					|| ch == '~' || ch == '*' || ch == '\'' || ch == '('
					|| ch == ')') {
				sbuf.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != s.charAt(i + 1)) {
					cint = (cint << 4) | val[s.charAt(i + 1)];
					cint = (cint << 4) | val[s.charAt(i + 2)];
					i += 2;
				} else {
					cint = (cint << 4) | val[s.charAt(i + 2)];
					cint = (cint << 4) | val[s.charAt(i + 3)];
					cint = (cint << 4) | val[s.charAt(i + 4)];
					cint = (cint << 4) | val[s.charAt(i + 5)];
					i += 5;
				}
				sbuf.append((char) cint);
			} else {
				sbuf.append((char) ch);
			}
			i++;
		}
		return sbuf.toString();
	}

	public static ArrayList<String> filelist = new ArrayList<String>();
	public static ArrayList<String> folderList = new ArrayList<String>();

	public static void getFiles(String filePath){
		System.out.println(filePath);
	  File root = new File(filePath);
	    File[] files = root.listFiles();
	    for(File file:files){     
	     if(file.isDirectory()){
	    	 folderList.add(file.getName());
	     }else{
	    	 filelist.add(file.getName());
	      System.out.println("显示"+filePath+"下所有子目录"+file.getAbsolutePath());
	     }     
	    }
	 }
	
	public static String randomName(){
		String name = "";
		String name_1 = "赵钱孙李周吴郑王冯陈楮卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤";
		String name_2 ="安邦安福安歌安国安和安康安澜安民安宁安平安然安顺"
			+"宾白宾鸿宾实彬彬彬炳彬郁斌斌斌蔚滨海波光波鸿波峻"
			+"才捷才良才艺才英才哲才俊成和成弘成化成济成礼成龙"
			+"德本德海德厚德华德辉德惠德容德润德寿德水德馨德曜"
			+"飞昂飞白飞飙飞掣飞尘飞沉飞驰飞光飞翰飞航飞翮飞鸿"
			+"刚豪刚洁刚捷刚毅高昂高岑高畅高超高驰高达高澹高飞"
			+"晗昱晗日涵畅涵涤涵亮涵忍涵容涵润涵涵涵煦涵蓄涵衍"
			+"嘉赐嘉德嘉福嘉良嘉茂嘉木嘉慕嘉纳嘉年嘉平嘉庆嘉荣"
			+"开畅开诚开宇开济开霁开朗凯安凯唱凯定凯风凯复凯歌"
			+"乐安乐邦乐成乐池乐和乐家乐康乐人乐容乐山乐生乐圣"
			+"茂才茂材茂德茂典茂实茂学茂勋茂彦敏博敏才敏达敏叡"
			+"朋兴朋义彭勃彭薄彭湃彭彭彭魄彭越彭泽彭祖鹏程鹏池";
		
		int name_1_ = (int)(name_1.length()*Math.random());
		int name_2_ = (int)((name_2.length()-2)*Math.random());
		name = name_1.substring(name_1_,name_1_+1) + name_2.substring(name_2_,name_2_+2);		
		
		return name;
	}
	
	
	
	
	public static void main(String args[]) throws SQLException {
//		 tools.initMemory();
		// System.out.println(new Gson().toJson(obj.il8n));
//		System.out.println(tools.getConn());
//	    String filePath = "E:\\x\\Apache2.2\\logs";
//	    getFiles(filePath);
		// System.out.println(tools.getSQL("basic_user__login_loginData"));
		// System.out.println(tools.getTableId("basic_user"));
//		System.out.println(System.getProperty("os.name"));

//		tools.initMemory();
		System.out.println(tools.getTableId("basic_user"));
//		ArrayList a = new ArrayList();
//		Hashtable t1 = new Hashtable();
//		t1.put("code", "11");
//		a.add(t1);
//		t1 = new Hashtable();
//		t1.put("code", "12");
//		a.add(t1);
//		t1 = new Hashtable();
//		t1.put("code", "1201");
//		a.add(t1);
//		t1 = new Hashtable();
//		t1.put("code", "120101");
//		a.add(t1);
//		t1 = new Hashtable();
//		t1.put("code", "120102");
//		a.add(t1);
//		t1 = new Hashtable();
//		t1.put("code", "13");
//		a.add(t1);
//		
//		System.out.println( new Gson().toJson( tools.list2Tree(a) ) );
	}
}
