package org.ltd3000.jasperprinter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author xushanshan
 * @date 181221 
 * @class 系统工具类
 *
 */
public class ConfigUtil {

	private static final String SYSCONFIGFILE = "./sys.properties";// 系统配置文件

	private static final Logger log = Logger.getLogger(ConfigUtil.class);// LOG

	/**
	 * @param name
	 * @function 获取系统配置的值
	 */
	public static String getProperty(String name) {

		Properties prop = new Properties();
		FileInputStream in = null;
		try {
			File f = new File(SYSCONFIGFILE);
			if (f.exists()) {
				in = new FileInputStream(SYSCONFIGFILE);
				prop.load(in);
		
				return prop.getProperty(name, "");
			} else {
				log.error("未找到配置文件");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";

	}
	
	/**
	 * @param name
	 * @param value
	 * @function 设置系统配置属性
	 */
	public static void setProperty(String name, String value) {
		
		Properties prop = new Properties();
		FileInputStream in = null;
		FileOutputStream oFile = null;
		try {
			File f = new File(SYSCONFIGFILE);
			if (f.exists()) {
				in = new FileInputStream(SYSCONFIGFILE);
				prop.load(in);
				prop.setProperty(name, value);
				oFile = new FileOutputStream(SYSCONFIGFILE, false);
				prop.store(oFile, "");
				oFile.close();
			} else {
				log.error("未找到配置文件");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * @param name
	 * @function 删除配置项
	 */
	public static void removeProperty(String name) {
		
		Properties prop = new Properties();
		FileInputStream in = null;
		FileOutputStream oFile = null;
		try {
			File f = new File(SYSCONFIGFILE);
			if (f.exists()) {
				in = new FileInputStream(SYSCONFIGFILE);
				prop.load(in);
				prop.remove(name);
				oFile = new FileOutputStream(SYSCONFIGFILE, false);
				prop.store(oFile, "");
				oFile.close();				
			} else {
				log.error("未找到配置文件");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	
	}
	public static String getCurrentPath() {
		String path = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		return path.substring(1, path.lastIndexOf("/"));

	}

	/**
	 * @function 设置系统默认工作路径,路径为jasperprinter.jar的同目录
	 */
	public static boolean setDefaultPath() {
		String path = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = path.substring(1, path.lastIndexOf("/"));
		
		setProperty("xmlpath", path + "/xml");
		File xmlpath = new File(path + "/xml");
		log.info("初始化XML路径为："+xmlpath.getAbsolutePath());
		if (!xmlpath.exists()) {
			xmlpath.mkdirs();
		}
		
		setProperty("pdfpath", path + "/pdf");
		File pdfpath = new File(path + "/pdf");
		log.info("初始化PDF路径为："+pdfpath.getAbsolutePath());
		if (!pdfpath.exists()) {
			pdfpath.mkdirs();
		}
		
		setProperty("rptpath", path + "/jasper");
		File jasperpath = new File(path + "/jasper");
		log.info("初始化Jasper模板路径为："+jasperpath.getAbsolutePath());
		if (!jasperpath.exists()) {
			jasperpath.mkdirs();
		}
		
		setProperty("stationpath", path + "/station");
		File stationpath = new File(path + "/station");
		log.info("初始化工作区station路径为："+stationpath.getAbsolutePath());
		if (!stationpath.exists()) {
			stationpath.mkdirs();
		}
		return true;
	}
	public static boolean checkXMLTask(XMLTaskParam xmlP) {
		if("".equals(xmlP.getOrderkey())) {
			return false;
		}
		return true;

	}
	public static void main(String[] args) {

	}

}
