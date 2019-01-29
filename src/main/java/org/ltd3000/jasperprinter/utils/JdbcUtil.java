package org.ltd3000.jasperprinter.utils;

import org.apache.log4j.Logger;

/**
 * @author xushanshan
 * @date 20181221
 * @class jdbc 连接处理工具
 *
 */
public class JdbcUtil {

	private static final Logger logger = Logger.getLogger("JDBCUtil");

	public static JdbcEntity parseJdbcUrl(String jdbcUrl) {
		JdbcEntity jdbc = new JdbcEntity();
		String[] arg = jdbcUrl.split("::");
		if (arg.length == 3) {
			String[] uArg = arg[0].split(":");
			if (uArg.length == 4 || uArg.length == 5) {
				jdbc.setDbtype(uArg[1]);
				jdbc.setUrl(arg[0]);
				jdbc.setUserNmae(arg[1]);
				jdbc.setPassWord(arg[2]);
				jdbc.setDriverClass(getDriverClass(uArg[1]));
				return jdbc;
			} else {
				return jdbc;
			}
		} else {
			logger.error("连接字符串配置错误，请参考'jdbc:oracle:thin:@127.0.0.1:1521:ORCL::username::password'");
			return jdbc;
		}

	}

	private static String getDriverClass(String string) {
		if ("mysql".equalsIgnoreCase(string)) {
			return "com.mysql.jdbc.Driver";
		} else if ("oracle".equalsIgnoreCase(string)) {
			return "oracle.jdbc.driver.OracleDriver";
		} else if ("sqlserver".equalsIgnoreCase(string)) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}
		return "请配置合适的数据库类型!";
	}

	public static void main(String[] args) {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL::scm::scm2016";
		JdbcEntity jdbc = parseJdbcUrl(url);
		System.out.println(jdbc.toString());

	}

}
