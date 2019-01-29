package org.ltd3000.jasperprinter.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xushanshan
 * @date 181221 
 * @class 数据库任务类工具
 */
public class DatabaseTaskUtil {

	/**
	 * @param rs
	 * @function 从连接中获取打印任务
	 * @throws Exception
	 */
	public static List<DatabaseTask> getJasperTaskList(ResultSet rs) throws Exception {

		List<DatabaseTask> list = new ArrayList<DatabaseTask>();
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int colCount = rsmd.getColumnCount(); // 得到列的总数
		for (; rs.next();) {
			DatabaseTask task = new DatabaseTask();
			// 格式为row id , col name, col context
			for (int i = 1; i <= colCount; i++) {
				rsmd.getColumnTypeName(i);
				String name = rsmd.getColumnName(i);
				if ("id".equalsIgnoreCase(name)) {
					task.setId(rs.getString(i));
				} else if ("querysql".equalsIgnoreCase(name)) {
					task.setQuerysql(rs.getString(i));
				} else if ("status".equalsIgnoreCase(name)) {
					task.setStatus(rs.getString(i));
				} else if ("printdate".equalsIgnoreCase(name)) {
					task.setPrintDate(rs.getString(i));
				} else if ("copies".equalsIgnoreCase(name)) {
					task.setCopies(rs.getInt(i));
				} else if ("jasperfile".equalsIgnoreCase(name)) {
					task.setJasperfile(rs.getString(i));
				} else if ("printname".equalsIgnoreCase(name)) {
					task.setPrintname(rs.getString(i));
				} else if ("afterjob".equalsIgnoreCase(name)) {
					task.setAfterjob(rs.getString(i));
				}
			}
			list.add(task);
		}

		return list;

	}

}
