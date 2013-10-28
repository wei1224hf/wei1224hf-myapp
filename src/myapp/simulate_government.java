package myapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class simulate_government {
	
	//根据地图数据模拟组织
	public static void basic_group_fromPG(){
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
	public static void basic_group(){
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
}
