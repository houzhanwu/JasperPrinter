package org.ltd3000.jasperprinter.printer;

import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;

import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.service.PrintDatabaseService;
import org.ltd3000.jasperprinter.utils.DatabaseTask;
import org.ltd3000.jasperprinter.utils.DatabaseTaskUtil;
import org.ltd3000.jasperprinter.utils.JdbcEntity;
import org.ltd3000.jasperprinter.utils.JdbcUtil;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.export.JRExporterContext;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.PrintServiceExporterConfiguration;
import net.sf.jasperreports.export.PrintServiceReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

/**
 * @author xushanshan
 * @descrip 数据库类型打印机
 *
 */
public class DatabasePrinter extends JasperPrinter {

	private PrintDatabaseService labelService = null;// 打印服务

	private static final Logger log = Logger.getLogger("DataBasePrinter");//log


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

	public DatabasePrinter(String printerName, PrintDatabaseService labelService) {
		this.printerName = printerName;
		this.printerActive = true;
		this.labelService = labelService;
	}

	// 打印机实例化的时候传入打印服务和状态
	public DatabasePrinter(String printerName, PrintDatabaseService labelService, boolean printerActive) {
		this.printerName = printerName;
		this.printerActive = printerActive;
		this.labelService = labelService;
	}

	public boolean isPrinterActive() {
		return printerActive;
	}

	/* 
	 * @开启打印机工作线程
	 */
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

	public PrintDatabaseService getLabelService() {
		return labelService;
	}

	public void setLabelService(PrintDatabaseService labelService) {
		this.labelService = labelService;
	}

	public boolean printDatabaseLabel(File label, ResultSet dataset,DatabaseTask task, Connection connection) {
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = JasperFillManager.fillReport(label.getAbsolutePath(), null,
					new JRResultSetDataSource(new SimpleJasperReportsContext(), dataset));
			log.info("装载dataSet完成");
		} catch (Exception jre) {
			jre.printStackTrace();
			log.info("装载dataSet失败");
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
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd:HH:mm:ss");
		jasperPrint.setName(jasperPrint.getName() + "-" + sdf.format(new Date()));
		JRAbstractExporter<PrintServiceReportConfiguration, PrintServiceExporterConfiguration, ExporterOutput, JRExporterContext> je = new JRPrintServiceExporter();
		// Replaced by setExporterInput(ExporterInput),
		// setConfiguration(ExporterConfiguration),
		// setConfiguration(ReportExportConfiguration)
		// and setExporterOutput(ExporterOutput)

		// je.setParameter(JRPrintServiceExporterParameter.JASPER_PRINT, jasperPrint);
		je.setExporterInput(new SimpleExporterInput(jasperPrint));

		// PrintServiceExporterConfiguration
		SimplePrintServiceExporterConfiguration conf = new SimplePrintServiceExporterConfiguration();
		conf.setDisplayPageDialog(false);
		conf.setDisplayPrintDialog(false);
		conf.setPrintService(ps);
		// je.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, false);
		// je.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, false);
		// je.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, ps);
		je.setConfiguration(conf);
		try {
			for (int i = 0; i < task.getCopies(); i++) {
				je.exportReport();
				markPrintedTask(connection, task);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

	}

	/**
	 * @param connection
	 * @param task
	 * @标记task状态为打印完成
	 * @return
	 */
	private boolean markPrintedTask(Connection connection, DatabaseTask task) {
		try {
			Statement stmtTask = connection.createStatement();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			stmtTask.execute(task.getAfterjob()+";update jaspertask t set t.status='2' , t.printdate='" + sdf.format(new Date())
					+ "' where t.id ='" + task.getId() + "'");
			stmtTask.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @开启数据库类型打印服务
	 */
	public void startDatabasePrintThread() {
		JdbcEntity jdbc = JdbcUtil.parseJdbcUrl(labelService.getUrlPath());
		log.info(jdbc.toString());
		try {
			if (!jdbc.getDriverClass().isEmpty()) {
				Class.forName(jdbc.getDriverClass());
				Connection conn = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUserNmae(), jdbc.getPassWorld());
				_PrinterDatabaseThread printerThread = new _PrinterDatabaseThread(this, conn);
				printerThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 这里文件加载到打印队列即删除
	public class _PrinterDatabaseThread extends Thread {

		private DatabasePrinter databasePrinter = null;

		private PrintDatabaseService service = null;
		private Connection connection = null;

		public _PrinterDatabaseThread(DatabasePrinter databasePrinter, Connection connection) {
			this.databasePrinter = databasePrinter;
			this.connection = connection;
			this.service = databasePrinter.getLabelService();
		}

		@Override
		public void run() {
			try {

				while (true) {
					try {
						if (databasePrinter.isStopThread())
							break;
						if (service.getServiceStatus() && databasePrinter.isPrinterActive()) {
							databasePrinter.setStatus("0");
							String sql = "select * from JasperTask t where t.status='0' and t.printname='"
									+ databasePrinter.getPrinterName() + "' ";
							Statement stmt = connection.createStatement();
							ResultSet rs = stmt.executeQuery(sql);
							List<DatabaseTask> list = DatabaseTaskUtil.getJasperTaskList(rs);

							for (DatabaseTask jasperTask : list) {
								if (markQueuedTask(connection, jasperTask)) {// 标记为打印队列中
									Statement stmtTask = connection.createStatement();
									ResultSet resultSet = stmtTask.executeQuery(jasperTask.getQuerysql());
									/*
									 * DatabasePrintTask task = new DatabasePrintTask(resultSet, jasperTask,
									 * stmtTask); executor.execute(task);
									 */
									printDatabaseJ(resultSet, jasperTask, stmtTask);
								}

							}
							stmt.close();
							rs.close();

						} else {
							if (service.getServiceStatus()) {
								databasePrinter.setStatus("-999");
							} else {
								databasePrinter.setStatus("-888");
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
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		private boolean markQueuedTask(Connection connection, DatabaseTask task) {
			Statement stmtTask;
			try {
				stmtTask = connection.createStatement();
				stmtTask.execute("update jaspertask t set t.status='1' where t.id ='" + task.getId() + "'");
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

		}


		// 打印报表
		public void printDatabaseJ(ResultSet resultSet, DatabaseTask task, Statement stmtTask) {
			databasePrinter.setStatus("1");
			File jasperFile = new File(service.getRptPath() + "/" + task.getJasperfile());

			if (jasperFile.exists()) {
				databasePrinter.setStatus("3");
				if (databasePrinter.printDatabaseLabel(jasperFile,resultSet,task,connection)) {
					try {
						resultSet.close();
						stmtTask.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					databasePrinter.setStatus("9");
				} else {
					log.error("打印失败");
					databasePrinter.setStatus("-3");
				}

			} else {
				databasePrinter.setStatus("-2");
				log.error("获取Jasper文件失败：" + service.getRptPath() + "/" + task.getJasperfile());
			}
			if (databasePrinter.getStatusTotal().containsKey(databasePrinter.getStatus())) {
				Integer cnt = databasePrinter.getStatusTotal().get(databasePrinter.getStatus());
				databasePrinter.getStatusTotal().put(databasePrinter.getStatus(), new Integer(cnt.intValue() + 1));
			} else {
				databasePrinter.getStatusTotal().put(databasePrinter.getStatus(), new Integer(1));
			}
		}

	}
}
