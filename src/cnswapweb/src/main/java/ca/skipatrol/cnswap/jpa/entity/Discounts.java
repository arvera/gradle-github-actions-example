package ca.skipatrol.cnswap.jpa.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name="discounts")
public class Discounts { 
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name="auth_username")
    private String authUsername;
    
    @Column
    private String comment;
    
    @Column
    private String discountType;
    
    @Column
    private BigDecimal total;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthUsername() {
		return authUsername;
	}

	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Discounts [id=" + id + ", authUsername=" + authUsername + ", comment=" + comment + ", discountType="
				+ discountType + ", total=" + total + "]";
	}
    
    
}