package org.ltd3000.jasperprinter.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.ltd3000.jasperprinter.service.PrintXmlService;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 打印机选择面板
 */
public class CurrPrinter extends JDialog {

	private static final long serialVersionUID = 5122058478405835376L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtPrintName;

	private JTable printerTable = null;

	public JTable getPrinterTable() {
		return printerTable;
	}

	public void setPrinterTable(JTable printerTable) {
		this.printerTable = printerTable;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CurrPrinter dialog = new CurrPrinter();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建对话框
	 */
	public CurrPrinter() {
		setTitle("当前打印机");
		setBounds(100, 100, 367, 175);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("打印机注册名:");
		lblNewLabel.setBounds(22, 28, 88, 15);
		contentPanel.add(lblNewLabel);

		txtPrintName = new JTextField();
		txtPrintName.setBounds(107, 26, 223, 25);
		contentPanel.add(txtPrintName);
		txtPrintName.setColumns(10);

		JLabel label = new JLabel("系统打印机名:");
		label.setBounds(22, 64, 88, 15);
		contentPanel.add(label);

		JComboBox<String> cbxOSPrinterName = new JComboBox<String>();
		cbxOSPrinterName.setBounds(107, 59, 223, 25);
		loadOSPrinterName(cbxOSPrinterName);

		contentPanel.add(cbxOSPrinterName);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PrintXmlService service = PrintXmlService.getInstance();
						if (txtPrintName.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "注册打印机不能为空!");
							return;
						}
						if (service.savePrinter(txtPrintName.getText().toUpperCase(),
								cbxOSPrinterName.getSelectedItem() + "")) {
							printerTable.setModel(new DefaultTableModel(service.getPrinterModel(),
									new String[] { "\u5E8F\u53F7", "\u6CE8\u518C\u6253\u5370\u673A",
											"\u7CFB\u7EDF\u6253\u5370\u673A", "\u662F\u5426\u4F7F\u7528" }));
							JOptionPane.showMessageDialog(contentPanel, "打印机处理成功!");
						} else {
							JOptionPane.showMessageDialog(contentPanel, "打印机处理失败!");
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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

	/**
	 * 获取系统安装的打印机服务列表名称
	 */
	public void loadOSPrinterName(JComboBox<String> cbxOSPrinterName) {
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;

		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		for (int i = 0; i < printService.length; i++) {
			cbxOSPrinterName.addItem(printService[i].getName());
		}
		// 设置列表默认选项
		PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
		cbxOSPrinterName.setSelectedItem(ps.getName());

	}
}
