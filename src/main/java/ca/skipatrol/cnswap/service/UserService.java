package ca.skipatrol.cnswap.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.restservice.bean.ui.UserUIBean;
import ca.skipatrol.cnswap.util.CNSwapLogger;

@Service
public class UserService {
	
	//https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
	//https://docs.spring.io/spring-security/site/apidocs/org/springframework/security/provisioning/JdbcUserDetailsManager.html

	
	Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private  UsersRepository userRepo;
	
	@Autowired
	private final UserDetailsManager userService;
	
	
	public UserService(UserDetailsManager userService) {
		this.userService = userService;
	}
	
	
	
	public List<UserDetails> getAllUsers(){
		final String METHODNAME = "getAllUsers";
		CNSwapLogger.logEntry(LOGGER, METHODNAME, "");
		
		List<User> allUsers = userRepo.findAll();
		List<UserDetails> allUserDetails = new ArrayList<UserDetails>();
		

		allUsers.forEach((user) -> {
			CNSwapLogger.trace(LOGGER,"Loading user="+user);
			try {
				UserDetails u = userService.loadUserByUsername(user.getUsername());				
				allUserDetails.add(u);
			}
			catch(Exception e) {
				CNSwapLogger.trace(LOGGER,"Not found:"+ user);
			}
		});
		
		
		return allUserDetails;
	}



	public void updatePassword(String username, String password1) {
		UserDetails user = userService.loadUserByUsername(username);
		
		User userDB = userRepo.findByUsername(username);
		userDB.setPassword(password1);
		userRepo.save(userDB);
		
	}
	
	public void deleteUser(String username) {
		
		userService.deleteUser(username);
		
	}
	
	public void updateUser(UserDetails userDetails,UserUIBean userUiInfo) {
		
		UserDetails ud = org.springframework.security.core.userdetails.User.builder()
				.username(userUiInfo.getUsername())
				.authorities(userUiInfo.getAuthorities())
				.disabled(!userUiInfo.getEnabled())
				.password(userUiInfo.getPassword())
				.build();
		userService.updateUser(ud);
	}
	
	public void createUser(UserUIBean userUiInfo) {
		
		UserDetails ud = org.springframework.security.core.userdetails.User.builder()
				.username(userUiInfo.getUsername())
				.authorities(userUiInfo.getAuthorities())
				.disabled(!userUiInfo.getEnabled())
				.password(userUiInfo.getPassword())
				.build();
		
		userService.createUser(ud);
		
	}

	public UserDetails getUserDetails(String username) {
		return userService.loadUserByUsername(username);
		
	}
	
	public User getUser(String username) {
		return userRepo.findByUsername(username);
		
	}
	
	public UserUIBean getUserUIBean(String username) {
		UserUIBean userUi = new UserUIBean(getUser(username));
		userUi.merge(getUserDetails(username));
		
		return userUi;
		
	}
	
	
}
