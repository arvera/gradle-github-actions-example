package ca.skipatrol.cnswap.jpa.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name="taxinstructions")
public class TaxInstruction { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
  	@JoinColumn (name = "ORDERSID")
    private Order ordersId;
    
    @Column(name="TOTAL")
    private BigDecimal total;
    
    
    @Column (name="NAME")
    private String name;
        

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Order getOrdersId() {
		return ordersId;
	}

	public void setOrdersId(Order ordersId) {
		this.ordersId = ordersId;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TaxInstructions [id=" + id + ", ordersId=" + ordersId + ", total=" + total + ", name=" + name+ "]";
	}
    

}