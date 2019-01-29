package org.ltd3000.jasperprinter.utils;

/**
 * @author xushanshan
 * @date 20181221
 * @class jaspertask参数 Entity
 *
 */
public class JasperConfigEntity {
	@Override
	public String toString() {
		return "JasperConfigEntity [ID=" + ID + ", PRINTTYPE=" + PRINTTYPE + ", JASPERFILE=" + JASPERFILE
				+ ", IDENTITYORDER=" + IDENTITYORDER + ", WHERECASE=" + WHERECASE + ", AFTERJOB=" + AFTERJOB
				+ ", SQLQUERY=" + SQLQUERY + "]";
	}
	private String ID;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getPRINTTYPE() {
		return PRINTTYPE;
	}
	public void setPRINTTYPE(String pRINTTYPE) {
		PRINTTYPE = pRINTTYPE;
	}
	public String getJASPERFILE() {
		return JASPERFILE;
	}
	public void setJASPERFILE(String jASPERFILE) {
		JASPERFILE = jASPERFILE;
	}
	public int getIDENTITYORDER() {
		return IDENTITYORDER;
	}
	public void setIDENTITYORDER(int iDENTITYORDER) {
		IDENTITYORDER = iDENTITYORDER;
	}
	public String getWHERECASE() {
		return WHERECASE;
	}
	public void setWHERECASE(String wHERECASE) {
		WHERECASE = wHERECASE;
	}
	public String getAFTERJOB() {
		return AFTERJOB;
	}
	public void setAFTERJOB(String aFTERJOB) {
		AFTERJOB = aFTERJOB;
	}
	private String PRINTTYPE;
	private String JASPERFILE;
	private int IDENTITYORDER;
	private String WHERECASE;
	private String AFTERJOB;
	private String SQLQUERY;
	public String getSQLQUERY() {
		return SQLQUERY;
	}
	public void setSQLQUERY(String sQLQUERY) {
		SQLQUERY = sQLQUERY;
	}
	

}
