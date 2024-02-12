package ca.skipatrol.cnswap.jpa.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name="CNSFiles")
public class CNSFile {

	
		@Id
	    private String filename;
		
		private String cnstype;
		
	    private String url;

	    private int recordsProcessed;
	    	    
	    private int totalOfRecords;
	    
	    private long size;
	    
	    private Timestamp uploadedTime;
		
	    private String HRSize;
	    

	    public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	    
	    public Timestamp getUploadedTime() {
			return uploadedTime;
		}

		public void setUploadedTime(Timestamp uploadedTime) {
			this.uploadedTime = uploadedTime;
		}

		public int getTotalOfRecords() {
			return totalOfRecords;
		}

		public void setTotalOfRecords(int totalOfRecords) {
			this.totalOfRecords = totalOfRecords;
		}

		
	    public String getHRSize() {
			return HRSize;
		}

		public void setHRSize(String hRSize) {
			HRSize = hRSize;
		}

	    
		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}


		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public int getRecordsProcessed() {
			return recordsProcessed;
		}

		public void setRecordsProcessed(int recordsProcessed) {
			this.recordsProcessed = recordsProcessed;
		}

		public String getCnstype() {
			return cnstype;
		}

		public void setCnstype(String cnstype) {
			this.cnstype = cnstype;
		}
	
	
}
