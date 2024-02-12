package ca.skipatrol.cnswap.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity 
public class CatentryType { 
   

	@Id 
	@Column (name = "CATENTRYTYPE_ID")
    private String catentryTypeId;

	@Column (name = "CATENTRYTYPE_NAME")
    private String catentryTypeName;
	
	public String getCatentryTypeId() {
		return catentryTypeId;
	}

	public void setCatentryTypeId(String catentryTypeId) {
		this.catentryTypeId = catentryTypeId;
	}

	public String getCatentryTypeName() {
		return catentryTypeName;
	}

	public void setCatentryTypeName(String catentryTypeName) {
		this.catentryTypeName = catentryTypeName;
	}

    public String toString() {
        return "catentryType{" +
                "catentryTypeId='" + catentryTypeId + '\'' +
                ", catentryTypeName='" + catentryTypeName + "\'" +
                '}';
    }

}