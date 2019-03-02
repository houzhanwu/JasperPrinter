package org.ltd3000.jasperprinter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.printer.DatabasePrinter;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.JdbcEntity;
import org.ltd3000.jasperprinter.utils.JdbcUtil;


public class PrintDatabaseService extends PrintService {

	// 数据库连接路径
	private final String URLPATH = "urlpath";
	private String urlPath = "";
	private String printType = "";
	// 打印机清单
	public static Map<String, DatabasePrinter> allPrinter = new HashMap<String, DatabasePrinter>();
	// 打印服务
	private static PrintDatabaseService instance = null;
	//日志
	private static final Logger log = Logger.getLogger("DataBasePrintService");

	private PrintDatabaseService() {
		log.info("当前打印服务类型为数据库打印。");
		//加载配置
		loadConfig();
		//加载打印机
		loadPrinter();
		//数据库预处理
		prepareDatabase();
		//启动打印服务
		this.setServiceStatus(true);
		//启动清理PDF线程
		startCleanThread();
	}

	/**
	 * @数据库预处理，监测数据库连接，初始化数据库结构
	 */
	private void prepareDatabase() {
		String url = this.getUrlPath();
		if (!url.isEmpty()) {
			JdbcEntity jdbc = JdbcUtil.parseJdbcUrl(ConfigUtil.getProperty("urlpath"));
			if (!jdbc.getDriverClass().isEmpty()) {
				try {
					Class.forName(jdbc.getDriverClass());
					try {
						Connection conn = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUserNmae(),
								jdbc.getPassWorld());
						ResultSet rs = conn.getMetaData().getTables(null, null, "JASPERTASK", null);
						if (!rs.next()) {// 表不存在
							Statement stmtTask = conn.createStatement();
							stmtTask.execute(
									"create table JASPERTASK(  id NVARCHAR2(20) not null,  sql NVARCHAR2(2000),  copies NVARCHAR2(100),  jasper NVARCHAR2(100),  status    NVARCHAR2(10),  printdate NVARCHAR2(20),  printname NVARCHAR2(120))");
							stmtTask.close();
							conn.close();
						}
					} catch (SQLException e) {
						log.error("连接异常");
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					log.error("未找到驱动文件");
					e.printStackTrace();
				}

			} else {
				log.error("JDBC 驱动类未找到！");
			}
		} else {
			log.error("JDBC连接未配置！");
		}

	}

	@Override
	public void loadConfig() {
		Properties prop = new Properties();
		FileInputStream in = null;
		File directory = new File("");
		try {
			String currentpath = directory.getAbsolutePath();
			File f = new File(SYSCONFIGFILE);
			if (!f.exists()) {
				this.rptPath = currentpath + "\\label";
				this.stationPath = currentpath + "\\station";
				this.pdfPath = currentpath + "\\pdfpath";
				this.printType = "databse";
				this.urlPath = "";
			} else {
				in = new FileInputStream(SYSCONFIGFILE);
				prop.load(in);
				this.rptPath = prop.getProperty(RPTPATH, currentpath + "\\label");
				this.stationPath = prop.getProperty(STATIONPATH, currentpath + "\\station");
				this.pdfPath = prop.getProperty(PDFPATH, currentpath + "\\pdf");
				this.urlPath = prop.getProperty(URLPATH, "");
				this.printType = prop.getProperty("printtype", "");
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

	/*
	 * 加载打印机
	 */
	public void loadPrinter() {
		Properties prop = new Properties();
		FileInputStream in = null;
		try {
			File f = new File(PRINTERCONFIGFILE);
			if (f.exists()) {
				in = new FileInputStream(PRINTERCONFIGFILE);
				prop.load(in);
				Iterator<String> it = prop.stringPropertyNames().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = prop.getProperty(key);
					addPrinter(key, value);
				}
			}
			log.info("加载打印机文件");
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

	public String getPrinttype() {
		return printType;
	}

	public void setPrinttype(String printType) {
		this.printType = printType;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
		File pdfPathFile = new File(pdfPath);
		if (!pdfPathFile.exists()) {
			pdfPathFile.mkdirs();
		}
	}

	public String getRptPath() {
		return rptPath;
	}

	public void setRptPath(String rptPath) {
		this.rptPath = rptPath;
		File rptPathFile = new File(rptPath);
		if (!rptPathFile.exists()) {
			rptPathFile.mkdirs();
		}
	}

	public String getLogPath() {
		return stationPath;
	}

	public void setLogPath(String logPath) {
		this.stationPath = logPath;
		File logPathFile = new File(logPath);
		if (!logPathFile.exists()) {
			logPathFile.mkdirs();
		}
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public void addPrinter(String printerName, String osprintername) {
		if (!allPrinter.containsKey(printerName.toUpperCase())) {
			DatabasePrinter xmlPrinter = new DatabasePrinter(printerName, this);
			xmlPrinter.setOsPrinterName(osprintername);
			allPrinter.put(printerName, xmlPrinter);
			if ("database".equalsIgnoreCase(getPrinttype())) {
				xmlPrinter.startDatabasePrintThread();
			} else {
				xmlPrinter.startDatabasePrintThread();
			}

		}
	}

	public boolean addPrinterAction(String printerName, String osprintername) {
		Properties prop = new Properties();
		File f = new File(PRINTERCONFIGFILE);
		FileInputStream in = null;
		FileOutputStream oFile = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			in = new FileInputStream(f);
			prop.load(in);
			prop.setProperty(printerName, osprintername);
			oFile = new FileOutputStream(PRINTERCONFIGFILE, false);
			prop.store(oFile, "");
			oFile.close();

			addPrinter(printerName, osprintername);
			return true;
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		} finally {
			if (oFile != null) {
				try {
					oFile.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void removePrinter(String printerName) {
		if (allPrinter.containsKey(printerName)) {
			DatabasePrinter xmlPrinter = (DatabasePrinter) allPrinter.get(printerName);
			xmlPrinter.setStopThread(true);
			allPrinter.remove(printerName);
		}
	}

	public boolean removePrinterAction(String printerName) {
		Properties prop = new Properties();
		File f = new File(PRINTERCONFIGFILE);
		FileInputStream in = null;
		FileOutputStream oFile = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			in = new FileInputStream(f);
			prop.load(in);
			prop.remove(printerName);
			oFile = new FileOutputStream(PRINTERCONFIGFILE, false);
			prop.store(oFile, "");
			oFile.close();

			removePrinter(printerName);
			return true;
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		} finally {
			if (oFile != null) {
				try {
					oFile.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public static PrintDatabaseService getInstance() {
		if (instance == null) {
			instance = new PrintDatabaseService();
		}
		return instance;
	}

	public boolean getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(boolean active) {
		this.serviceStatus = active;
	}

	public void saveConfigPath(String labelPath, String logPath, String pdfPath, String urlPath) {

		this.setRptPath(labelPath);
		this.setLogPath(logPath);
		this.setPdfPath(pdfPath);
		this.setUrlPath(urlPath);

		Properties prop = new Properties();
		File f = new File(this.SYSCONFIGFILE);
		FileInputStream in = null;
		FileOutputStream oFile = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			in = new FileInputStream(f);
			prop.load(in);
			prop.setProperty(RPTPATH, labelPath);
			prop.setProperty(STATIONPATH, logPath);
			prop.setProperty(PDFPATH, pdfPath);
			prop.setProperty(URLPATH, urlPath);
			oFile = new FileOutputStream(SYSCONFIGFILE, false);
			prop.store(oFile, "");
			oFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oFile != null) {
				try {
					oFile.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public boolean savePrinter(String printerName, String osprintername) {

		if (PrintDatabaseService.allPrinter.containsKey(printerName)) {
			return true;
		}
		return this.addPrinterAction(printerName, osprintername);
	}

	public Object[][] getPrinterModel() {

		Set<String> keys = PrintDatabaseService.allPrinter.keySet();
		Object[][] obj = new Object[keys.size()][4];
		int i = 0;
		DatabasePrinter p = null;
		for (String key : keys) {
			obj[i][0] = i + 1;
			obj[i][1] = key;
			p = (DatabasePrinter) PrintDatabaseService.allPrinter.get(key);
			obj[i][2] = p.getOsPrinterName();
			obj[i][3] = p.isPrinterActive() ? "Y" : "N";
			i++;
		}
		return obj;
	}

	/**
	 * @return
	 * @获取打印状态数据
	 */
	public Object[][] getPrinterStatusModel() {
		
		Set<String> keys = PrintDatabaseService.allPrinter.keySet();
		Object[][] obj = new Object[keys.size() == 0 ? 1 : keys.size()][4];
		int i = 0;
		DatabasePrinter p = null;
		for (String key : keys) {
			obj[i][0] = i + 1;
			obj[i][1] = key;
			p = PrintDatabaseService.allPrinter.get(key);
			obj[i][2] = p.getStatus();
			Map<String, Integer> m = p.getStatusTotal();
			Set<String> mk = m.keySet();
			String memo = "";
			for (String k : mk) {
				memo += k + "=" + m.get(k).intValue() + ";";
			}
			obj[i][3] = memo;
			i++;
		}
		if (i == 0) {
			obj[0][0] = "";
			obj[0][1] = "";
			obj[0][2] = "";
			obj[0][3] = "";
		}
		return obj;
	}

	/**
	 * @param printerName
	 * @设置打印机服务状态
	 */
	public void setActive(String printerName, boolean b) {

		if (PrintDatabaseService.allPrinter.containsKey(printerName)) {
			DatabasePrinter p = (DatabasePrinter) allPrinter.get(printerName);
			p.setPrinterActive(b);
		}
	}

	
	/**
	 * @ 启动清理PDF线程
	 */
	public void startCleanThread() {
		cleanPdfThread backThread = new cleanPdfThread();
		backThread.start();
	}

	/**
	 * @author xushanshan
	 * @清理Pdf线程
	 */
	public class cleanPdfThread extends Thread {
		@Override
		public void run() {
			log.info("清理文件线程启动。");
			while (true) {
				try {
					cleanPdfFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(600 * 1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @清理PDF文件夹
	 */
	public synchronized void cleanPdfFile() {
		String pdfPath = getPdfPath();
		File dir = new File(pdfPath);
		if (!dir.exists())
			return;
		if (!dir.isDirectory())
			return;
		File[] allFile = dir.listFiles();
		Calendar currentDate = Calendar.getInstance();
		currentDate.add(Calendar.DATE, ConfigUtil.getProperty("deletepdf").equals("") ? -1
				: Integer.parseInt(ConfigUtil.getProperty("deletepdf")) * -1);
		for (File f : allFile) {
			Calendar createDate = Calendar.getInstance();
			createDate.setTimeInMillis(f.lastModified());
			if (currentDate.after(createDate)) {
				while (f.exists()) {
					try {
						f.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
