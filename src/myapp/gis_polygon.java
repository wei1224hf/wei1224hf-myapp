package myapp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class gis_polygon {
	
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
		if(zoom_i>=16){
			zoom_i=16;
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
		Statement statement2 = tools.getConn().createStatement();
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
		ArrayList a = new ArrayList();
		if(zoom_i>=16){
			String sql = "select astext(ogc_geom) as wkt ,gis_polygon_"+zoom_+".name,tempt.id as code , gis_polygon_"+zoom_+".id , gis_polygon_"+zoom_+".type from gis_polygon_"+zoom_ +" left join (select * from government_building where id_gis_polygon <> 0 )tempt on tempt.id_gis_polygon = gis_polygon_"+zoom_+".id " ;
			String where = " WHERE MBRIntersects(GEOMFROMTEXT('POLYGON(("+sm.get("right")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("bottom")+"))'), gis_polygon_"+zoom_+".ogc_geom)";
			sql += where + " limit 100;";
			ResultSet rs = statement.executeQuery(sql);
			ResultSetMetaData rsData = rs.getMetaData();
			
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rs.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "null");
					}
				}
				t.put("color", "#666666");
				a.add(t);
			}
		}else{
			String sql = "select astext(gis_polygon_"+zoom_+".ogc_geom) as wkt,astext(gis_point_"+zoom_+".ogc_geom) as wkt2 ,gis_polygon_"+zoom_+".name , gis_polygon_"+zoom_+".code , gis_polygon_"+zoom_+".type from gis_polygon_"+zoom_+",gis_point_"+zoom_;
			String where = " WHERE gis_polygon_"+zoom_+".code = gis_point_"+zoom_+".code and MBRIntersects(GEOMFROMTEXT('POLYGON(("+sm.get("right")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("top")+","+sm.get("left")+" "+sm.get("bottom")+","+sm.get("right")+" "+sm.get("bottom")+"))'), gis_polygon_"+zoom_+".ogc_geom)";
			sql += where + " limit 100;";
			ResultSet rs = statement.executeQuery(sql);
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
				t.put("color", "#666666");
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
				wkt = "LINESTRING("+wkt__+")";
				String wkt2 = rs.getString("wkt2");
				ArrayList wkt_a = new ArrayList();
				wkt_a.add(wkt);
				wkt_a.add(wkt2);
				t.put("wkt", wkt_a);
				a.add(t);
			}

		}
		t_return.put("Rows", a);	


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
	
	public static int gid = 1;
	public static int cid = 0;
	public static String thecode = "";
	public static int thecodeid = 0;
	public static void split(String tablefrom,String tableto,String code_,Boolean vertical){
		gis_polygon.cid ++;		

		try {			
			Connection conn = tools.getPostGreSqlConn();
			
			Statement st = conn.createStatement();
			Statement st2 = conn.createStatement();
			Statement st3 = conn.createStatement();
			String sql = " select avg(st_area(geom)) as avg_ from  "+tablefrom+" where code like '"+code_+"%'";
//			System.out.println(sql);
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			double avg_ = rs.getDouble(1);


//			if(tablefrom.equals("what3")||tablefrom.equals("what4")||tablefrom.equals("what5"))
			sql = " select gid,st_astext(geom) as wkt,code,name from "+tablefrom+" where code like '"+code_+"%' and st_area(geom) > "+avg_+"/3 order by code ";
//			System.out.println(sql);
			
//			st3.executeUpdate("drop table "+tableto);
//			st3.executeUpdate("drop KEY "+tableto+"_key");
			if(code_.equals("360731")){
				String sql_create = "CREATE TABLE "+tableto+"(  gid serial NOT NULL,  geom geometry(POLYGON),  name character varying(200),  code character varying(200),  CONSTRAINT "+tableto+"_pkey PRIMARY KEY (gid))";
				st3.executeUpdate(sql_create);
			}
			
			rs = st.executeQuery(sql);	
			String sql_insert;
			st2.executeUpdate("BEGIN");
			gis_polygon.thecode = "";
			while (rs.next()) {
				
				String geom = "ST_GeomFromText('"+rs.getString("wkt")+"',4326)";
				geom = geom.replace("MULTIPOLYGON(", "POLYGON").replace(")))", "))");
				String sql_box = "select box2d("+geom+") as box";
				String code = rs.getString("code");
				if(!gis_polygon.thecode.equals(code)){
					gis_polygon.thecode = code;
					gis_polygon.thecodeid = 1;
				}
				ResultSet rs2 = st2.executeQuery(sql_box);
				rs2.next();
				String box = rs2.getString("box").replace("BOX(", "").replace(")", "");
				String[] box_ = box.split(",");
				double lon1 = Double.valueOf( box_[0].split(" ")[0] );
				double lat1 = Double.valueOf( box_[0].split(" ")[1] );
				double lon2 = Double.valueOf( box_[1].split(" ")[0] );
				double lat2 = Double.valueOf( box_[1].split(" ")[1] );
				
				String line ="";
				if(vertical){
					line = "ST_GeomFromText('LINESTRING("+(lon1+lon2)/2+" "+lat1+","+(lon1+lon2)/2+" "+lat2+")',4326)";
				}else{
					line = "ST_GeomFromText('LINESTRING("+lon1+" "+(lat1+lat2)/2+","+lon2+" "+(lat1+lat2)/2+")',4326)";
				}
				

				String sql_select_item = "SELECT (ROW_NUMBER() over (ORDER BY path ASC))+"+(gis_polygon.gid+10)+" as gid,"+code+" as code,'"+rs.getString("name")+"' as name ,ST_GeomFromText(st_astext(geom),4326) as geom from  st_dump(ST_Split( "+geom+" , "+line+"))";
				ResultSet rs3 = st2.executeQuery(sql_select_item);
				while(rs3.next()){
					gis_polygon.gid ++;
					
					if(gis_polygon.cid == 4){
						gis_polygon.thecodeid ++;
						code = rs.getString("code")+(10 + gis_polygon.thecodeid );
					}		
					if(tableto.equals("what11")&&code.equals("3607021923")){
						System.out.println("nodire "+code_+" "+" "+vertical+" "+code+" "+gis_polygon.cid);
					}					
					sql_insert = "insert into "+tableto+"(gid,code,name,geom) values ('"+gid+"','"+code+"','"+rs3.getString("name")+"','"+rs3.getString("geom")+"') ";
//					System.out.println( gis_polygon.thecodeid);
					st3.executeUpdate(sql_insert);
				}
			}
			
			sql = " select gid,st_astext(geom) as wkt,code,name from "+tablefrom+" where code like '"+code_+"%' and st_area(geom) <= "+avg_+"/3 order by code ";
//			System.out.println(sql);
			rs = st.executeQuery(sql);	
			gis_polygon.thecode = "";
			while (rs.next()) {
				gis_polygon.gid ++;
				
				String geom = "ST_GeomFromText('"+rs.getString("wkt")+"',4326)";
				geom = geom.replace("MULTIPOLYGON(", "POLYGON").replace(")))", "))");
				String code = rs.getString("code");
				if(!gis_polygon.thecode.equals(code)){
					gis_polygon.thecode = code;
					gis_polygon.thecodeid = 1;
				}
				String name = rs.getString("name");

				if(gis_polygon.cid == 4){
					gis_polygon.thecodeid ++;
					code = rs.getString("code")+(50 + gis_polygon.thecodeid );
				}			
				if(tableto.equals("what11")&&code.equals("3607021923")){
					System.out.println("nodire "+code_+" "+" "+vertical+" "+code+" "+gis_polygon.cid);
				}
				sql_insert = "insert into "+tableto+"(gid,code,name,geom) values ("+gis_polygon.gid+","+code+",'"+name+"',"+geom+") ";
//				System.out.println(sql_insert);
				st3.executeUpdate(sql_insert);
			}
			st2.executeUpdate("COMMIT");
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

		if(gis_polygon.cid==4)gis_polygon.cid=0;
	}	
	
	public static void pg2mysql(){
		try {			
			Connection conn = tools.getPostGreSqlConn();
			Connection conn2 = tools.getConn();
			
			Statement st = conn.createStatement();
			Statement st2 = conn2.createStatement();
			st2.executeUpdate("START TRANSACTION;");
			String sql = "select code,name,st_astext(geom) as t from what7 union select code,name,st_astext(geom) as t from what11 union select code,name,st_astext(geom) as t from what3 union select code,name,st_astext(geom) as t from what  order by code";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				String sql_insert = "";
				if(rs.getString("code").length()==10){
					sql_insert = "insert into gis_polygon_14(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}else if(rs.getString("code").length()==8){
					sql_insert = "insert into gis_polygon_12(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}else if(rs.getString("code").length()==4){
					sql_insert = "insert into gis_polygon_8(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}else{
					sql_insert = "insert into gis_polygon_10(code,name,type,ogc_geom) values ('"+rs.getString("code")+"','"+rs.getString("name")+"','111',GeomFromText('"+rs.getString("t")+"'))";
				}
				st2.executeUpdate(sql_insert);
			}
			st2.executeUpdate("COMMIT;");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getWidth(int level,int num){ 
		double r = 0;
		//if(level<10)r = num*()
		return r;
	}
	
	public static void getSLD(){
		
		
		int max = 221000000;
		int z_ = 20;
		DecimalFormat df =new DecimalFormat("#");
		
	
//		String name = "gis_line_4__traffic__t";
//		int c_r = 254;
//		int c_g = 195;
//		int c_b = 101;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 5;
//		double widths[] = {0,1,1,1,1, 0.5,0.8,1, 1.5,2,2.5 ,3,4,5,6,7 ,9,11,13,15 ,18};
		
//		String name = "gis_line_4__traffic__b";
//		int c_r = 221;
//		int c_g = 161;
//		int c_b = 66;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 7;
//		double widths[] = {0,1,1,1,1, 0.5,0.8,1.8, 2.5,3,3.5 ,4,5,6,7,8 ,9,12,14,16 ,19};
		
//		String name = "gis_line_5__traffic__t";
//		int c_r = 255;
//		int c_g = 247;
//		int c_b = 116;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 5;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,1.2, 2.3,2.7,3 ,3.5,4.5,4.8,6,7 ,8,10,12,14 ,16};		
		
//		String name = "gis_line_5__traffic__b";
//		int c_r = 243;
//		int c_g = 219;
//		int c_b = 91;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 7;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,1.7, 3.3,3.7,4 ,4.5,5.5,5.8,7,8 ,9,11,13,15 ,17};				
		
//		String name = "gis_line_6__traffic__t";
//		int c_r = 254;
//		int c_g = 253;
//		int c_b = 117;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 9;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,1.2, 2,2.5,2.9 ,3.5,4.3,5.1,6.2,6.9 ,7.5,9.5,11.5,13.5 ,15.5};	
		
//		String name = "gis_line_6__traffic__b";
//		int c_r = 217;
//		int c_g = 205;
//		int c_b = 151;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 9;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,1.2, 2.5,3,3.6 ,4.5,5.3,6.1,7.2,7.9 ,8.5,10.5,12.5,14.5 ,16.5};		
		
//		String name = "gis_line_10__traffic__t";
//		int c_r = 254;
//		int c_g = 253;
//		int c_b = 177;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 10;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,0, 0,2,2.5,2.9 ,3.5,4.3,5.1,6.2,6.9 ,7.5,9.5,11.5,13.5 };				
		
//		String name = "gis_line_10__traffic__b";
//		int c_r = 228;
//		int c_g = 219;
//		int c_b = 202;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 10;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,0, 0,2.5,3,3.6 ,4.5,5.3,6.1,7.2,7.9 ,8.5,10.5,12.5,14.5};	
		
//		String name = "gis_line_12__traffic__t";
//		int c_r = 249;
//		int c_g = 249;
//		int c_b = 249;
//		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
//		int z = 11;
//		double widths[] = {0,1,1,1,1, 0.3,0.7,0, 0,0,0,1,2 ,3.5,4.3,5.1,6.2,6.9 ,7.5,8.5,9.5 };		
//		
		String name = "gis_line_12__traffic__b";
		int c_r = 218;
		int c_g = 213;
		int c_b = 210;
		String color = "#"+Integer.toHexString(c_r)+Integer.toHexString(c_g)+Integer.toHexString(c_b);
		int z = 10;
		double widths[] = {0,1,1,1,1, 0.3,0.7,0, 0,0,0.1,2,3 ,4.5,5.3,6.1,7.2,7.9 ,8.5,9.5,10.5};			
//			
		//P62003
		String sld = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		sld += "\n    <StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:se=\"http://www.opengis.net/se\">";
		sld += "\n        <NamedLayer>";
		sld += "\n            <se:Name>"+name+"</se:Name>";
		sld += "\n            <UserStyle>";
		sld += "\n                <se:Name>"+name+"</se:Name>";
		sld += "\n                <se:FeatureTypeStyle>";
		
		for(int i=z;i<=z_;i++){
		sld += "\n                    <se:Rule>";
		sld += "\n                        <se:Name>"+name+"___"+i+"</se:Name>";
		sld += "\n                        <se:MinScaleDenominator>"+df.format(Math.floor(max/(Math.pow(2, i))))+"</se:MinScaleDenominator>";
		sld += "\n                        <se:MaxScaleDenominator>"+df.format(Math.floor(max/(Math.pow(2, i-1))))+"</se:MaxScaleDenominator>";
		sld += "\n                        <se:LineSymbolizer>";
		sld += "\n                            <se:Stroke>";
		sld += "\n                                <se:SvgParameter name=\"stroke\">"+color+"</se:SvgParameter>";
		sld += "\n                                <se:SvgParameter name=\"stroke-width\">"+widths[i]+"</se:SvgParameter>";
		sld += "\n                                <se:SvgParameter name=\"stroke-linejoin\">round</se:SvgParameter>";
		sld += "\n                                <se:SvgParameter name=\"stroke-linecap\">round</se:SvgParameter>";
		sld += "\n                            </se:Stroke>";		
		sld += "\n                        </se:LineSymbolizer>";
		sld += "\n                    </se:Rule>";
		}
		
		sld += "\n                </se:FeatureTypeStyle>"+
			   "\n            </UserStyle>"+
			   "\n        </NamedLayer>"+
			   "\n    </StyledLayerDescriptor>";

		
		System.out.println(sld);
	}
	
	public static void main(String args[]) throws SQLException{
//		String[] str = {"360731","360722",	"360723",	"360724",	"360725",	"360726",	"360727",	"360728",	"360729",	"360730",		"360732",	"360733",	"360734",	"360735",	"360781",	"360782","360702","360721"};
//		for(int i=0;i<str.length;i++){
////			gis_polygon.split("what3","what4",str[i],true);
////			gis_polygon.split("what4","what5",str[i],false);
////			gis_polygon.split("what5","what6",str[i],true);
////			gis_polygon.split("what6","what7",str[i],false);
////////			
////			gis_polygon.split("what7","what8",str[i],true);
////			gis_polygon.split("what8","what9",str[i],false);
////			gis_polygon.split("what9","what10",str[i],true);
////			gis_polygon.split("what10","what11",str[i],false);
//////			
////////			gis_polygon.split("what11","what12",str[i],true);
//		}
		
//		gis_polygon.pg2mysql();
		gis_polygon.getSLD();
	}	
}
