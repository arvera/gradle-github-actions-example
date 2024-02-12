package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity 
@Table (name="users")
public class User { 
    
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", enabled=" + enabled + "]";
	}

	@Id
    private String username;

    @Column
    private String password;
    
    @Column
    private Integer enabled;

//    @OneToMany
//	@JoinColumn (name = "username")
//	private List<Authorities> authorities;
    
    @Transient
    private String[] authorities;


	public String[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

  
    public User() {
    	
    }
    
    public User(String username,String password,Integer enabled) {
    	this.username = username;
    	this.password = password;
    	this.enabled = enabled;
    }
    
    public User(String username,String password) {
    	this.username = username;
    	this.password = password;
    	this.enabled = 1;
    	
    }
    

}