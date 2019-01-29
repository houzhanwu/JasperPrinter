package org.ltd3000.jasperprinter.utils;

import java.io.File;

/**
 * @author xushanshan
 * @date 20181221
 * @class XML打印任务参数Entity
 *
 */
public class XMLTaskParam {

	@Override
	public String toString() {
		return "XMLTaskParam [labelName=" + labelName + ", printerName=" + printerName + ", copies=" + copies + ", xml="
				+ xml + ", taskfile=" + taskfile + ", orderkey=" + orderkey + "]";
	}

	public File getTaskfile() {
		return taskfile;
	}

	public void setTaskfile(File taskfile) {
		this.taskfile = taskfile;
	}

	public String getOrderkey() {
		return orderkey;
	}

	public void setOrderkey(String orderkey) {
		this.orderkey = orderkey;
	}

	public String labelName = "";

	public String printerName = "";

	public int copies = 1;

	public File xml = null;
	public File taskfile = null;

	public String orderkey = "";

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}

	public File getXml() {
		return xml;
	}

	public void setXml(File xml) {
		this.xml = xml;
	}

}
