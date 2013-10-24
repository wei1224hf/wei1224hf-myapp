package myapp;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class service_resource {
	
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
			
			if (functionName.equals("qq_schools")) {
				Hashtable t_ = g.fromJson((String) request.getParameter("data"), Hashtable.class);
				out = qq_schools(
						 (String)t_.get("type")
						,(String)t_.get("country")
						,(String)t_.get("province")
						,(String)t_.get("prefix")
						,(String)t_.get("district")						
				);					
			}
			if (functionName.equals("eds_map")) {
				//Hashtable t_ = g.fromJson((String) request.getParameter("data"), Hashtable.class);
				out = eds_map(
						(String) request.getParameter("path")				
				);					
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return out;
	}

	public static String qq_schools(String type,String country,String province,String prefix,String district)  throws IOException{
		String s_return = "[";
		OutputStream outputStream = null;
		InputStream inputStream = null;
		String urlpath = "http://api.pengyou.com/json.php?cb=__i_5&mod=school&act=selector&schooltype="+type+"&country="+country+"&province="+province+"&g_tk=2114832255";
		if(prefix!=null || !prefix.equals("null")){
			urlpath += "&prefix="+prefix;
		}
		if(district!=null || !district.equals("null")){
			if(district.substring(0, 2).equals("11")){
				district = district.substring(0, 2)+district.substring(4, 6)+district.substring(4, 6);
			}
			urlpath += "&district="+district;
		}		
		System.out.println(urlpath);
		URL url = new URL(urlpath);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		 
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(20000);
		conn.setReadTimeout(20000);
		outputStream = conn.getOutputStream();
		String data_post = "";
		outputStream.write(data_post.getBytes("GB2312") );
		outputStream.flush(); 
		 
		//响应
		int responseCode = conn.getResponseCode();
		 
		if (responseCode == HttpURLConnection.HTTP_OK) {
			inputStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String temp = null;
			StringBuilder responseStr = new StringBuilder();
			while ((temp = reader.readLine()) != null) {
				responseStr.append(temp);
			}
			
			String str_temp = responseStr.toString();
			str_temp = str_temp.replace("document.domain = \"pengyou.com\"; __i_5(", "");
			str_temp = str_temp.replace(";<\\/i><a href=\\\"javascript:choose_school(", "");
			str_temp = str_temp.replace(",", "");
			
			String[] s_a = str_temp.split("middot");
			for(int i=1;i<s_a.length;i++){
				String str_item = s_a[i];
				String[] str_item_a = str_item.split("'"); 
				s_return += "{\"code\":\""+ str_item_a[0] +"\",\"value\":\""+str_item_a[1]+"\"},";
			}
			if(!s_return.endsWith("["))s_return = s_return.substring(0,s_return.length()-1);
			s_return += "]";
			System.out.println(s_return);
			
		}
		return s_return;
	}
	
	public static String eds_map(String path){		
		String thePath = "D:/gz/"+path;
		File file = new File(thePath);
		if (!file.exists()) {
			OutputStream outputStream = null;
			InputStream inputStream = null;
			//String thepath = "http://hbpic0.go2map.com/seamless1/dalian/mappic/png"+path;
			String thepath = "http://122.224.124.93:8005/ganzhou3D/"+path;
			URL url;
			try {
				url = new URL(thepath);
				URLConnection uc = url.openConnection();
				InputStream is = uc.getInputStream();

				BufferedImage images = ImageIO.read(is);
				ImageIO.write(images, "png", new File(thePath));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return thePath;
	}
	
	public static void main(String args[]){
		try {
			String s = service_resource.qq_schools("3", "0", "13","B",null);
			Hashtable t = new Gson().fromJson(s, Hashtable.class);
			ArrayList a = (ArrayList) t.get("data");
			for(int i=0;i<a.size();i++){
				System.out.println( ((StringMap)a.get(i)).get("title"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
