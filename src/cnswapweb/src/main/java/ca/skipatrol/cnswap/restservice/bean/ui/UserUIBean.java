package ca.skipatrol.cnswap.restservice.bean.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import ca.skipatrol.cnswap.jpa.entity.User;

public class UserUIBean { 
    
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", enabled=" + enabled + "]";
	}


    private String username;


    private String password;
    
    private String passwordConfirm;
    

    public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}


	private Boolean enabled;


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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

  
    public UserUIBean() {
    	
    }
    
  
    public UserUIBean(String username,String password,Boolean enabled) {
    	this.username = username;
    	this.password = password;
    	this.enabled = enabled;
    }
    
    public UserUIBean(String username,String password) {
    	this.username = username;
    	this.password = password;
    	this.enabled = false;
    	
    }

	public UserUIBean(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.enabled = user.getEnabled() >= 0? true:false ;
	}

	
	public UserUIBean(UserDetails userDetails) {
		this.username = userDetails.getUsername();
		this.password = userDetails.getPassword();
		this.enabled = userDetails.isEnabled();
		Set<String> authorityList = new HashSet<String>();
		userDetails.getAuthorities().forEach( (authority) -> {
			 authorityList.add(authority.getAuthority());
		});
	}

	public void merge(UserDetails userDetails) {
		if (this.username == null)
			this.username = userDetails.getUsername();
		
		if (this.password == null)
			this.password = userDetails.getPassword();
		
		if (this.enabled == null)
			this.enabled = userDetails.isEnabled();
		
		Set<String> authorityList = new HashSet<String>();
		userDetails.getAuthorities().forEach( (authority) -> {
			 authorityList.add(authority.getAuthority());
		});
		this.authorities = authorityList.toArray(new String[0]);
		
	}

}