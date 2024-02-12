package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table (name="paymenttype")
public class PaymentType { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;
    
    @Column
    private Integer comission;
    
    @OneToMany(mappedBy = "paymentType")
    private List<Payment> payments;

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

	public Integer getComission() {
		return comission;
	}

	public void setComission(Integer comission) {
		this.comission = comission;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "PaymentType [id=" + id + ", name=" + name + ", comission=" + comission + ", payments=" + payments + "]";
	}
     
    
    

}