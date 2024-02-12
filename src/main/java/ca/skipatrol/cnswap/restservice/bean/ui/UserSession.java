package ca.skipatrol.cnswap.restservice.bean.ui;


import java.time.LocalDateTime;

public class UserSession {
	String userId;
	LocalDateTime logonTime;	
	Integer sessionExpiryTimeInMinutes;
	LocalDateTime lastLogon;

	public UserSession (String userId) {
		this.userId = userId;
		this.logonTime = LocalDateTime.now(); 
		this.lastLogon = getLastLogonTime();
		this.sessionExpiryTimeInMinutes = getSessionExpiryTime();
	}
	
	private LocalDateTime getLastLogonTime() {
		// TODO: Retrieve value from DB
		return null;
	}
	
	private Integer getSessionExpiryTime() {
		// TODO: Retrieve value from configuration 
		return 300;
	}
}
