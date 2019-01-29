package org.ltd3000.jasperprinter.utils;

/**
 * @author xushanshan
 * @date 181221 
 * @class 数据库打印类
 */
public class DatabaseTask {

	private String id;
	private String querysql;
	private String status;
	private int copies;
	private String jasperfile;
	private String printname;
	private String afterjob;
    public String getAfterjob() {
		return afterjob;
	}
	public void setAfterjob(String afterjob) {
		this.afterjob = afterjob;
	}
	@Override
	public String toString() {
		return "DatabaseTask [id=" + id + ", querysql=" + querysql + ", status=" + status + ", copies=" + copies
				+ ", jasperfile=" + jasperfile + ", printname=" + printname + ", afterjob=" + afterjob + ", printDate="
				+ printDate + "]";
	}
	public String getPrintname() {
		return printname;
	}
	public void setPrintname(String printname) {
		this.printname = printname;
	}
	public String getJasperfile() {
		return jasperfile;
	}
	public void setJasperfile(String jasper) {
		this.jasperfile = jasper;
	}
	public int getCopies() {
		return copies;
	}
	public void setCopies(int copies) {
		this.copies = copies;
	}
	private String  printDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuerysql() {
		return querysql;
	}
	public void setQuerysql(String sql) {
		this.querysql = sql;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

}
