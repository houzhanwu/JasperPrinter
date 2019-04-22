package org.ltd3000.jasperprinter.service;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.printer.XmlPrinter;
import org.ltd3000.jasperprinter.utils.DeliverUtil;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.FtpUtils;

public class PrintXmlService extends PrintService {

	// 默认xmlPath：currentPath/xml
	private final String XMLPATH = "xmlpath";
	private String xmlPath = "/xml";

	// 打印服务
	public static PrintXmlService instance = null;
	// 打印机清单
	public static Map<String, XmlPrinter> allPrinter = new HashMap<String, XmlPrinter>();

	private static final Logger log = Logger.getLogger("XMLPrintService");

	private PrintXmlService() {
		log.info("打印服务类型为XML打印");
		loadConfig();// 加载工作区配置
		loadPrinter();// 加载打印机
		this.setServiceStatus(true);//启动服务
		startCleanPDFThread();//启动PDF清理线程
		startCleanXMLThread();//启动XML清理线程
		startCleanBakThread();//启动备份task文件清理线程
		if ("master".equalsIgnoreCase(ConfigUtil.getProperty("mode"))) {
			startDeliverThread();
			log.info("打印模式为服务器");
		} else if ("slave".equalsIgnoreCase(ConfigUtil.getProperty("mode"))) {
			startClientThread();
			startUpdateThread();
			log.info("打印模式为客户端");
		}
		//自动更新模板
		new Thread() {
			@Override
			public void run() {
				if (FtpUtils.downloadAllJasperFile()) {
					log.info("同步模板完成!");
				} else {
					log.info("同步模板失败，请查看日志");
				}
			}
		}.start();
	}

	// 加载工作区配置
	@Override
	public void loadConfig() {
		Properties prop = new Properties();
		FileInputStream in = null;
		try {
			String currentpath = new File("").getAbsolutePath();
			File f = new File(SYSCONFIGFILE);
			if (!f.exists()) {
				this.xmlPath = currentpath + "/xml";
				this.rptPath = currentpath + "/label";
				this.stationPath = currentpath + "/station";
				this.pdfPath = currentpath + "/pdfpath";
			} else {
				in = new FileInputStream(SYSCONFIGFILE);
				prop.load(in);
				this.xmlPath = prop.getProperty(XMLPATH, currentpath + "/xml");
				this.rptPath = prop.getProperty(RPTPATH, currentpath + "/label");
				this.stationPath = prop.getProperty(STATIONPATH, currentpath + "/log");
				this.pdfPath = prop.getProperty(PDFPATH, currentpath + "/pdf");
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
					//验证本地打印机是否存在,并且给出报警
                   javax.print.PrintService[] pss = PrinterJob.lookupPrintServices();
                   javax.print.PrintService ps = null;
					for (int i = 0; i < pss.length; i++) {
						if (value.equalsIgnoreCase(pss[i].getName())) {
							ps = pss[i];
							break;
						}
					}
					if (ps == null) {
						log.error("未找到系统打印机:["+value+"]");
						JOptionPane.showMessageDialog(null,"系统打印机["+value+"]不存在，程序仍将运行，但此打印机将不可用");
					}
					//					
					addPrinter(key, value);
				}
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

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
		File xmlPathFile = new File(xmlPath);
		if (!xmlPathFile.exists()) {
			xmlPathFile.mkdirs();
		}
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

	public String getStationPath() {
		return stationPath;
	}

	public void setStationPath(String stationPath) {
		this.stationPath = stationPath;
		File logPathPath = new File(stationPath);
		if (!logPathPath.exists()) {
			logPathPath.mkdirs();
		}
	}
    //添加打印机
	public void addPrinter(String printerName, String osprintername) {
		//避免重复添加
		if (!allPrinter.containsKey(printerName.toUpperCase())) {
			
			XmlPrinter xmlPrinter = new XmlPrinter(printerName, this);
			xmlPrinter.setOsPrinterName(osprintername);
			allPrinter.put(printerName, xmlPrinter);
			xmlPrinter.startXmlPrintThread();//启动打印线程
			
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
			XmlPrinter xmlPrinter = (XmlPrinter) allPrinter.get(printerName);
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

	public static PrintXmlService getInstance() {
		if (instance == null) {
			instance = new PrintXmlService();
		}
		return instance;
	}

	public boolean getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(boolean active) {
		this.serviceStatus = active;
	}

	public void saveConfigPath(String xmlPath, String labelPath, String logPath, String pdfPath) {

		this.setXmlPath(xmlPath);
		this.setRptPath(labelPath);
		this.setStationPath(logPath);
		this.setPdfPath(pdfPath);

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
			prop.setProperty(XMLPATH, xmlPath);
			prop.setProperty(RPTPATH, labelPath);
			prop.setProperty(STATIONPATH, logPath);
			prop.setProperty(PDFPATH, pdfPath);
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

		if (PrintXmlService.allPrinter.containsKey(printerName)) {
			return true;
		}
		return this.addPrinterAction(printerName, osprintername);
	}

	public Object[][] getPrinterModel() {

		Set<String> keys = PrintXmlService.allPrinter.keySet();
		Object[][] obj = new Object[keys.size()][4];
		int i = 0;
		XmlPrinter p = null;
		for (String key : keys) {
			obj[i][0] = i + 1;
			obj[i][1] = key;
			p = (XmlPrinter) PrintXmlService.allPrinter.get(key);
			obj[i][2] = p.getOsPrinterName();
			obj[i][3] = p.isPrinterActive() ? "Y" : "N";
			i++;
		}
		return obj;
	}

	public Object[][] getPrinterStatusModel() {

		Set<String> keys = PrintXmlService.allPrinter.keySet();
		Object[][] obj = new Object[keys.size() == 0 ? 1 : keys.size()][4];
		int i = 0;
		XmlPrinter p = null;
		for (String key : keys) {
			obj[i][0] = i + 1;
			obj[i][1] = key;
			p = PrintXmlService.allPrinter.get(key);
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

	public void setActive(String printerName, boolean b) {

		if (PrintXmlService.allPrinter.containsKey(printerName)) {
			XmlPrinter p = (XmlPrinter) allPrinter.get(printerName);
			p.setPrinterActive(b);
		}
	}

	// 清理PDF文件线程
	public void startCleanPDFThread() {
		_BackThread backThread = new _BackThread();
		backThread.start();
	}

	// 清理XML文件线程
	public void startCleanXMLThread() {
		_CleanXmlThread cleanXmlThread = new _CleanXmlThread();
		cleanXmlThread.start();
	}

	// 清理bak文件线程
	public void startCleanBakThread() {
		_CleanStationThread cleanStationThread = new _CleanStationThread();
		cleanStationThread.start();
	}

	// 分发文件线程
	public void startDeliverThread() {
		_deliverThread deliverThread = new _deliverThread();
		deliverThread.start();
	}

	// 获取文件线程
	public void startClientThread() {
		_downloadTaskThread clientThread = new _downloadTaskThread();
		clientThread.start();
	}

	// 检查更新
	public void startUpdateThread() {
		_updateThread updateThread = new _updateThread();
		updateThread.start();
	}

	public class _updateThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					if (FtpUtils.checkLateastVersion()) {
						JOptionPane.showMessageDialog(null, "打印程序有更新，请及时更新！", "jasper提示信息",
								JOptionPane.WARNING_MESSAGE);
						break;
					}
					Thread.sleep(3600000);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public class _BackThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					deleteOldPDFFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(6000000);// deletepdf
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class _CleanXmlThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					deleteStandardlessFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//100分钟清理一次
					Thread.sleep(6000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class _CleanStationThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String stationPath = getStationPath();
					cleanStation(stationPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//1天清理
					Thread.sleep(144000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void deleteOldPDFFile() {
		String pdfPath = getPdfPath();
		File dir = new File(pdfPath);
		if (!dir.exists())
			return;
		if (!dir.isDirectory())
			return;
		File[] allFile = dir.listFiles();
		Calendar currentDate = Calendar.getInstance();
		currentDate.add(Calendar.DATE, 
				"".equals(ConfigUtil.getProperty("pdfclean")) ? -30 :Integer.parseInt(ConfigUtil.getProperty("pdfclean")) * -1);
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

	/**
	 * delete bak
	 */
	public void cleanStation(String cleanPath) {

		File dir = new File(cleanPath + "/bak");
		if (!dir.exists()) {
			return;
		}
		if (!dir.isDirectory()) {
			return;
		}
		File[] allFile = dir.listFiles();

		for (File f : allFile) {
			Calendar currentDate = Calendar.getInstance();
			currentDate.add(Calendar.DATE, (ConfigUtil.getProperty("stationclean").equals("") ? -30
					: (Integer.parseInt(ConfigUtil.getProperty("stationclean")) * -1)));
			Calendar createDate = Calendar.getInstance();
			createDate.setTimeInMillis(f.lastModified());
			if (currentDate.after(createDate) || !f.getName().contains(".xml")) {
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

	/**
	 * delete jobs which is not standard
	 */
	public synchronized void deleteStandardlessFile() {
		String pdfPath = getXmlPath();
		File dir = new File(pdfPath);
		if (!dir.exists()) {
			return;
		}
		if (!dir.isDirectory()) {
			return;
		}
		File[] allFile = dir.listFiles();
		Calendar currentDate = Calendar.getInstance();
		currentDate.add(Calendar.DATE, -1);
		for (File f : allFile) {
			Calendar createDate = Calendar.getInstance();
			createDate.setTimeInMillis(f.lastModified());
			if (currentDate.after(createDate) || !f.getName().contains(".xml")) {
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

	/**
	 * @author Administrator
	 * @function 分发文件到客户端读取区
	 */
	public class _deliverThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String path = ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					path = path.substring(1, path.lastIndexOf("/"));
					String deliver_xml_path = ConfigUtil.getProperty("deliverpath").equals("") ? path + "/deliver"
							: ConfigUtil.getProperty("deliverpath");
					File fd = new File(deliver_xml_path);
					if (!fd.exists()) {
						fd.mkdirs();
					}
					File[] findFile = new File(xmlPath).listFiles();
					if (findFile != null) {
						// 移动
						for (int i = 0; i < findFile.length; i++) {
							String printerName = DeliverUtil.getPrinterName(findFile[i].getName());
							if (DeliverUtil.isClientPrinter(printerName)) {
								// move to
								addToDeliverSequence(findFile[i]);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @author deski
	 * @function 获取服务端打印任务
	 *
	 */
	public class _downloadTaskThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					FtpUtils.downloadXmlTask();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * @function 将任务加入分布式式打印序列
	 */
	public String addToDeliverSequence(File xml) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		String Taskname = (ConfigUtil.getProperty("deliverpath").equals("")?stationPath+"/deliver":ConfigUtil.getProperty("deliverpath")) + "/" + xml.getName();
		try {
			if (xml.exists()) {
				inStream = new FileInputStream(xml);

				File desFile = new File(Taskname);
				if (!desFile.getParentFile().exists()) {
					desFile.getParentFile().mkdirs();
				}
				if (desFile.exists()) {
					desFile.delete();
				}
				fs = new FileOutputStream(Taskname);
				SAXReader reader = new SAXReader();
				org.dom4j.Document doc = reader.read(inStream);
				doc.setDocType(null);
				XMLWriter writer = new XMLWriter(fs);
				writer.write(doc);
				writer.flush();
				writer.close();
				log.info("加入分发队列：" + xml.getName());
				DBUtil.addJasperCount();
				inStream.close();
				fs.close();
				while (xml.exists()) {
					xml.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Taskname;
		}
		return Taskname;
	}

	public static void main(String[] args) {

	}
}
