package org.ltd3000.jasperprinter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * @author xushanshan
 * @date 181221
 * @class FTP工具类
 */
public class FtpUtils {
	private static final Logger logger = Logger.getLogger("FTPUtil");

	/**
	 * 获取FTPClient对象 
	 */
	//TODO 发布强请更改服务器地址
	public static FTPClient getFTPClient() {
		FTPClient ftpClient = new FTPClient();
		try {
			String ip=ConfigUtil.getProperty("center").equals("")?"172.20.70.32":ConfigUtil.getProperty("center");
			// 连接FTP服务器 端口默认21
			ftpClient.connect(ip,21);
			ftpClient.login("administrator", "feiliks2018()");// 登陆FTP服务器
			ftpClient.setControlEncoding("UTF-8"); // 中文支持
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				logger.error("连接到服务器失败，请检查防火墙规则或者用户名密码设置。");
				ftpClient.disconnect();
			}
		} catch (SocketException e) {
			e.printStackTrace();
			logger.error("FTP的IP地址可能错误，请正确配置。");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("FTP的端口错误,请正确配置。");
		}
		return ftpClient;
	}

	/**
	 * @ function 获取服务器端任务
	 */
	public static void downloadXmlTask() {

		FTPClient ftpClient = getFTPClient();
		try {
			ftpClient.changeWorkingDirectory("deliver");
			File localDir = new File(ConfigUtil.getProperty("xmlpath"));
			if (!localDir.exists()) {
				localDir.mkdir();
			}
			FTPFile[] fileList = ftpClient.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String xmlPrintername = DeliverUtil.getPrinterName(fileList[i].getName());
				if (DeliverUtil.isClientPrinter(xmlPrintername)) {
					// download and delete XML task
					File localFile = new File(localDir.getPath() + File.separatorChar + fileList[i].getName());
					OutputStream os = new FileOutputStream(localFile);
					ftpClient.retrieveFile(fileList[i].getName(), os);
					os.close();
					logger.info("获取Task成功：" + fileList[i].getName());
					ftpClient.deleteFile(fileList[i].getName());
				}
			}
			ftpClient.logout();

		} catch (FileNotFoundException e) {
			logger.error("没有找到文件");
			e.printStackTrace();
		} catch (SocketException e) {
			logger.error("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("文件读取错误。");
			e.printStackTrace();
		}

	}

	public static boolean checkLateastVersion() {
		FTPClient ftpClient = getFTPClient();

		try {
			ftpClient.changeWorkingDirectory("jar");
			File localDir = new File(ConfigUtil.getCurrentPath());
			if (!localDir.exists()) {
				localDir.mkdir();
			}
			double localversion = Double.parseDouble(
					ConfigUtil.getProperty("version").equals("") ? "1.0" : ConfigUtil.getProperty("version"));// 版本
			FTPFile[] fileList = ftpClient.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String filename = fileList[i].getName();
				if (filename.endsWith(".jar")) {
					if (filename.contains("-")) {
						String beginString = filename.substring(0, filename.indexOf("-") + 1);
						if ("jasperprint-".equalsIgnoreCase(beginString)) {
							double b = Double.parseDouble(
									filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf(".")));
							if (b > localversion) {
								return true;
							}
						}
					}

				}

			}
			return false;
		} catch (FileNotFoundException e) {
			logger.error("没有找到文件");
			e.printStackTrace();
		} catch (SocketException e) {
			logger.error("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("文件读取错误。");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @dowload update files
	 * @param ftpHost
	 * @param ftpUserName
	 * @param ftpPassword
	 * @param ftpPort
	 */
	public static void downloadAllUpdateFile() {

		FTPClient ftpClient = getFTPClient();
		try {
			ftpClient.changeWorkingDirectory("jar");
			File localDir = new File(ConfigUtil.getCurrentPath());
			if (!localDir.exists()) {
				localDir.mkdir();
			}
			//
			double version = Double.parseDouble(
					ConfigUtil.getProperty("version").equals("") ? "1.0" : ConfigUtil.getProperty("version"));// 版本
			String begin = "";
			String end = "";

			FTPFile[] fileList = ftpClient.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String filename = fileList[i].getName();
				if (filename.endsWith(".jar")) {
					if (filename.contains("-")) {
						String beginString = filename.substring(0, filename.indexOf("-") + 1);

						if ("jasperprint-".equalsIgnoreCase(beginString)) {
							double b = Double.parseDouble(
									filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf(".")));
							if (b > version) {
								begin = beginString;
								version = b;
								end = filename.substring(filename.lastIndexOf("."), filename.length());
							}
						}
					} else if ("update.jar".equalsIgnoreCase(filename)) {
						String localupdate = "update.jar";
						if (new File(localupdate).exists()) {
							new File(localupdate).delete();
						}
						OutputStream os = new FileOutputStream(filename);
						ftpClient.retrieveFile(fileList[i].getName(), os);
						os.close();
					}

				}

			}
			ConfigUtil.setProperty("version", version + "");
			String localjar = begin + version + end;
			OutputStream os = new FileOutputStream(localjar);
			ftpClient.retrieveFile(localjar, os);
			os.close();
			ftpClient.logout();
		} catch (FileNotFoundException e) {
			logger.error("没有找到文件");
			e.printStackTrace();
		} catch (SocketException e) {
			logger.error("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("文件读取错误。");
			e.printStackTrace();
		}

	}

	/**
	 * @function 更新所有打印模板
	 */
	public static boolean downloadAllJasperFile() {

		FTPClient ftpClient = getFTPClient();
		try {
			ftpClient.changeWorkingDirectory("jasper");
			File localDir = new File(ConfigUtil.getProperty("rptpath"));
			if (!localDir.exists()) {
				localDir.mkdir();
			}			

			FTPFile[] fileList = ftpClient.listFiles();

			for (int i = 0; i < fileList.length; i++) {
				// download and delete
				File localFile = new File(localDir.getPath() + File.separatorChar + fileList[i].getName());
				if (localFile.exists()) {
					localFile.delete();
				}
				OutputStream os = new FileOutputStream(localFile);
				ftpClient.retrieveFile(fileList[i].getName(), os);
				os.close();
				logger.info("更新打印模板成功：" + fileList[i].getName());
			}
			ftpClient.logout();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("没有找到文件");
			e.printStackTrace();
		} catch (SocketException e) {
			logger.error("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("文件读取错误。");
			e.printStackTrace();
		}
		return false;

	}

}
