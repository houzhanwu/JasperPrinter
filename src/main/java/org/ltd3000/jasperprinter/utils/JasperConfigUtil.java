package org.ltd3000.jasperprinter.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xushanshan
 * @date 20181221
 * @class JasperTask 处理工具类
 */
public class JasperConfigUtil {

	public static List<JasperConfigEntity> getJasperConfigList(ResultSet rs) throws Exception {
		
		List<JasperConfigEntity> list = new ArrayList<JasperConfigEntity>();
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int colCount = rsmd.getColumnCount(); // 得到列的总数
		for (;rs.next();) {
			JasperConfigEntity config = new JasperConfigEntity();
			// 格式为row id , col name, col context
			for (int i = 1; i <= colCount; i++) {
				rsmd.getColumnTypeName(i);
				String name = rsmd.getColumnName(i);				
				if ("ID".equalsIgnoreCase(name)) {
					config.setID(rs.getString(i));
				} else if ("PRINTTYPE".equalsIgnoreCase(name)) {
					config.setPRINTTYPE(rs.getString(i));
				} else if ("JASPERFILE".equalsIgnoreCase(name)) {
					config.setJASPERFILE(rs.getString(i));
				} else if ("IDENTITYORDER".equalsIgnoreCase(name)) {
					config.setIDENTITYORDER(rs.getInt(i));
				}else if ("WHERECASE".equalsIgnoreCase(name)) {
					config.setWHERECASE(rs.getString(i));
				}else if ("AFTERJOB".equalsIgnoreCase(name)) {
					config.setAFTERJOB(rs.getString(i));
				}else if ("SQLQUERY".equalsIgnoreCase(name)) {
					config.setSQLQUERY(rs.getString(i));
				}
			}
			list.add(config);
		}

		return list;

	}

}
