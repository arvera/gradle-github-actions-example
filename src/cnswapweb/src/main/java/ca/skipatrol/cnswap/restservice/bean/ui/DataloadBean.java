package ca.skipatrol.cnswap.restservice.bean.ui;

public class DataloadBean {

	public String filename;
	public String vendortypeId;
	public String vendorId;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getVendortypeId() {
		return vendortypeId;
	}
	public void setVendortypeId(String vendortypeId) {
		this.vendortypeId = vendortypeId;
	}
	
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	
	@Override
	public String toString() {
		return "DataloadBean [filename=" + filename + ", vendortypeId=" + vendortypeId + ", vendorId=" + vendorId + "]";
	}
	
}
