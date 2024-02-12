package ca.skipatrol.cnswap.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.restservice.bean.ui.UserUIBean;
import ca.skipatrol.cnswap.service.UserService;
import ca.skipatrol.cnswap.util.CNSwapLogger;

@RestController
public class UserController {
	
	@Autowired
	private final UserService userService;
	
	Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	// Constructor for populate the service
	public UserController(UserService UserService) {
		this.userService = UserService;
	}
	
    public void addViewControllers(ViewControllerRegistry registry) {
	 
		// 2nd level views: Admin
    	registry.addViewController("/admin/users").setViewName("admin/users");
    	registry.addViewController("/admin/userEdit").setViewName("admin/userEdit");
    	registry.addViewController("/admin/addUser").setViewName("admin/addUser");
		
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
    
    
    @GetMapping("/admin/users")
    public ModelAndView listUser(Model model,
    		@RequestParam(required = false) String action,
    		@RequestParam(required = false) String username) {
    	final String METHODNAME = "listUser";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);

    	// To populate the HTMLTable
    	List<UserDetails> userList =  userService.getAllUsers();
    	
    	if (action != null && action.compareToIgnoreCase("edit")==0){
    		addAttributeParam(model);
    		UserUIBean userUiInfo = userService.getUserUIBean(username);
    		model.addAttribute("userUiInfo", userUiInfo);
    		
    		
    	}
    	
    	
	    model.addAttribute("userDetailList", userList);
	    
    	return new ModelAndView("admin/users");
    }
    
    
    @GetMapping("/admin/addUser")
    public ModelAndView addUser(Model model) {
    	final String METHODNAME = "addUser";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);

    	// To populate the HTMLTable
    	List<UserDetails> userList =  userService.getAllUsers();
    	model.addAttribute("userDetailList", userList);
    	
    	
    	addAttributeParam(model);
    	
    	UserUIBean userUiInfo = new UserUIBean();
    	model.addAttribute("userUiInfo", userUiInfo);	
	    
	    
    	return new ModelAndView("admin/addUser");
    }
    
    
    
    
    @PostMapping("/admin/userPasswordUpdate")
    public ModelAndView userPasswordUpdate(Model model,
    		@RequestParam(required = true) String username,
    		@RequestParam(required = true) String password1,
    		@RequestParam(required = true) String password2,
    		RedirectAttributes redirectAttributes) {
    	final String METHODNAME = "userPasswordUpdate";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);

    	if (password1.compareTo(password2)!=0) {
    		redirectAttributes.addFlashAttribute("errorMessage","Password and confirmation did not match. Please retry");
    		return new ModelAndView("redirect:/admin/users");
    	}
    	userService.updatePassword(username,password1);
    	String message = String.format(Locale.US, "Password updated for [user=%s]",username);
    	redirectAttributes.addFlashAttribute("message",message);
	    
    	return new ModelAndView("redirect:/admin/users");
    }
    
    @PostMapping("/admin/userDelete")
    public ModelAndView userDelete(Model model,
    		@RequestParam(required = true) String username,
    		@RequestParam(required = true) String confirm,
    		RedirectAttributes redirectAttributes) {
    	final String METHODNAME = "userDelete";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);

    	if (confirm.compareTo(username)!=0) {
    		redirectAttributes.addFlashAttribute("errorMessage","The confirmation does not match the username. Remember that the username is case sensitive");
    		return new ModelAndView("redirect:/admin/users");
    	}
    	userService.deleteUser(username);
    	String message = String.format(Locale.US, "You have succesfuly deleted user %s",username);
    	redirectAttributes.addFlashAttribute("message",message);
    	
    	
    	return new ModelAndView("redirect:/admin/users");
    }
    
    
    public void addAttributeParam(Model model) {
    	final String METHODNAME = "addAttributeParam";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);
    			
		 // To populate the HTMLForm to create a new Vendor 
	    model.addAttribute("userInfo", new User());
	    CNSwapLogger.trace(LOGGER, "Adding the HARDOCODED ROLES");
	    model.addAttribute("availableRoleList", new String[]{"ROLE_ADMIN","ROLE_TELLER"});
	    
	    CNSwapLogger.logExit(LOGGER, METHODNAME, model);
    	
    }
    
    @PostMapping("/admin/userEdit")
    public ModelAndView userEdit(Model model,
    		 @ModelAttribute UserUIBean userUiInfo,
    		RedirectAttributes redirectAttributes) {
    	final String METHODNAME = "userEdit";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);
    	
    	UserDetails userDetails = userService.getUserDetails(userUiInfo.getUsername());
    	userService.updateUser(userDetails,userUiInfo);
    	
    	return new ModelAndView("redirect:/admin/users");
    }
    
    
    @PostMapping("/admin/addUser")
    public ModelAndView addUser(Model model,
    		 @ModelAttribute UserUIBean userUiInfo,
    		RedirectAttributes redirectAttributes) {
    	final String METHODNAME = "addUser";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);
    	
    	if (userUiInfo.getPassword().compareTo(userUiInfo.getPasswordConfirm())!=0) {
    		redirectAttributes.addFlashAttribute("errorMessage","Password and confirmation did not match. Please retry");
    		return new ModelAndView("redirect:/admin/addUser");
    	}
    	
    	userService.createUser(userUiInfo);
    	
    	return new ModelAndView("redirect:/admin/users");
    }
    
    
}
