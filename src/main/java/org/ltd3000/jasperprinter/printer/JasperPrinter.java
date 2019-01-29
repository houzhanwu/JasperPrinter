package org.ltd3000.jasperprinter.printer;

import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import org.apache.log4j.Logger;

public class JasperPrinter {

	// 打印机注册名称
	public String printerName = "";
	// 系统打印机名称
	public String osPrinterName;
	// 打印机活跃状态[休眠|活跃]
	public boolean printerActive = false;
	// 打印服务
	public PrintService labelService = null;

	public boolean stopThread = false;
	//打印机打印状态
	public String status = "0";
    //打印信息
	public static Map<String, String> statusMap = new HashMap<String, String>();
	//日志
	public static final Logger log = Logger.getLogger("JasperPrinter");
    //状态MAP
	static {
		statusMap.put("-999", "打印机休眠");
		statusMap.put("-888", "打印服务停止");
		statusMap.put("-777", "失败");
		statusMap.put("0", "等待");
		statusMap.put("1", "获取XML文件");
		statusMap.put("-1", "获取XML文件失败");
		statusMap.put("2", "获取报表文件");
		statusMap.put("-2", "获取报表文件失败");
		statusMap.put("3", "打印报表");
		statusMap.put("-3", "打印报表失败");
		statusMap.put("9", "打印完成");
		statusMap.put("-9", "备份文件失败");
	}
    //打印状态数量统计
	private Map<String, Integer> statusTotal = new HashMap<String, Integer>();

	public boolean isStopThread() {
		return stopThread;
	}

	public void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}

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


	// 测试
	public static void main(String args[]) {
	}


}
