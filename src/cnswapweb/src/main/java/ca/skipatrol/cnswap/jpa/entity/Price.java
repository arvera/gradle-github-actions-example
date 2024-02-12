package ca.skipatrol.cnswap.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity 
public class Price { 
    
	@Id
	private String id;
	private String name; 
	private String description;
	private String size;
	private Long price;
	private Boolean isNew;
	@ManyToOne
	@JoinColumn
	private CatentryType catentrytype;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	public CatentryType getType() {
		return catentrytype;
	}
	public void setType(CatentryType type) {
		this.catentrytype = type;
	}


}