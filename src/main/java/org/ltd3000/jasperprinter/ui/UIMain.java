package org.ltd3000.jasperprinter.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.ltd3000.jasperprinter.db.DBUtil;
import org.ltd3000.jasperprinter.service.PrintDatabaseService;
import org.ltd3000.jasperprinter.service.PrintXmlService;
import org.ltd3000.jasperprinter.utils.ConfigUtil;
import org.ltd3000.jasperprinter.utils.FtpUtils;
import org.ltd3000.jasperprinter.utils.SecureUtil;

import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class UIMain {

	private JFrame frmV;// 界面
	private JTable tblMain;// 程序窗体
	private static final Logger logger = Logger.getLogger("UIMain");

	public UIMain() {
		initialize();
	}

	public void setFrmVisable() {
		this.frmV.setVisible(true);
	}

	/**
	 * @初始化系统参数与界面与服务
	 */
	private void initialize() {
		frmV = new JFrame();
		frmV.setTitle("Jasper单证打印服务  V" + ConfigUtil.getProperty("version") + "[" + DBUtil.getJasperCount() + "]");
		frmV.setBounds(100, 100, 450, 300);
		frmV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmV.setExtendedState(JFrame.MAXIMIZED_BOTH);

		JMenuBar mBarMain = new JMenuBar();
		frmV.setJMenuBar(mBarMain);

		JMenu mmenuSys = new JMenu("系统");
		mBarMain.add(mmenuSys);

		JMenuItem mitemPrinter = new JMenuItem("打印机管理");
		mitemPrinter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrinterManager printer = new PrinterManager();
				printer.setAlwaysOnTop(true);
				printer.setVisible(true);
			}
		});
		mmenuSys.add(mitemPrinter);
		// 同步
		if ("slave".equalsIgnoreCase(ConfigUtil.getProperty("mode"))) {
			JMenu mmenuSyc = new JMenu("同步与更新");
			mBarMain.add(mmenuSyc);
			//
			JMenuItem mitemSyc = new JMenuItem("同步打印模版");
			mitemSyc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Thread() {
						@Override
						public void run() {
							if (FtpUtils.downloadAllJasperFile()) {
								JOptionPane.showMessageDialog(null, "同步完成!");
							} else {
								JOptionPane.showMessageDialog(null, "同步失败，请查看日志");
							}
						}
					}.start();

				}
			});
			mmenuSyc.add(mitemSyc);
			// jar
			JMenuItem mitemexe = new JMenuItem("更新打印程序");
			mitemexe.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Thread() {
						@Override
						public void run() {
							FtpUtils.downloadAllUpdateFile();
							try {
								Runtime.getRuntime().exec("java -jar update.jar");
								logger.info("更新程序启动！");
								System.exit(0);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.start();

				}
			});
			mmenuSyc.add(mitemexe);

		}

		// 同步
		JMenuItem mitemSys = new JMenuItem("参数管理");
		mitemSys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SysManager sys = new SysManager();
				sys.setAlwaysOnTop(true);
				sys.setVisible(true);
			}
		});
		mmenuSys.add(mitemSys);

		JMenuItem mitemQuit = new JMenuItem("退出");
		mitemQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mmenuSys.add(mitemQuit);

		JMenu menuService = new JMenu("打印服务");
		mBarMain.add(menuService);

		JMenuItem mitemStart = new JMenuItem("开启");
		mitemStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("database")) {
					// 启动数据库类型打印服务
					PrintDatabaseService service = PrintDatabaseService.getInstance();
					service.setServiceStatus(true);

				} else if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("xml")) {
					// 启动XML类型打印
					PrintXmlService service = PrintXmlService.getInstance();
					service.setServiceStatus(true);
				}

				logger.info("开启打印服务!");
			}
		});
		menuService.add(mitemStart);

		JMenuItem mitemStop = new JMenuItem("停止");
		mitemStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("database")) {
					PrintDatabaseService service = PrintDatabaseService.getInstance();
					service.setServiceStatus(false);
				} else if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("xml")) {
					PrintXmlService service = PrintXmlService.getInstance();
					service.setServiceStatus(false);
				}
				logger.info("停止打印服务!");
			}
		});
		menuService.add(mitemStop);


		frmV.getContentPane().setLayout(new BoxLayout(frmV.getContentPane(), BoxLayout.X_AXIS));

		JScrollPane sclpnlMain = new JScrollPane();
		frmV.getContentPane().add(sclpnlMain);
		tblMain = new JTable();
		tblMain.setModel(new LabelTableModel(
				new String[] { "\u5E8F\u53F7", "\u6253\u5370\u673A", "\u72B6\u6001", "\u5907\u6CE8" }) {
			private static final long serialVersionUID = 5142540972495473663L;
			boolean[] columnEditables = new boolean[] { false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tblMain.getColumnModel().getColumn(0).setResizable(false);
		sclpnlMain.setViewportView(tblMain);
		// 主界面增加打印机控制
		tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {

					JPopupMenu popMenu = null;
					JTable table = (JTable) e.getComponent();
					// 获取鼠标右键选中的行
					int row = table.rowAtPoint(e.getPoint());
					if (row == -1) {
						return;
					}
					// 获取已选中的行
					int[] rows = table.getSelectedRows();
					boolean inSelected = false;
					// 判断当前右键所在行是否已选中
					for (int r : rows) {
						if (row == r) {
							inSelected = true;
							break;
						}
					}
					// 当前鼠标右键点击所在行不被选中则高亮显示选中行
					if (!inSelected) {
						table.setRowSelectionInterval(row, row);
					}
					// 生成右键菜单
					popMenu = new JPopupMenu();

					JMenuItem stopPrinter = new JMenuItem("停止打印机");
					JMenuItem startPrinter = new JMenuItem("开启打印机");
					stopPrinter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int selectrow = table.getSelectedRow();
							TableModel dtm = (TableModel) table.getModel();
							String printerName = (String) dtm.getValueAt(selectrow, 1);// 打印机名称
							PrintXmlService service;
							try {
								service = PrintXmlService.getInstance();
								service.setActive(printerName, false);

							} catch (Exception e1) {

								e1.printStackTrace();
							}

						}
					});
					startPrinter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int selectrow = table.getSelectedRow();
							TableModel dtm = (TableModel) table.getModel();
							String printerName = (String) dtm.getValueAt(selectrow, 1);
							PrintXmlService service;
							try {
								service = PrintXmlService.getInstance();
								service.setActive(printerName, true);
							} catch (Exception e1) {

								e1.printStackTrace();
							}

						}
					});
					popMenu.add(startPrinter);
					popMenu.add(stopPrinter);
					popMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		RefreshUIThread refresh = new RefreshUIThread(tblMain);
		refresh.start();
	}

	// 初始化主界面的时候启动打印服务
	public class LabelTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -124322375382861381L;

		Object data[][] = null;

		String title[] = null;

		public LabelTableModel(String title[]) {
			this.title = title;
			if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("database")) {
				PrintDatabaseService Databaseservice = PrintDatabaseService.getInstance();
				data = Databaseservice.getPrinterStatusModel();
			} else if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("xml")) {
				PrintXmlService Xmlservice = PrintXmlService.getInstance();
				data = Xmlservice.getPrinterStatusModel();
			} else {
				JOptionPane.showMessageDialog(null, "请配置打印类型printtype=xml|database");
				System.exit(0);
			}
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return title.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("database")) {
				PrintDatabaseService Databaseservice = PrintDatabaseService.getInstance();
				data = Databaseservice.getPrinterStatusModel();
			} else if (ConfigUtil.getProperty("printtype").equalsIgnoreCase("xml")) {
				PrintXmlService Xmlservice = PrintXmlService.getInstance();
				data = Xmlservice.getPrinterStatusModel();
			} else {
				JOptionPane.showMessageDialog(null, "请配置打印类型printtype=xml|database");
				System.exit(0);
			}
			if (data == null)
				return null;
			if (rowIndex >= data.length)
				return null;
			return data[rowIndex][columnIndex];

		}

		@Override
		public String getColumnName(int column) {
			return title[column];
		}

	}

	/**
	 * @author xushanshan
	 * @刷新UI界面线程
	 *
	 */
	public class RefreshUIThread extends Thread {
		JTable table = null;

		public RefreshUIThread(JTable table) {
			super();
			this.table = table;
		}

		@Override
		public void run() {
			while (true) {
				table.validate();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						table.updateUI();
						frmV.setTitle("Jasper单证打印服务  V" + ConfigUtil.getProperty("version") + "["
								+ DBUtil.getJasperCount() + "]");
					}
				});
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
