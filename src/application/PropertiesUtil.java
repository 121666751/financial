package application;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 属性工具类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:55:57
 */
public class PropertiesUtil {
	
	public static void main(String[] arss){
		try {
			String clubId = PropertiesUtil.readProperty("clubId");
			System.out.println(clubId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    private static String properiesName = "usrInfo.properties";


    public static String readProperty(String key) {
        String value = "";
        InputStream is = null;
        try {
//            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(properiesName);
            is = PropertiesUtil.class.getResourceAsStream(properiesName);  
            Properties p = new Properties();
            p.load(is);
            value = p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static Properties getProperties() {
        Properties p = new Properties();
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getResourceAsStream(properiesName);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public static void writeProperty(String key, String value) {
        InputStream is = null;
        OutputStream os = null;
        Properties p = new Properties();
        try {
            is = new FileInputStream(PropertiesUtil.class.getResource(properiesName).getFile());
            p.load(is);
            os = new FileOutputStream(PropertiesUtil.class.getResource(properiesName).getFile());

            p.setProperty(key, value);
            p.store(os, key);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
                if (null != os)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}