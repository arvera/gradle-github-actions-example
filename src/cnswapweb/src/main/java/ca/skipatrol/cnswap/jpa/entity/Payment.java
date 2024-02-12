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
@Table (name="payment")
public class Payment { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
  	@JoinColumn (name = "ordersId")
    private Order ordersId;
    
    @Column
    private BigDecimal total;
    
    
    private String auth;
        
    @ManyToOne
	@JoinColumn (name = "paymenttype_id")
    private PaymentType paymentType;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Order getOrdersId() {
		return ordersId;
	}


	public void setOrdersId(Order order) {
		this.ordersId = order;
	}


	public BigDecimal getTotal() {
		return total;
	}


	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	
	public String getAuth() {
		return auth;
	}


	public void setAuth(String auth) {
		this.auth = auth;
	}


	public PaymentType getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}


	@Override
	public String toString() {
		return "Payment [id=" + id + ", ordersId=" + ordersId.getId() + ", total=" + total + ",  auth=" + auth + ", paymentType=" + paymentType + "]";
	}

    
    
    

}