package ca.skipatrol.cnswap.jpa.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table (name="orders")
public class Order { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column
//    private Integer calculation_id;
    
    @Column
    private Integer discount_id;
    
    @Column
    private BigDecimal subTotal;
    
    @Column
    private BigDecimal total;
    
    @Column
    private String status;
    
    @Column
    private String comment;

	@Column(name = "trans_time")
    private Timestamp transTime;
    
    @ManyToOne
   	@JoinColumn (name = "trans_username")
    private User transUsername;
    
    @OneToMany(mappedBy="order" ,cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;
    
    @OneToMany(mappedBy = "ordersId", cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    @OneToMany(mappedBy = "ordersId", cascade = CascadeType.ALL)
    private List<TaxInstruction> taxInstructions;
    
    
	public List<TaxInstruction> getTaxInstructions() {
		return taxInstructions;
	}

	public void setTaxInstructions(List<TaxInstruction> taxInstructions) {
		this.taxInstructions = taxInstructions;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getDiscount_id() {
		return discount_id;
	}

	public void setDiscount_id(Integer discount_id) {
		this.discount_id = discount_id;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Timestamp getTransTime() {
		return transTime;
	}

	public void setTransTime(Timestamp transTime) {
		this.transTime = transTime;
	}

	public User getTransUsername() {
		return transUsername;
	}

	public void setTransUsername(User transUsername) {
		this.transUsername = transUsername;
	}

	public List<OrderItems> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItems> orderItems) {
		this.orderItems = orderItems;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getComment() {
		return comment;
	}

	public void setComment(String comments) {
		this.comment = comments;
	}
	
//	@Override
//	public String toString() {
//		return "Order [id=" + id + ", discount_id=" + discount_id + ", subTotal=" + subTotal + ", total=" + total
//				+ ", status=" + status + ", transTime=" + transTime + ", transUsername=" + transUsername.getUsername()
//				+ ", taxInstructions=" + taxInstructions.size() + ", payments=" + payments.size()+ ", orderItems=" + orderItems + "]";
//	}
        
	@Override
	public String toString() {
		return "Order [id=" + id + ", discount_id=" + discount_id + ", subTotal=" + subTotal + ", total=" + total
				+ ", status=" + status + ", transTime=" + transTime + ", transUsername=" + transUsername.getUsername()
				+ ", orderItems=" + orderItems + "]";
	}
    

}