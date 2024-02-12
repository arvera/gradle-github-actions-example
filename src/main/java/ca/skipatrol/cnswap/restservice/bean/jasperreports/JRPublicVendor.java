package ca.skipatrol.cnswap.restservice.bean.jasperreports;

import java.util.List;

import ca.skipatrol.cnswap.jpa.entity.Catentry;

public class JRPublicVendor {
	private String vendorId;
	private List<Catentry> catEntryList;
	private String catentry;
	private String status;
	
	public String getCatentry() {
		return catentry;
	}
	public void setCatentry(String catentry) {
		this.catentry = catentry;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public List<Catentry> getCatEntryList() {
		return catEntryList;
	}
	public void setCatEntryList(List<Catentry> catEntryList) {
		this.catEntryList = catEntryList;
	}
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
}
