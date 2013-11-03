package myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class install {

	public static String path = "";

	public static Hashtable step1() {
		Hashtable t_return = new Hashtable();

		File f_config = new File(install.path + "\\config.xml");
		if (!f_config.exists()) {
			t_return.put("status", "2");
			t_return.put("msg", "Cant find the config.xml " + install.path
					+ "\\config.xml");
			return t_return;
		}
		if (!f_config.canWrite()) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ "\\config.xml can't be written. modify the authority and try again");
			return t_return;
		}

		String xml = "";
		try {
			String path = install.path + "\\config.xml";

			File file = new File(path);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			int s;
			while ((s = br.read()) != -1) {
				buffer.append((char) s);
			}
			xml = buffer.toString();

		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		try {
			Document document = DocumentHelper.parseText(xml);
			document.elementByID("APPPATH").setText(install.path);
			String savexml = document.asXML();
			savexml = savexml.replace(
							"<!DOCTYPE root>",
							"<!DOCTYPE root [   <!ELEMENT root ANY>   <!ELEMENT item ANY>   <!ATTLIST item ID ID #REQUIRED>   <!ATTLIST item Explanation CDATA #IMPLIED>]>");

			FileOutputStream fos = new FileOutputStream(install.path
					+ "\\config.xml");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(savexml);
			osw.flush();
		} catch (DocumentException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (FileNotFoundException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (UnsupportedEncodingException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		// 判断主要业务文件夹
		String otherPath = "";
		otherPath = "\\file";
		f_config = new File(install.path +otherPath );
		if (!(f_config.isDirectory() && f_config.canWrite())) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ otherPath+ " can't be written. modify the authority and try again");
			return t_return;
		}

		// 判断主要业务文件夹
		f_config = new File(install.path + "\\file\\upload");
		if (!(f_config.isDirectory() && f_config.canWrite())) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ "\\file\\upload can't be written. modify the authority and try again");
			return t_return;
		}

		t_return.put("status", "1");
		t_return.put("msg", "Done, everything is right.");
		return t_return;
	}

	public static String unm, pwd, host, port, db, il8n ,type,mode = "";

	public static Hashtable step2() {
		Hashtable t_return = new Hashtable();

		String xml = "";
		try {
			String path = install.path + "\\config.xml";

			File file = new File(path);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			int s;
			while ((s = br.read()) != -1) {
				buffer.append((char) s);
			}
			xml = buffer.toString();

		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		try {
			Document document = DocumentHelper.parseText(xml);			

			document.elementByID("DB_URL").setText(
					"CDATASTART__jdbc:"+install.type+"://" + install.host + ":" + install.port + "/"
							+ install.db + "?user="+install.unm+"&password="+install.pwd+"__CDATAEND");
			document.elementByID("DB_UNM").setText("NULL");
			document.elementByID("DB_PWD").setText("NULL");
			document.elementByID("DB_HOST").setText("NULL");
			document.elementByID("DB_NAME").setText("NULL");
			document.elementByID("DB_TYPE").setText(install.type);
			document.elementByID("MODE").setText(install.mode);
			document.elementByID("IL8N").setText(install.il8n);
			
			String savexml = document.asXML();
			savexml = savexml
					.replace(
							"<!DOCTYPE root>",
							"<!DOCTYPE root [   <!ELEMENT root ANY>   <!ELEMENT item ANY>   <!ATTLIST item ID ID #REQUIRED>   <!ATTLIST item Explanation CDATA #IMPLIED>]>");
			savexml = savexml.replace("&amp;", "&");
			savexml = savexml.replace("CDATASTART__", "<![CDATA[");
			savexml = savexml.replace("__CDATAEND", "]]>");
			FileOutputStream fos = new FileOutputStream(install.path
					+ "\\config.xml");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(savexml);
			osw.flush();
			
		} catch (DocumentException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}catch (FileNotFoundException e) {		 
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (UnsupportedEncodingException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}		
		tools.getConfigItem("reLoad");
		Connection conn = tools.getConn();
		if(conn==null){
			t_return.put("status", "2");
			t_return.put(
					"msg",
					"Can not connect to the database. ");
		}else{
			t_return.put("status", "1");
			t_return.put(
					"msg",
					"Done, everything is right. You may check the Databse infomation from config.xml later. ");
		}

		return t_return;
	}

	public static String XLSSQL = null;
	public static Hashtable step3() {
		Hashtable t_return = new Hashtable();
		String rootpath = tools.getConfigItem("APPPATH");
		String filePath = rootpath
				+ "\\file\\"+tools.getConfigItem("DB_TYPE")+".xls";
		InputStream fs = null;
		Workbook workBook = null;

		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (BiffException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		String sql_create = "";
		String sql_insert = "";
		String tablename = null;
		String language = "";
		int sheetcount = workBook.getNumberOfSheets();
		for (int i = 0; i < sheetcount; i++) {
			Sheet sheet = workBook.getSheet(i);
			int rows = sheet.getRows();
			
			for (int i2 = 0; i2 < rows; i2++) {
				String theSQL = sheet.getCell(4, i2).getContents();

				if(theSQL.contains("insert")){
					sql_insert += theSQL  + "\r\t";
				}else{
					sql_create += theSQL  + "\r\t";
					if(sheet.getCell(0, i2).getContents()!=null){
						language += sheet.getCell(1, i2).getContents()+"=\""+sheet.getCell(0, i2).getContents()+"\""+"\r\t";
					}
				}
				if(theSQL.contains("create table")){
					if(tablename!=null){
						FileOutputStream fos;
						try {
							fos = new FileOutputStream(rootpath
									+ "\\language\\"+tools.getConfigItem("IL8N")+"\\"+tablename+".ini");
							OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
							osw.write(language);
							osw.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						tablename = sheet.getCell(1, i2).getContents();
					}					
				}
			}
		}

		try {
			install.XLSSQL = sql_create + sql_insert;
			FileOutputStream fos = new FileOutputStream(rootpath
					+ "\\file\\sql.txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(sql_create);
			osw.flush();
			
			FileOutputStream fos2 = new FileOutputStream(rootpath
					+ "\\file\\data.txt");
			OutputStreamWriter osw2 = new OutputStreamWriter(fos, "UTF-8");
			osw2.write(sql_insert);
			osw2.flush();			
		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		String sqls[] = install.XLSSQL.split(";");
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			int index = 0;
			for (int i = 0; i < sqls.length - 1; i++) {
				System.out.println(sqls[i]);
				stmt.executeUpdate(sqls[i] + ";");
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		tools.initMemory();

		t_return.put("status", "1");
		t_return.put("msg", "Done, everything is right.  ");
		return t_return;
	}

	public static Hashtable step4() {
		Hashtable t_return = new Hashtable();

		basic_group.upload(tools.getConfigItem("APPPATH")
				+ "/file/data.xls", "admin");

		String rootpath = tools.getConfigItem("APPPATH");
		String filePath = rootpath
				+ "\\file\\data.xls";
		InputStream fs = null;
		Workbook workBook = null;

		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (BiffException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		String sql = "";
		int sheetcount = workBook.getNumberOfSheets();
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			Sheet sheet = workBook.getSheet("init");
			int rows = sheet.getRows();
			stmt.executeUpdate("delete from basic_parameter where reference in ('zone','profession');");
			stmt.executeUpdate("START TRANSACTION;");
			for (int i2 = 0; i2 < rows; i2++) {
				sql = sheet.getCell(4, i2).getContents();
				stmt.executeUpdate(sql);
			}
			stmt.executeUpdate("COMMIT;");
		} catch (SQLException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		tools.initMemory();

		t_return.put("status", "1");
		t_return.put(
				"msg",
				"Done, everything is right. You can visit the <a href='../html/desktop.html'>system</a> now . Username and password are both 'admin' ");
		return t_return;
	}

	public static Hashtable basic_group() {
		Hashtable t_return = new Hashtable();
		//basic_group.simulate();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select count(*) as c from basic_group ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table basic_group has been simulated : " + c
				+ " in total");
		return t_return;
	}

	public static Hashtable basic_user() {
		Hashtable t_return = new Hashtable();
		basic_user.data4test();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select count(*) as c from basic_user ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table basic_user has been simulated : " + c
				+ " in total");
		return t_return;
	}

	public static Hashtable oa_plan() {
		Hashtable t_return = new Hashtable();
		oa_plan.data4test(1);
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select count(*) as c from oa_plan ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table oa_plan has been simulated : " + c
				+ " in total");
		return t_return;
	}

	public static Hashtable oa_work() {
		Hashtable t_return = new Hashtable();
		oa_work.data4test(1);
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select count(*) as c from oa_work ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table oa_work has been simulated : " + c
				+ " in total");
		return t_return;
	}

	public static Hashtable government_building() {
		Hashtable t_return = new Hashtable();
		government_building.data4test(1);
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt
					.executeQuery("select count(*) as c from government_building ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table government_building has been simulated : "
				+ c + " in total");
		return t_return;
	}

	public static Hashtable government_company() {
		Hashtable t_return = new Hashtable();
		government_company.data4test();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt
					.executeQuery("select count(*) as c from government_company ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table government_company has been simulated : "
				+ c + " in total");
		return t_return;
	}

	public static Hashtable government_family() {
		Hashtable t_return = new Hashtable();
		government_family.data4test(1);
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt
					.executeQuery("select count(*) as c from government_family ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table government_family has been simulated : " + c
				+ " in total");
		return t_return;
	}

	public static Hashtable government_resident() {
		Hashtable t_return = new Hashtable();
		government_resident.data4test(1);
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		String c = "0";
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("select count(*) as c from government_resident ");
			rset.next();
			c = rset.getString("c");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}

		t_return.put("status", "1");
		t_return.put("msg", "table government_resident has been simulated : "
				+ c + " in total");
		return t_return;
	}
	
	public static void main(String args[]){
		install.step3();
	}
}
