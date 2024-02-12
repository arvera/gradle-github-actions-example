package ca.skipatrol.cnswap.jpa.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity 
public class Catentry { 
    

	
	@Id
	@Column (name="id")
	private String id;
	@Column (name="name")
	private String name;
	@Column (name="description")
	private String description;
	@Column (name="size")
	private String size;
	@Column (name="NEW")
	private Boolean isNew;
	@Column (name="price")
	private Long price;
	@Column (name="comment")
	private String comment;
	

	@ManyToOne
	@JoinColumn (name="CATENTRYTYPE")
	private CatentryType catentrytype;
	
	@ManyToOne
	@JoinColumn (name="VENDOR")
	private Vendor vendor;
	
	// from: https://www.baeldung.com/jpa-joincolumn-vs-mappedby
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="inventory")
	private Inventory inventory;
	
	
	
	public Vendor getVendor() {
		return vendor;
	}
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
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
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	public void setType(CatentryType type) {
		this.catentrytype = type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public String toString() {
		return "Catentry [id=" + id + ", name=" + name + ", description=" + description + ", size=" + size + ", isNew="
				+ isNew + ", price=" + price + ", catentrytype=" + catentrytype + "]";
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}

}