package org.ltd3000.jasperprinter.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
  * @author xushanshan
 * @date 181221 
 * @下面是一个侵入式改造已有系统的范例,可参照实现你自己的解决方案,目标就是让你的系统产生相关的xml数据文件或者数据库打印任务序列
 *
 */
public class BussinessExample {
	// 这里的orderkey来源于你的业务系统,其次打印的时候要选择打印机以及打印的份数
	public void Example(String orderkey, String printname, int copies) {
		JdbcEntity jdbc = JdbcUtil.parseJdbcUrl(ConfigUtil.getProperty("urlpath"));
		try {
			if (!jdbc.getDriverClass().isEmpty()) {
				Class.forName(jdbc.getDriverClass());
				Connection conn = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUserNmae(), jdbc.getPassWorld());
				// 获取打印类型,这里[order]根据你的业务系统打印类型来决定
				String sql = "select * from JasperConfig t where t.printtype='[order]' order by IDENTITYORDER ASC";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				List<JasperConfigEntity> list = JasperConfigUtil.getJasperConfigList(rs);
				stmt.close();
				rs.close();
				// 寻找最佳配置,这里使用识别优先级配合查询条件，完美根据实际业务情况分配打印的模版
				for (JasperConfigEntity config : list) {
					String wherecase = config.getWHERECASE();
					String query = "select * from order where orderkey='" + orderkey + "' and " + wherecase;
					Statement stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery(query);
					if (rs1.next()) {// 匹配成功
						DatabaseTask task = new DatabaseTask();
						task.setAfterjob(config.getAFTERJOB());
						task.setCopies(copies);
						task.setJasperfile(config.getJASPERFILE());
						task.setPrintname(printname);
						task.setQuerysql(config.getSQLQUERY().replace(":=orderkey", "=" + orderkey));
						// 生成xml或者加入打印任务表
					}
					stmt1.close();
					rs1.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
