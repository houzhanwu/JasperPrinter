package org.ltd3000.jasperprinter.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.ltd3000.jasperprinter.service.PrintXmlService;


public class PrinterManager extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -802167387174894311L;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PrinterManager dialog = new PrinterManager();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PrinterManager() {
		setTitle("打印机管理");
		setBounds(100, 100, 450, 300);
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.addMouseListener(new MouseAdapter() {
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
							JMenuItem mitemDelete = new JMenuItem("删除");
							JMenuItem mitemStop = new JMenuItem("停止");
							JMenuItem mitemStart = new JMenuItem("开启");
							mitemDelete.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									// LabelCurrPrinter currPrinter = new LabelCurrPrinter();
									// currPrinter.setAlwaysOnTop(true);
									// currPrinter.setVisible(true);
									if (table.getRowCount() == 1) {

										return;
									}
									int selectrow = table.getSelectedRow();
									// System.out.println(selectrow);
									DefaultTableModel dtm = (DefaultTableModel) table.getModel();
									String printerName = (String) dtm.getValueAt(selectrow, 1);

									PrintXmlService service;
									try {
										service = PrintXmlService.getInstance();
										service.removePrinterAction(printerName);
										dtm.setDataVector(service.getPrinterModel(),
												new String[] { "\u5E8F\u53F7", "\u6CE8\u518C\u6253\u5370\u673A",
														"\u7CFB\u7EDF\u6253\u5370\u673A", "\u662F\u5426\u4F7F\u7528" });
									} catch (Exception e1) {
										e1.printStackTrace();
									}

									// table.setModel(new DefaultTableModel(service.getPrinterModel()));
									// dtm.removeRow(selectrow);
									// table.repaint();
								}
							});
							mitemStop.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									int selectrow = table.getSelectedRow();
									DefaultTableModel dtm = (DefaultTableModel) table.getModel();
									String printerName = (String) dtm.getValueAt(selectrow, 1);
									PrintXmlService service;
									try {
										service = PrintXmlService.getInstance();
										service.setActive(printerName, false);
										dtm.setDataVector(service.getPrinterModel(),
												new String[] { "\u5E8F\u53F7", "\u6CE8\u518C\u6253\u5370\u673A",
														"\u7CFB\u7EDF\u6253\u5370\u673A", "\u662F\u5426\u4F7F\u7528" });
									} catch (Exception e1) {
										
										e1.printStackTrace();
									}

								}
							});
							mitemStart.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									int selectrow = table.getSelectedRow();
									DefaultTableModel dtm = (DefaultTableModel) table.getModel();
									String printerName = (String) dtm.getValueAt(selectrow, 1);
									PrintXmlService service;
									try {
										service = PrintXmlService.getInstance();
										service.setActive(printerName, true);
										dtm.setDataVector(service.getPrinterModel(),
												new String[] { "\u5E8F\u53F7", "\u6CE8\u518C\u6253\u5370\u673A",
														"\u7CFB\u7EDF\u6253\u5370\u673A", "\u662F\u5426\u4F7F\u7528" });
									} catch (Exception e1) {
										
										e1.printStackTrace();
									}

								}
							});
							popMenu.add(mitemDelete);
							popMenu.add(mitemStart);
							popMenu.add(mitemStop);
							popMenu.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				});

				PrintXmlService service = PrintXmlService.getInstance();
				Object[][] model = service.getPrinterModel();

				table.setModel(
						new DefaultTableModel(model, new String[] { "\u5E8F\u53F7", "\u6CE8\u518C\u6253\u5370\u673A",
								"\u7CFB\u7EDF\u6253\u5370\u673A", "\u662F\u5426\u4F7F\u7528" }) {
							/**
							 * 
							 */
							private static final long serialVersionUID = -723186627825071279L;
							boolean[] columnEditables = new boolean[] { false, false, false, true };

							public boolean isCellEditable(int row, int column) {
								return columnEditables[column];
							}
						});
				table.getColumnModel().getColumn(0).setResizable(false);
				/*
				 * table.getColumnModel().getColumn(0).setPreferredWidth(50);
				 * table.getColumnModel().getColumn(1).setPreferredWidth(102);
				 * table.getColumnModel().getColumn(2).setPreferredWidth(100);
				 */
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						CurrPrinter currPrinter = new CurrPrinter();
						currPrinter.setPrinterTable(table);
						currPrinter.setAlwaysOnTop(true);
						currPrinter.setVisible(true);
					}
				});
				buttonPane.add(btnAdd);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

	}
}
