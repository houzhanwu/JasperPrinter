package org.ltd3000.jasperprinter.service;

public class PrintService implements IPrintService{
	//打印机配置
	final String PRINTERCONFIGFILE = "./printer.properties";
	//系统设置
	final String SYSCONFIGFILE = "./sys.properties";


	//打印服务状态
	public boolean active = false;
	public final String PDFPATH = "pdfpath";

	public final String RPTPATH = "rptpath";

	public final String STATIONPATH = "stationpath";

	public String rptPath = "";
	
	public String pdfPath = "";

	public String stationPath = "";



	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return this.active;
	}

	@Override
	public void setActive(boolean active) {
		this.active=active;
		
	}

	@Override
	public void loadConfig() {
		
	}

	@Override
	public void loadPrinter() {
		
	}


}
