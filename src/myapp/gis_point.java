package myapp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class gis_point {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
	
		try {	
			Hashtable t__grup_type = new Hashtable();
			if(!functionName.equals("login")){
				t__grup_type = basic_user.getSession(executor);	
			}
				
			String group_code = (String) t__grup_type.get("group_code");
			String user_type = (String) t__grup_type.get("user_type");
			
			if(functionName.equals("add")){
				t = add(
						(String)request.getParameter("data"),
						(String)request.getParameter("zoom"),
						executor
						
						);							
			}
			if(functionName.equals("read")){
				t = read(
						(String)request.getParameter("data"),
						(String)request.getParameter("zoom"),
						executor
						);							
			}
			if(functionName.equals("remove")){
				t = remove(
						(String)request.getParameter("data"),
						executor
						);							
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable add(String data,String zoom,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();

		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	

		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			if(!key.equals("ogc_geom")){
				t_data.put(key, "'"+value+"'");
			}else{
				t_data.put(key, value);
			}
		}

		e = t_data.keys();
		
		int zoom_i = Integer.valueOf(zoom);
		if(zoom_i>=17){
			zoom_i=17;
		}else if(zoom_i>=15){
			zoom_i=15;
		}
		
		String zoom_ = zoom_i+"";
		String keys = "insert into gis_polygon_"+zoom_+" (";
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
		
		sql = "SELECT LAST_INSERT_ID() as id;";
		System.out.println(sql);		
		ResultSet rs = statement.executeQuery(sql);
		rs.next();
		
		t_return.put("id", rs.getString("id"));
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		
		return t_return;
	}	
	
	
	public static Hashtable read(String data,String zoom,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		StringMap sm = new Gson().fromJson(data, StringMap.class);
		int zoom_i = Integer.valueOf(zoom);
		if(zoom_i>=16){
			zoom_i=16;
		}else if(zoom_i>=14){
			zoom_i=14;
		}
		else if(zoom_i>=12){
			zoom_i=12;
		}
		else if(zoom_i>=10){
			zoom_i=10;
		}
		else if(zoom_i>=8){
			zoom_i=8;
		}
		String zoom_ = zoom_i+"";
		
		String sql = "select astext(ogc_geom) as wkt ,name , id , type from gis_point_"+zoom_;
		String where = " WHERE MBRIntersects(GEOMFROMTEXT('POLYGON(("+sm.get("right")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("bottom")+"))'), ogc_geom)";

		sql += where + " limit 100;";

		System.out.println(sql);
		ResultSet rs = statement.executeQuery(sql);
		ArrayList a = new ArrayList();
		ResultSetMetaData rsData = rs.getMetaData();
		while (rs.next()) {			
			Hashtable<String, String> t = new Hashtable();	
			for(int i=1;i<=rsData.getColumnCount();i++){
				if(rs.getString(rsData.getColumnLabel(i)) != null){
					t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
				}else{
					t.put(rsData.getColumnLabel(i), "nul");
				}
			}
			if(zoom_i<=10){
				String wkt = rs.getString("wkt");
				wkt = wkt.replace("MULTIPOLYGON(((", "").replace(")))", "");
				String[] wkt_ = wkt.split(",");
				String wkt__ = "";
				for(int j=0;j<wkt_.length;j+=30){
					wkt__ += wkt_[j]+",";
				}
				wkt__+= wkt_[wkt_.length-1];
				wkt = "POLYGON(("+wkt+"))";
				t.put("wkt", wkt);
			}			
			t.put("color", "#666666");
			a.add(t);
		}
		t_return.put("Rows", a);	
		String sql_total = "select count(*) as count_ from gis_polygon_"+zoom_+where;

		rs = statement.executeQuery(sql_total);
		rs.next();
		t_return.put("Total", rs.getString("count_"));

		return t_return;
	}		
	
	public static Hashtable remove(String data,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement statement = tools.getConn().createStatement();
		StringMap sm = new Gson().fromJson(data, StringMap.class);
		String sql = "delete from gis_polygon where id = "+sm.get("id");

		System.out.println(sql);
		statement.executeUpdate(sql);
		t_return.put("id", sm.get("id"));

		return t_return;
	}
	

	
	public static void pg2mysql(){
		try {			
			Connection conn = tools.getConn_pg();
			Connection conn2 = tools.getConn();
			
			Statement st = conn.createStatement();
			Statement st2 = conn2.createStatement();
			st2.executeUpdate("START TRANSACTION;");
			String sql = "select code,name,st_astext(st_centroid(geom)) as t from what7 union select code,name,st_astext(st_centroid(geom)) as t from what11 union select code,name,st_astext(st_centroid(geom)) as t from what3 union select code,name,st_astext(st_centroid(geom)) as t from what  order by code";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				String sql_insert = "";
				
				if(rs.getString("code").length()==6){
					sql_insert = "insert into gis_point_10(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}
				else if(rs.getString("code").length()==8){
					sql_insert = "insert into gis_point_12(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}
				else if(rs.getString("code").length()==4){
					sql_insert = "insert into gis_point_8(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}
				else if(rs.getString("code").length()==10){					
					sql_insert = "insert into gis_point_14(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
					System.out.println(sql_insert);
				}
				st2.executeUpdate(sql_insert);
			}
			st2.executeUpdate("COMMIT;");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws SQLException{
//		String[] str = {"360722",	"360723",	"360724",	"360725",	"360726",	"360727",	"360728",	"360729",	"360730",	"360731",	"360732",	"360733",	"360734",	"360735",	"360781",	"360782","360702","360721"};
//		for(int i=0;i<str.length;i++){
//			gis_polygon.split("what3","what4",str[i],true);
//			gis_polygon.split("what4","what5",str[i],false);
//			gis_polygon.split("what5","what6",str[i],true);
//			gis_polygon.split("what6","what7",str[i],false);
//			gis_polygon.split("what7","what8",str[i],true);
//			gis_polygon.split("what8","what9",str[i],false);
////			gis_polygon.split("what9","what10",str[i],true);
////			gis_polygon.split("what10","what11",str[i],false);
////			gis_polygon.split("what11","what12",str[i],true);
//		}
		
		gis_point.pg2mysql();
	}	
}
