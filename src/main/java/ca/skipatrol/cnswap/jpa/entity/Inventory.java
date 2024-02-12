package ca.skipatrol.cnswap.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name="inventory")
public class Inventory {


	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	private Integer id;
	
	@Column(name="status")
	private String status;
	
	@Column(name="qty")
	private Integer quantity;
	
	// from: https://www.baeldung.com/jpa-joincolumn-vs-mappedby
	@OneToOne(fetch = FetchType.LAZY,mappedBy = "inventory")
	private Catentry catentry;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Catentry getCatentry() {
		return catentry;
	}

	public void setCatentry(Catentry catentry) {
		this.catentry = catentry;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Inventory [id=" + id + ", catentry=" + catentry + ", status=" + status + ", quantity=" + quantity + "]";
	} 

	
}
