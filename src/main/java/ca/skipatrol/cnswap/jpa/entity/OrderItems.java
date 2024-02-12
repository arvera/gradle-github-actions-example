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
@Table (name="orderitems")
public class OrderItems { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String catentry_id;
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @Column
    private String comment;

	@Column
    private String size;
    
    @Column
    private String type;
    
    @Column
    private BigDecimal unitPrice;

	@ManyToOne
	@JoinColumn (name = "orders_id")
    private Order order;
    
    @Column
    private Integer discount_id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCatentry_id() {
		return catentry_id;
	}

	public void setCatentry_id(String catentry_id) {
		this.catentry_id = catentry_id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Order getOrders_id() {
		return order;
	}

	public void setOrders_id(Order orders_id) {
		this.order = orders_id;
	}

	public Integer getDiscount_id() {
		return discount_id;
	}

	public void setDiscount_id(Integer discount_id) {
		this.discount_id = discount_id;
	}

    
    public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "OrderItems [id=" + id + ", catentry_id=" + catentry_id + ", name=" + name + ", description="
				+ description + ", size=" + size + ", type=" + type + ", unitPrice=" + unitPrice + 
				", discount_id=" + discount_id + "]";
	}
	
	
    
      

    

}