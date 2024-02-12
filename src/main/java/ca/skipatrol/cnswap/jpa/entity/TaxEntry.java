package ca.skipatrol.cnswap.jpa.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity 
@Table (name="TAXENTRY")
public class TaxEntry { 
    
	@Id 
	@Column (name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column (name = "NAME")
    private String name;
	
	@Column (name = "REGISTRATION")
    private String registration;
	
	@Column (name = "PERCENTAGE")
    private BigDecimal percentage;
    

	@Column (name = "INCLUDEINREFUND")
    private Boolean includeInRefund;
    
    @ManyToMany
	@JoinTable (name = "vendortype_tax", 
	            joinColumns= {@JoinColumn(name="taxentry_id") },
	            inverseJoinColumns = {@JoinColumn(name="vendorType_id") }
				)
    private List<Vendortype> vendortype;

	@Transient
    private String isChecked;
    

	public String isChecked() {
		return isChecked;
	}

	public void setChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public Boolean getIncludeInRefund() {
		return includeInRefund;
	}

	public void setIncludeInRefund(Boolean includeInRefund) {
		this.includeInRefund = includeInRefund;
	}
	
	
	public List<Vendortype> getVendortype() {
		return vendortype;
	}

	public void setVendortype(List<Vendortype> vendortype) {
		this.vendortype = vendortype;
	}
	
	
	@Override
	public String toString() {
		return "TaxEntry [id=" + id + ", name=" + name + ", registration=" + registration + ", percentage=" + percentage
				+ ", includeInRefund=" + includeInRefund + "]";
	}

	 @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaxEntry other = (TaxEntry) obj;
		return Objects.equals(id, other.id);
	}

	

}