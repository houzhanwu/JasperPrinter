package org.ltd3000.jasperprinter.service;

public interface IPrintService {

	public boolean isActive();//获取服务状态

	public void setActive(boolean active);//设置服务状态

	public void loadConfig();//加载系统设置

	public void loadPrinter();//加载打印机
}
