package org.ltd3000.jasperprinter.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.ltd3000.jasperprinter.service.PrintDatabaseService;
import org.ltd3000.jasperprinter.service.PrintXmlService;
import org.ltd3000.jasperprinter.utils.ConfigUtil;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class SysManager extends JDialog {

	private static final long serialVersionUID = 7327087983900069194L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtXMLPath;
	private JTextField txtRptPath;
	private JTextField txtStationPath;
	private JTextField txtPdfPath;
	private JTextField txtUrlPath;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SysManager dialog = new SysManager();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SysManager() {
		setResizable(false);
		setTitle("系统管理");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		PrintXmlService service = PrintXmlService.getInstance();
		//显示database数据库连接配置
		if (ConfigUtil.getProperty("printtype").contains("database")) {
			JLabel labelUrl = new JLabel("URL路径:");
			labelUrl.setBounds(10, 13, 54, 15);
			contentPanel.add(labelUrl);
			
			txtUrlPath = new JTextField();
			txtUrlPath.setEditable(true);
			txtUrlPath.setColumns(10);
			txtUrlPath.setBounds(62, 10, 331, 21);
			txtUrlPath.setText(ConfigUtil.getProperty("urlpath"));
			
			contentPanel.add(txtUrlPath);
		}else {
		//XML 设置
		JLabel lblNewLabel = new JLabel("XML路径:");
		lblNewLabel.setBounds(10, 13, 54, 15);
		contentPanel.add(lblNewLabel);
		
		txtXMLPath = new JTextField();
		txtXMLPath.setEditable(false);
		txtXMLPath.setBounds(62, 10, 331, 21);
		contentPanel.add(txtXMLPath);
		txtXMLPath.setColumns(10);
		txtXMLPath.setText(service.getXmlPath());
		
		JButton btnSelectXml = new JButton("...");
		btnSelectXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				if (file.isDirectory()) {
					txtXMLPath.setText(file.getAbsolutePath());
				} else if (file.isFile()) {
					txtXMLPath.setText(file.getAbsolutePath());
				}
			}
		});
		btnSelectXml.setBounds(403, 9, 21, 23);
		contentPanel.add(btnSelectXml);
		//station
		JLabel label = new JLabel("工作区路径:");
		label.setBounds(10, 78, 54, 15);
		contentPanel.add(label);

		JLabel labelPdf = new JLabel("PDF路径:");
		labelPdf.setBounds(10, 111, 54, 15);
		contentPanel.add(labelPdf);


		txtStationPath = new JTextField();
		txtStationPath.setEditable(false);
		txtStationPath.setColumns(10);
		txtStationPath.setBounds(62, 75, 331, 21);
		txtStationPath.setText(service.getStationPath());
		contentPanel.add(txtStationPath);

		JButton btnSelectStationPath = new JButton("...");
		btnSelectStationPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				if (file.isDirectory()) {
					txtStationPath.setText(file.getAbsolutePath());
				} else if (file.isFile()) {
					txtStationPath.setText(file.getAbsolutePath());
				}
			}
		});
		btnSelectStationPath.setBounds(403, 74, 21, 23);
		contentPanel.add(btnSelectStationPath);
		}

		JButton btnSelectRpt = new JButton("...");
		btnSelectRpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				if (file.isDirectory()) {
					txtRptPath.setText(file.getAbsolutePath());
				} else if (file.isFile()) {
					txtRptPath.setText(file.getAbsolutePath());
				}
			}
		});
		btnSelectRpt.setBounds(403, 41, 21, 23);
		contentPanel.add(btnSelectRpt);

		txtRptPath = new JTextField();
		txtRptPath.setEditable(false);
		txtRptPath.setColumns(10);
		txtRptPath.setBounds(62, 42, 331, 21);
		txtRptPath.setText(service.getRptPath());
		contentPanel.add(txtRptPath);

		JLabel lblRpt = new JLabel("Jasper路径:");
		lblRpt.setBounds(10, 45, 54, 15);
		contentPanel.add(lblRpt);

		
		//////////////////// PDF路径
		txtPdfPath = new JTextField();
		txtPdfPath.setEditable(false);
		txtPdfPath.setColumns(10);
		txtPdfPath.setBounds(62, 108, 331, 21);
		txtPdfPath.setText(service.getPdfPath());
		contentPanel.add(txtPdfPath);

		JButton btnSelectPdfPath = new JButton("...");
		btnSelectPdfPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				if (file.isDirectory()) {
					txtPdfPath.setText(file.getAbsolutePath());
				} else if (file.isFile()) {
					txtPdfPath.setText(file.getAbsolutePath());
				}
			}
		});
		btnSelectPdfPath.setBounds(403, 108, 21, 23);
		contentPanel.add(btnSelectPdfPath);
		//

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {							
							if (ConfigUtil.getProperty("printtype").contains("database")) {
								PrintDatabaseService service = PrintDatabaseService.getInstance();
							service.saveConfigPath( txtRptPath.getText(), txtStationPath.getText(),
									txtPdfPath.getText(), txtUrlPath.getText());
							}else if(ConfigUtil.getProperty("printtype").contains("xml")) {
								PrintXmlService service = PrintXmlService.getInstance();
								service.saveConfigPath(txtXMLPath.getText(), txtRptPath.getText(), txtStationPath.getText(),
										txtPdfPath.getText());
							}
							JOptionPane.showMessageDialog(null, "保存成功!");
						} catch (Exception e1) {
							e1.printStackTrace();
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
}
