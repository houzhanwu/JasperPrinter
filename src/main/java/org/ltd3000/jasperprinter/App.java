package org.ltd3000.jasperprinter;

import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.ui.UIMain;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.SecureUtil;

/**
 * @author xushanshan
 * @authrized to 2020-02-11
 * @license 许可证书 jasper.public 
 * authkey={"p"\:"hN2LwpKardmSm3Xpj9PJFZDmHpZWotNnXaFWUsbBdyFO4H9nKqRN+ev/GHKxi6gQN3CVxPRP7AUUaaJn9hr4tV0QOd1MYMxxtHAgKbfSWSHuDSX1XRAztaGwzxXBMNMy61gDDBHMaO5MN8o3g987sOM82AzvaghPFdi+uT2XQME\=","d"\:"4BA78F08F874CF071231B82D7C66B319B3C69265AADB6F1E3F7E3EE5D47FA7B9B958F5A99FDEAE0BED80D6FE9991FB4DBD022E3AFC285EFFEF296DFF13414BDBDE13B0536113F87E1EA075E097BCD1BD9B8CCAB6C726B110461C24346435638F0FB909EE23F3B913"}
 * 
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
		// DBUtil.checkPermission()
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
