package org.ltd3000.jasperprinter.utils;

/**
 * @author xushanshan
 * @date 20181221
 * @class JDBC参数Entity
 *
 */
public class JdbcEntity {
	@Override
	public String toString() {
		return "JdbcEntity [dbtype=" + dbtype + ", url=" + url + ", passWord=" + passWord + ", userNmae=" + userNmae
				+ ", driverClass=" + driverClass + "]";
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPassWorld() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getUserNmae() {
		return userNmae;
	}
	public void setUserNmae(String userNmae) {
		this.userNmae = userNmae;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String dbtype="";
	public String url="";
	public String passWord="";
	public String userNmae="";
	public String driverClass="";

}
