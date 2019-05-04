package org.ltd3000.jasperprinter;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.ui.UIMain;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.SecureUtil;

/**
 * @author xushanshan
 * @authrized to 2020-03-10
 * @license 许可证书 jasper.public
 *H/+bJM7wEYPpfxabfh4z/rGXBIsUF0hDujKxJN6jWzm66AROtirrgnpOqZ98vqzdxVeKJOBxXWztfqrT6hJK0Lr5kyda+8tKpITFWBDtlgdb6QWRPPi4iIKymyfmVLxmWy6FyEihgP3VBseB/bK2tPzcVMLzjRnRUpekRU4idK0=          
 *
 */
public class App {
	private static final Logger logger = Logger.getLogger("APP");

	public static void main(String[] args) {
		// 初始化数据库和配置项
		if ("true".equals(ConfigUtil.getProperty("init"))) {
			ConfigUtil.setDefaultPath();
			DBUtil.initDB();
			ConfigUtil.removeProperty("init");
		}

		// 软件许可检测

		if (SecureUtil.checkAuth()) {
			logger.info("软件许可验证通过！");

			// 单例锁定
			if (SecureUtil.lockInstance()) {
				UIMain window = new UIMain();
				window.setFrmVisable();
				logger.info("jasper打印服务启动!");
			} else {
				System.exit(0);
			}

		} else {
			JOptionPane.showMessageDialog(null, "软件许可无效!请联系许闪闪\n     1539601747@qq.com\n            18260215953");
			System.exit(0);
		}

	}
}
