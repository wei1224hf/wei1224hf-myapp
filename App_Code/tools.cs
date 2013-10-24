using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MySql.Data.MySqlClient;
using System.IO;
using System.Xml;
using System.Xml.Linq;
using System.Security.Cryptography;
using System.Text;

public class tools
{
    public tools()
	{
	}

    public static String webPath = "";

    public static MySqlConnection getConn(){
        String DB_HOST = tools.getConfigItem("DB_HOST");
        String DB_UNM = tools.getConfigItem("DB_UNM");
        String DB_PWD = tools.getConfigItem("DB_PWD");
        String DB_NAME = tools.getConfigItem("DB_NAME");
        MySqlConnection conn = new MySqlConnection("Data Source=" + DB_HOST + ";Database=" + DB_NAME + ";User ID="+DB_UNM+";Password="+DB_PWD+";Charset=utf8");
        conn.Open();
        MySqlCommand comd = new MySqlCommand("SET NAMES UTF8");
        comd.Connection = conn;
        comd.ExecuteNonQuery();

        comd.CommandText = "set time_zone='+8:00';";
        comd.ExecuteNonQuery();
        return conn;
    }

    public static Hashtable il8n = null;
    public static Hashtable readIl8n()
    {
        if (tools.il8n == null) 
        {
            tools.il8n = new Hashtable();
            String path = tools.webPath + "\\language\\zh-cn\\";
            String[] files = Directory.GetFiles(@path, "*.ini");
            for (int i = 0; i < files.Count();i++ )
            {
                String inipath = files[i];
                TextReader iniFile = new StreamReader(inipath);
                String strLine = iniFile.ReadLine();
                String currentRoot = "";
                Hashtable t_file = new Hashtable();
                while (strLine != null)
                {
                    if (strLine != "")
                    {
                        if (strLine.StartsWith("[") && strLine.EndsWith("]"))
                        {
                            currentRoot = strLine.Substring(1, strLine.Length - 2);
                        }
                        else
                        {
                            String[] key_value = strLine.Split(new char[] { '=' }, 2);
                            t_file.Add(key_value[0],key_value[1].Replace("\"",""));
                        }
                    }
                    strLine = iniFile.ReadLine();
                }
                tools.il8n.Add(currentRoot,t_file);
            }
        }

        return tools.il8n;
    }

    public static Hashtable importIl8n2DB()
    {
        Hashtable t_return = new Hashtable();
        MySqlConnection conn = tools.getConn();
        MySqlCommand comd = new MySqlCommand("delete from basic_memory where extend6 = 'il8n';");
        comd.Connection = conn;
        comd.ExecuteNonQuery();
        int total = 0;
        String tablenames = "";
        if (tools.il8n == null) tools.readIl8n();
        foreach (System.Collections.DictionaryEntry objDE in tools.il8n)
        {
            String tablename = objDE.Key.ToString();
            tablenames += "," + tablename;
            Hashtable t = (Hashtable)objDE.Value;
            foreach (System.Collections.DictionaryEntry objDE2 in t)
            {
                String sql = "insert into basic_memory (type,code,extend4,extend5,extend6) values ('0','"
                            + objDE2.Key.ToString() + "','" + objDE2.Value.ToString() + "','" + tablename + "','il8n');";

                comd.CommandText = sql;
                comd.ExecuteNonQuery();

                total++;
            }
        }
        comd = null;
        conn.Close();
        conn = null;
        t_return.Add("total", total);
        t_return.Add("tablenames", tablenames);
        return t_return;
    }

    /// <summary>
    /// 初始化数据库内存表
    /// 内存表中存储的数据有: 
    ///     参数表 basic_paramater 中的业务参数, 大型标准数据字典不会添加到内存表(比如行政区划编码)
    ///     各个业务表的当前递增ID
    ///     国际化语言 il8n  ,来自 .ini 文件
    /// </summary>
    public static Hashtable initMemory()
    {
        Hashtable t_return = new Hashtable();
        String s_sql = tools.getConfigItem("basic_memory__init");
        MySqlConnection conn = tools.getConn();
        MySqlCommand comd = new MySqlCommand();
        comd.Connection = conn;
        comd.CommandText = "delete form basic_memory";
        comd.ExecuteNonQuery();
        String[] sql = s_sql.Split(new char[1] { ';' });
        int total = 0;
        for (int i = 0; i < sql.Count();i++ )
        {
            String sql_ = sql[i];
            comd.CommandText = sql_;
            comd.ExecuteNonQuery();
            total++;
        }
        tools.importIl8n2DB();
        t_return.Add("total",total);
        conn.Close();
        conn = null;
        return t_return;
    }

    public static int getTableId(String tablename) 
    {
        int id = 0;
        String sql = "select extend1 as id from basic_memory where type = 2 and code = '"+tablename+"' ";
        MySqlConnection conn = tools.getConn();
        MySqlCommand comd = new MySqlCommand(sql);
        comd.Connection = conn;
        MySqlDataReader rd = comd.ExecuteReader();
        rd.Read();
        id = rd.GetInt32("id");
        conn.Close();
        return id;
    }

    public static XmlDocument xmlConfig = null;
    public static String configXML = null;
    public static String getConfigItem(String id)
    {
        String s_return = "";
        if (tools.configXML == null)
        {
            tools.configXML = "";
            String path = tools.webPath + "\\config.xml";
            TextReader fr = new StreamReader(path);
            String strLine = fr.ReadLine();
            while (strLine != null)
            {
                tools.configXML += strLine;
                strLine = fr.ReadLine();
            }

            xmlConfig = new XmlDocument();
            xmlConfig.LoadXml(configXML);
        }
        XmlElement e = xmlConfig.GetElementById(id);
        s_return = e.InnerText;
        return s_return;
    }

    public static ArrayList list2Tree(ArrayList a_list)
    {
        ArrayList a_return = new ArrayList();

        for (int i = 0; i < a_list.Count; i++)
        {
            Hashtable t = (Hashtable)a_list[i];
            int len = ((String)t["code"]).Length;

            int pos_1, pos_2, pos_3, pos_4, pos_5, pos_6 = 0;

            if (len == 2)
            {
                a_return.Add(t);
            }
            else if (len == 4)
            {
                pos_1 = a_return.Count - 1;

                Hashtable t_ = (Hashtable)a_return[pos_1];

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    a_.Add(t);
                    t_["children"] = a_;
                }
                else
                {
                    a_.Add(t);
                    t_.Add("children", a_);
                }      
               

                a_return[pos_1] = t_;
            }
            else if (len == 6)
            {
                pos_1 = a_return.Count - 1;
                pos_2 = ((ArrayList)( (Hashtable)a_return[pos_1])["children"]).Count - 1;

                Hashtable t_ = (Hashtable)(((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2]);

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    a_.Add(t);
                    t_["children"] = a_;
                }
                else
                {
                    a_.Add(t);
                    t_.Add("children", a_);
                } 

                ((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2] = t_;
                        
            }
            else if (len == 8)
            {
                pos_1 = a_return.Count - 1;
                pos_2 = ((ArrayList)((Hashtable)a_return[pos_1])
                        ["children"]).Count - 1;
                pos_3 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2])
                        ["children"]).Count - 1;

                Hashtable t_ = (Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2])["children"])[pos_3];

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    a_.Add(t);
                    t_["children"] = a_;
                }
                else
                {
                    a_.Add(t);
                    t_.Add("children", a_);
                } 

                ((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2])
                        ["children"])[pos_3]= t_;
            }
            else if (len == 10)
            {
                pos_1 = a_return.Count - 1;
                pos_2 = ((ArrayList)((Hashtable)a_return[pos_1])["children"]).Count - 1;
                pos_3 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2])["children"]).Count - 1;
                pos_4 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return[pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"]).Count - 1;

                Hashtable t_ = (Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4];

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    a_.Add(t);
                    t_["children"] = a_;
                }
                else
                {
                    a_.Add(t);
                    t_.Add("children", a_);
                } 

                ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])[pos_4]=t_;
            }
            else if (len == 12)
            {
                pos_1 = a_return.Count - 1;
                pos_2 = ((ArrayList)((Hashtable)a_return[pos_1])
                        ["children"]).Count - 1;
                pos_3 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"]).Count - 1;
                pos_4 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"]).Count - 1;
                pos_5 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"]).Count - 1;

                Hashtable t_ = (Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"])[pos_5];

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    t_["children"] = a_;
                }
                else
                {
                    t_.Add("children", a_);
                }
                a_.Add(t);

                ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"])[pos_5]= t_;
            }
            else if (len == 14)
            {
                pos_1 = a_return.Count - 1;
                pos_2 = ((ArrayList)((Hashtable)a_return[pos_1])
                        ["children"]).Count - 1;
                pos_3 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"]).Count - 1;
                pos_4 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"]).Count - 1;
                pos_5 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"]).Count - 1;
                pos_6 = ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"])[pos_5])
                        ["children"]).Count - 1;

                Hashtable t_ = (Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"])[pos_5])
                        ["children"])[pos_6];

                ArrayList a_ = new ArrayList();
                if (t_.ContainsKey("children"))
                {
                    a_ = (ArrayList)t_["children"];
                    t_["children"] = a_;
                }
                else
                {
                    t_.Add("children", a_);
                }
                a_.Add(t);

                ((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)((ArrayList)((Hashtable)a_return
                        [pos_1])["children"])[pos_2])
                        ["children"])[pos_3])["children"])
                        [pos_4])["children"])[pos_5])
                        ["children"])[pos_6]=t_;
            }
        }

        return a_return;
    }

    public static String MD5_(String input)
    {
        // Create a new instance of the MD5CryptoServiceProvider object.
        MD5 md5Hasher = MD5.Create();

        // Convert the input string to a byte array and compute the hash.
        byte[] data = md5Hasher.ComputeHash(Encoding.Default.GetBytes(input));

        // Create a new Stringbuilder to collect the bytes
        // and create a string.
        StringBuilder sBuilder = new StringBuilder();

        // Loop through each byte of the hashed data 
        // and format each one as a hexadecimal string.
        for (int i = 0; i < data.Length; i++)
        {
            sBuilder.Append(data[i].ToString("x2"));
        }

        // Return the hexadecimal string.
        return sBuilder.ToString();
    }

    public static String randomName()
    {
        Random rand = new Random();
        String name = "";
        String name_1 = "赵钱孙李周吴郑王冯陈楮卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤";
        String name_2 = "安邦安福安歌安国安和安康安澜安民安宁安平安然安顺"
            + "宾白宾鸿宾实彬彬彬炳彬郁斌斌斌蔚滨海波光波鸿波峻"
            + "才捷才良才艺才英才哲才俊成和成弘成化成济成礼成龙"
            + "德本德海德厚德华德辉德惠德容德润德寿德水德馨德曜"
            + "飞昂飞白飞飙飞掣飞尘飞沉飞驰飞光飞翰飞航飞翮飞鸿"
            + "刚豪刚洁刚捷刚毅高昂高岑高畅高超高驰高达高澹高飞"
            + "晗昱晗日涵畅涵涤涵亮涵忍涵容涵润涵涵涵煦涵蓄涵衍"
            + "嘉赐嘉德嘉福嘉良嘉茂嘉木嘉慕嘉纳嘉年嘉平嘉庆嘉荣"
            + "开畅开诚开宇开济开霁开朗凯安凯唱凯定凯风凯复凯歌"
            + "乐安乐邦乐成乐池乐和乐家乐康乐人乐容乐山乐生乐圣"
            + "茂才茂材茂德茂典茂实茂学茂勋茂彦敏博敏才敏达敏叡"
            + "朋兴朋义彭勃彭薄彭湃彭彭彭魄彭越彭泽彭祖鹏程鹏池";

        int name_1_ = (int)(name_1.Length * rand.NextDouble());
        int name_2_ = (int)((name_2.Length - 2) * rand.NextDouble());
        name = name_1.Substring(name_1_, 1) + name_2.Substring(name_2_,  2);

        return name;
    }

    static void Main(string[] args)
    {
        //To Do
    }
}