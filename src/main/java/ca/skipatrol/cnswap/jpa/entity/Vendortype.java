package ca.skipatrol.cnswap.jpa.entity;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity 
public class Vendortype { 
    
	@Id 
	@Column (name = "VENDORTYPE_ID")
    private String vendortypeId;
	
	@Column (name = "VENDORTYPE_NAME")
    private String vendortypeName;
    
    @Column (name = "COMMISSION")
    private Integer commission;
    
    @Column (name = "MINCATENTRYCOST")
    private Integer minCatEntryCost;
    
    @Column (name = "MINCOMISSION")
    private Integer minComission;
    
 
    @Column (name = "LISTITEMSONCHEQUE", columnDefinition="INT(1)")
    private Boolean listItemsOnCheque;

    @OneToMany(mappedBy = "vendortype")
    private List<Vendor> vendors;
    
   
    @ManyToMany(mappedBy="vendortype", cascade = CascadeType.PERSIST)
    private List<TaxEntry> taxEntry;
   
	

    public List<Vendor> getVendors() {
		return vendors;
	}

	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

	public List<TaxEntry> getTaxEntry() {
		return taxEntry;
	}

	public void setTaxEntry(TaxEntry[] taxEntry) {
		this.taxEntry = Arrays.asList(taxEntry);
	}
	
//	public void setTaxEntry(TaxEntry[] taxEntry) {
//		setTaxEntry( taxEntry;
//	}

	public String getVendortypeId() {
		return vendortypeId;
	}

	public void setVendortypeId(String vendortypeId) {
		this.vendortypeId = vendortypeId;
	}

	public String getVendortypeName() {
		return vendortypeName;
	}

	public void setVendortypeName(String vendortypeName) {
		this.vendortypeName = vendortypeName;
	}

	public Integer getCommission() {
		return commission;
	}

	public void setCommission(Integer commission) {
		this.commission = commission;
	}

	public Integer getMinCatEntryCost() {
		return minCatEntryCost;
	}

	public void setMinCatEntryCost(Integer minCatEntryCost) {
		this.minCatEntryCost = minCatEntryCost;
	}

	public Integer getMinComission() {
		return minComission;
	}

	public void setMinComission(Integer minComission) {
		this.minComission = minComission;
	}

	
	public Boolean getListItemsOnCheque() {
		return listItemsOnCheque;
	}

	public void setListItemsOnCheque(Boolean listItemsOnCheque) {
		this.listItemsOnCheque = listItemsOnCheque;
	}

    public String toString() {
        return "vendorType{" +
                "vendorTypeId='" + vendortypeId + '\'' +
                ", vendorTypeName='" + vendortypeName + "\'" +
                ", commission='" + commission + "\'" +
                ", minCatEntryCost='" + minCatEntryCost + '\'' +
                ", minComission='" + minComission + '\'' +
                ", listItemsOnCheque='" + listItemsOnCheque + '\'' +
                ", taxEntry='"+taxEntry.size()+
                '}';
    }
    
    public Vendortype() {
    	
    }
    
    

}