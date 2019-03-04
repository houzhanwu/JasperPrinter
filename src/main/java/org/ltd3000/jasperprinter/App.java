package org.ltd3000.jasperprinter;

import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.ui.UIMain;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.SecureUtil;

/**
 * @author xushanshan
 * @authrized to 2020-03-10
 * @license 许可证书 jasper.public
 * {"p":"kbXNAfFXEsM4S0YHQaOhtRyac7nGoXZDt7jHa1+5z1a7JJF38yq+lk5xlT67UUlWpQ8hHNHemtL4agRe0q3TCuJ2e1MtHTUc9wvMFzoG0dcnSZfkp1XGO46ipy2xTeE86xXuH668oCrMibx2Bb/R+acaiVUG5LuldRKZFuHw/R4=","d":"6BDE0F6A20A07C2F0109FA9F6AC2BE57AB5C33B85E7297B60102E331EC8475B054B130ED6B2418F11E1EC542E77A97D28E1094F1E71D5799"}
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

/*		if (SecureUtil.checkAuth()) {
			logger.info("软件许可验证通过！");*/

			// 单例锁定
			if (SecureUtil.lockInstance()) {
				UIMain window = new UIMain();
				window.setFrmVisable();
				logger.info("jasper打印服务启动!");
			} else {
				System.exit(0);
			}


		/*
		 * } else { JOptionPane.showMessageDialog(null,
		 * "软件许可无效!请联系许闪闪\n     1539601747@qq.com\n            18260215953");
		 * System.exit(0); }
		 */

	}
}
