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

public class install {

	public static String path = "";

	public static Hashtable step1() {
		Hashtable t_return = new Hashtable();

		// 判断配置文件是否可写
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
							+ "\\config.xml can't be written. please modify the authority and try again");
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
							+ otherPath+ " can't be written. please modify the authority and try again");
			return t_return;
		}

		// 判断主要业务文件夹
		f_config = new File(install.path + "\\file\\upload");
		if (!(f_config.isDirectory() && f_config.canWrite())) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ "\\file\\upload can't be written. please modify the authority and try again");
			return t_return;
		}

		t_return.put("status", "1");
		t_return.put("msg", "Done, everything is right.");
		return t_return;
	}

	public static String unm, pwd, host, port, db, il8n = "";

	public static Hashtable step2() {
		Hashtable t_return = new Hashtable();
		Connection conn = null;

		try {
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);

			String url = "jdbc:mysql://" + install.host + ":" + install.port
					+ "/" + install.db + "?characterEncoding=utf8";
			String user = install.unm;
			String password = install.pwd;
			conn = DriverManager.getConnection(url, user, password);

		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
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

			document.elementByID("DB_URL").setText(
					"jdbc:mysql://" + install.host + ":" + install.port + "/"
							+ install.db + "?characterEncoding=utf8");
			document.elementByID("DB_UNM").setText(install.unm);
			document.elementByID("DB_PWD").setText(install.pwd);
			document.elementByID("IL8N").setText(install.il8n);

			String savexml = document.asXML();
			savexml = savexml
					.replace(
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

		t_return.put("status", "1");
		t_return.put(
				"msg",
				"Done, everything is right. You may check the Databse infomation from config.xml later. ");
		return t_return;
	}

	public static String XLSSQL = null;

	public static Hashtable step3() {
		Hashtable t_return = new Hashtable();
		String rootpath = tools.getConfigItem("APPPATH");
		String filePath = rootpath
				+ "\\file\\sql.xls";
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
		for (int i = 0; i < sheetcount; i++) {
			Sheet sheet = workBook.getSheet(i);
			int rows = sheet.getRows();

			for (int i2 = 0; i2 < rows; i2++) {
				sql += sheet.getCell(4, i2).getContents() + "\r\t";
			}
		}

		try {
			install.XLSSQL = sql;
			FileOutputStream fos = new FileOutputStream(rootpath
					+ "\\file\\sql.txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(sql);
			osw.flush();
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
				+ "/file/developer/tables_community_data_2.xls", "admin");

		String rootpath = tools.getConfigItem("APPPATH");
		String filePath = rootpath
				+ "\\file\\developer\\tables_community_data_2.xls";
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
		basic_group.simulate();
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
			rset = stmt
					.executeQuery("select count(*) as c from government_resident ");
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
