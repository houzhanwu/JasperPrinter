package org.ltd3000.jasperprinter.printer;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.print.PrintService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.service.PrintXmlService;
import org.ltd3000.jasperprinter.utils.XMLTaskParam;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRExporterContext;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
//import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
//import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.PrintServiceExporterConfiguration;
import net.sf.jasperreports.export.PrintServiceReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

public class XmlPrinter extends JasperPrinter {

	private PrintXmlService labelService = null;// 打印服务

	private static final Logger log = Logger.getLogger("XMLPrinter");

	private Map<String, Integer> statusTotal = new HashMap<String, Integer>();// 打印信息统计

	public Map<String, Integer> getStatusTotal() {
		return statusTotal;
	}

	public String getOsPrinterName() {
		return osPrinterName;
	}

	public void setOsPrinterName(String osPrinterName) {
		this.osPrinterName = osPrinterName;
	}

	public void setStatusTotal(Map<String, Integer> statusTotal) {
		this.statusTotal = statusTotal;
	}

	public XmlPrinter(String printerName, PrintXmlService labelService) {
		this.printerName = printerName;
		this.printerActive = true;
		this.labelService = labelService;
	}

	// 打印机实例化的时候传入打印服务和状态
	public XmlPrinter(String printerName, PrintXmlService labelService, boolean printerActive) {
		this.printerName = printerName;
		this.printerActive = printerActive;
		this.labelService = labelService;
	}

	public boolean isPrinterActive() {
		return printerActive;
	}

	public void setPrinterActive(boolean printerActive) {
		this.printerActive = printerActive;
	}

	public String getStatus() {
		if (statusMap.containsKey(status)) {
			return statusMap.get(status);
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public PrintXmlService getLabelService() {
		return labelService;
	}

	public void setLabelService(PrintXmlService labelService) {
		this.labelService = labelService;
	}

	// 1--启动打印服务--//
	public void startXmlPrintThread() {
		_PrinterXmlThread printerThread = new _PrinterXmlThread(this);
		printerThread.start();
	}

	public class _PrinterXmlThread extends Thread {

		private XmlPrinter xmlPrinter = null;

		private PrintXmlService service = null;

		public _PrinterXmlThread(XmlPrinter xmlPrinter) {
			this.xmlPrinter = xmlPrinter;
			this.service = xmlPrinter.getLabelService();
		}

		@Override
		public void run() {
			while (true) {
				try {
					if (xmlPrinter.isStopThread())
						break;
					if (service.isActive() && xmlPrinter.isPrinterActive()) {
						xmlPrinter.setStatus("0");
						// 获取打印队列
						List<XMLTaskParam> listtask = xmlPrinter.checkAndFindTask();
						if (!listtask.isEmpty()) {
							for (XMLTaskParam eachTask : listtask) {
								try {
									printXmlJ(eachTask);
								} catch (Exception e) {

								}
							}
						}

					} else {
						if (service.isActive()) {
							xmlPrinter.setStatus("-999");
						} else {
							xmlPrinter.setStatus("-888");
						}
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		// 打印报表
		public void printXmlJ(XMLTaskParam xmlTask) {
			xmlPrinter.setStatus("1");
			if (xmlTask.xml.exists()) {
				xmlPrinter.setStatus("2");
				if (xmlTask != null && xmlTask.labelName.trim().length() != 0) {
					File labelFile = new File(xmlTask.labelName);
					if (labelFile.exists()) {
						xmlPrinter.setStatus("3");
						if (xmlPrinter.printXmlLabel(labelFile, xmlTask.xml, xmlTask.copies, xmlTask.taskfile,xmlTask.orderkey)) {
							xmlPrinter.setStatus("9");
						} else {
							log.error("打印失败：" + xmlTask.toString());
							xmlPrinter.setStatus("-3");
						}
					} else {
						xmlPrinter.setStatus("-2");
						log.error("获取模板文件失败：" + xmlTask.toString());
					}
				} else {
					xmlPrinter.setStatus("-2");
					log.error("获取模板文件失败：" + xmlTask.toString());
				}
			} else {
				xmlPrinter.setStatus("-1");
				log.error("获取XML文件失败：" + xmlTask.xml);
			}
			if (xmlPrinter.getStatusTotal().containsKey(xmlPrinter.getStatus())) {
				Integer cnt = xmlPrinter.getStatusTotal().get(xmlPrinter.getStatus());
				xmlPrinter.getStatusTotal().put(xmlPrinter.getStatus(), new Integer(cnt.intValue() + 1));
			} else {
				xmlPrinter.getStatusTotal().put(xmlPrinter.getStatus(), new Integer(1));
			}
		}

	}

	/*
	 * 2 获取任务序列
	 */
	public List<XMLTaskParam> checkAndFindTask() {
		String xmlPath = labelService.getXmlPath();

		File dir = new File(xmlPath);
		List<XMLTaskParam> listTask = new ArrayList<XMLTaskParam>();
		if (!dir.exists()) {
			return listTask;
		}
		if (!dir.isDirectory()) {
			return listTask;
		}
		File[] findFile = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml") && pathname.length() > 0
						&& pathname.canRead()) {
				
					XMLTaskParam printParam = findLabel(pathname, null);// 1
					if (printerName.equalsIgnoreCase(printParam.printerName)) {
						return true;
					}
					return false;
				}
				return false;
			}
		});

		if (findFile != null && findFile.length > 0) {
			Arrays.sort(findFile, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					return f1.getName().compareTo(f2.getName());
				}
			});

			// 移动
			for (int i = 0; i < findFile.length; i++) {
				String taskname = addToPrintSequence(findFile[i]);// 内部删除
				XMLTaskParam t = findLabel(new File(taskname), findFile[i]);
				listTask.add(t);
				while (findFile[i].exists()) {
					findFile[i].delete();
				}
			}
		}
		return listTask;
	}

	/*
	 * 查找打印参数，在过滤任务和获取打印参数是都用到
	 */
	public XMLTaskParam findLabel(File xml, File taskfile) {

		XMLTaskParam printParam = new XMLTaskParam();
		
		if (!xml.canRead()||!xml.getName().contains(printerName.toUpperCase())) {
			return printParam;
		}
		printParam.xml = xml;
		printParam.taskfile = taskfile;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		InputStream in = null;

		try {
			in = new FileInputStream(xml);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(in);
			//ORDERKEY
			NodeList OList = document.getElementsByTagName("ORDERKEY");
			if(OList.getLength()>0) {
				printParam.orderkey=OList.item(0).getFirstChild().getNodeValue();				
			}
			//
			NodeList nodeList = document.getElementsByTagName("labels");
			if (nodeList.getLength() > 0) {
				NamedNodeMap attrs = nodeList.item(0).getAttributes();
				String nodevalue = "";
				String nodename = "";
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attrNode = attrs.item(j);
					nodename = attrNode.getNodeName();
					nodevalue = attrNode.getNodeValue();
					if ("_FORMAT".equalsIgnoreCase(nodename)) {
						if (nodevalue != null && nodevalue.trim().length() != 0) {
							printParam.labelName = this.labelService.getRptPath() + "//" + nodevalue + ".jasper";
						}
					} else if ("_PRINTERNAME".equalsIgnoreCase(nodename)) {
						printParam.printerName = nodevalue;
					} else if ("_QUANTITY".equalsIgnoreCase(attrNode.getNodeName())) {
						try {
							printParam.copies = Integer.parseInt(attrNode.getNodeValue());
						} catch (Exception e) {
							printParam.copies = 1;
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
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
		return printParam;
	}

	/*
	 * 打印1
	 */
	public boolean printXmlLabel(File label, File xml, int copies, File taskfile,String orderkey) {
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = JasperFillManager.fillReport(label.getAbsolutePath(), null,
					new JRXmlDataSource(xml, "/labels/label"));
		} catch (Exception jre) {
			jre.printStackTrace();
			log.info("装载JRXmlDataSource失败:" + xml.getAbsolutePath());
			log.error(jre.getMessage());
		}
		if (jasperPrint == null)
			return false;

		PrintService[] pss = PrinterJob.lookupPrintServices();
		PrintService ps = null;
		for (int i = 0; i < pss.length; i++) {
			if (this.osPrinterName.equalsIgnoreCase(pss[i].getName())) {
				ps = pss[i];
				break;
			}
		}
		if (ps == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd HH:mm:ss SSS");
		jasperPrint.setName(jasperPrint.getName() + "-[" + orderkey + "]" + sdf.format(new Date()));
		JRAbstractExporter<PrintServiceReportConfiguration, PrintServiceExporterConfiguration, ExporterOutput, JRExporterContext> je = new JRPrintServiceExporter();
		//Replaced by setExporterInput(ExporterInput),
		//setConfiguration(ExporterConfiguration), 
		//setConfiguration(ReportExportConfiguration) 
		//and setExporterOutput(ExporterOutput)

		//je.setParameter(JRPrintServiceExporterParameter.JASPER_PRINT, jasperPrint);
		je.setExporterInput(new SimpleExporterInput(jasperPrint));
		
		//PrintServiceExporterConfiguration
		SimplePrintServiceExporterConfiguration conf=new SimplePrintServiceExporterConfiguration();
		conf.setDisplayPageDialog(false);
		conf.setDisplayPrintDialog(false);
		conf.setPrintService(ps);
		//je.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, false);
		//je.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, false);
		//je.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, ps);
		je.setConfiguration(conf);

		try {
			for (int i = 0; i < copies; i++) {
				je.exportReport();				
			}
			log.info("打印完成：" + "[" + orderkey + "-" + copies + "]" + xml.getName());
			DBUtil.addJasperCount();
			moveToLog(xml);
			if (xml.exists()) {
				xml.delete();
			}
			if (null != taskfile) {
				if (taskfile.exists()) {
					taskfile.delete();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * @导出excel
	 * @param jasperPrint
	 * @param filename
	 * @return
	 */
	public boolean exportXls(JasperPrint jasperPrint,String filename) {
		
		JRXlsxExporter exporter = new JRXlsxExporter();
		//设置输入项
		ExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
		exporter.setExporterInput(exporterInput);
		
		//设置输出项
		OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(filename);
		exporter.setExporterOutput(exporterOutput);
		
		try {
			exporter.exportReport();
			return true;
		} catch (JRException e1) {			
			e1.printStackTrace();
			return false;
		}
		
	}

	// 移动文件到日志区域
	private boolean moveToLog(File xml) {

		String stationPath = this.labelService.getStationPath();
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			if (xml.exists()) {
				int byteread = 0;
				inStream = new FileInputStream(xml);
				File desFile = new File(stationPath + "/bak/" + xml.getName());
				if (!desFile.getParentFile().exists()) {
					desFile.getParentFile().mkdirs();
				}
				if (desFile.exists()) {
					desFile.delete();
				}
				fs = new FileOutputStream(stationPath + "/bak/" + xml.getName());
				//////////////////
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
				//////////////////
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}


	/*
	 * 将任务加入打印序列，即移动到打印区域
	 */
	public String addToPrintSequence(File xml) {
		String stationPath = labelService.getStationPath();
		InputStream inStream = null;
		FileOutputStream fs = null;
		String Taskname = stationPath +"/"+printerName +"/"+  xml.getName();
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
				//////////////////
				SAXReader reader = new SAXReader();
				org.dom4j.Document doc = reader.read(inStream);
				doc.setDocType(null);
				//如果要启用客户端格式转换，请放开下面部分
				/*List<org.dom4j.Element> labelList=doc.selectNodes("labels/label");
				for(org.dom4j.Element label:labelList ) {//循环
					List<org.dom4j.Element> nodeList=label.selectNodes("variable");
					if(nodeList.isEmpty()) {
						break;
					}
					for(org.dom4j.Element node:nodeList) {
						String tag=node.attributeValue("name").toUpperCase();
						String value=node.getText();
						org.dom4j.Element e=org.dom4j.DocumentHelper.createElement(tag);
						e.setText(value);
						label.add(e);						
						label.remove(node);
					}
				}
				*/

				XMLWriter writer = new XMLWriter(fs);
				writer.write(doc);
				writer.flush();
				writer.close();
				inStream.close();
				fs.close();
				while(xml.exists()) {
					xml.delete();
					}
				//////////////////
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Taskname;
		}
		return Taskname;
	}
	
}
