package org.ltd3000.jasperprinter.utils;

/**
 * @author xushanshan
 * @date 181221
 * @class 客户打印工具类
 *
 */
public class DeliverUtil {
	/**
	 * @function 从打印任务xml文件名获取打印机名称
	 */
	public static String getPrinterName(String taskFileName) {

		return taskFileName.substring(taskFileName.lastIndexOf("@") + 1, taskFileName.indexOf("."));

	}

	/**
	 * @param printerName
	 * @function 判断打印机是否是客户端或者服务器注册的客户端打印机
	 */
	public static boolean isClientPrinter(String printerName) {
		if ("".equals(ConfigUtil.getProperty("printlist"))) {
			return false;
		}
		String[] printerlist = ConfigUtil.getProperty("printlist").split(",");
		return findString(printerlist, printerName);

	}

	/**
	 * @function 判断字符串是否在数据中
	 */
	private static boolean findString(String[] list, String s) {
		if (list.length == 0 || s == null) {
			return false;
		} else {
			for (int i = 0; i <= list.length - 1; i++) {
				if (s.equalsIgnoreCase(list[i])) {
					return true;
				}
			}
			return false;
		}
	}

	public static void main(String[] args) {
		String s = "11262018_065332_60360@FEILI_wmwhse1@XIN1-1F-005.xml";
		s = getPrinterName(s);
		System.out.println(s);
	}

}
