package ca.skipatrol.cnswap.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.restservice.bean.UserSession;
import ca.skipatrol.cnswap.util.CNSwapUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
public class LogonController implements WebMvcConfigurer {

   
    public void addViewControllers(ViewControllerRegistry registry) {
    	// 1st level views: Login and Navigation
    	registry.addViewController("/").setViewName("home");
    	registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/manage").setViewName("manage");
        
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        	
    }
    
	
	protected Map<String,UserSession> sessions = new HashMap<String,UserSession>();
	
	@Autowired
	UsersRepository users;

	
	@GetMapping("/debug")
	public String Debug() {
		
		String sessionId = sessions.size()+1+CNSwapUtil.java8_hash();		
		sessions.put(sessionId, new UserSession(""));
		
//		Users newUser = new Users(2L,"me","me");
//		users.save(newUser);
		
		Iterator<User> iter = users.findAll().iterator();
		StringBuffer strbuf = new StringBuffer();
		while (iter.hasNext()) {
			strbuf.append(iter.next().toString()+"\n");
		}
		System.out.println("DEBUG."+this.getClass().getCanonicalName() +": "+users.count());
		
		return strbuf.toString();
	}
	
	@GetMapping("/login") // NOT WORKING
	public ModelAndView login(Model model) {
		int ran= ThreadLocalRandom.current().nextInt(1, 5 + 1);
		System.out.println("DEBUG."+this.getClass().getCanonicalName() +": "+ran);
		model.addAttribute("randomNumber1to5", getRandomNumber1to5());
		return new ModelAndView("login","randomNumber1to5",getRandomNumber1to5());
	}
	
	public int getRandomNumber1to5() {
		int ran= ThreadLocalRandom.current().nextInt(1, 5 + 1);
		System.out.println("DEBUG."+this.getClass().getCanonicalName() +": "+ran);
		return ran;
	}
	
	@GetMapping("/custom-logout")
	public ModelAndView customLogout(HttpServletRequest request, HttpServletResponse response) {
	    // Get the Spring Authentication object of the current request.
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    System.out.println("DEBUG--- Custom logout");
	    // In case you are not filtering the users of this request url.
	    if (authentication != null){    
	        new SecurityContextLogoutHandler().logout(request, response, authentication); // <= This is the call you are looking for.
	    }
	    return new ModelAndView ("login","randomNumber1to5",getRandomNumber1to5());
	}
	
	@GetMapping("/admin")
	String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	String manager() {
		return "manager";
	}
	
	@GetMapping("/register")
	String register() {
		return "register";
	}
	
//	@GetMapping("/")
//	String home() {
//		return "home";
//	}
	
//	
////	@GetMapping("/login")
//	public String Login(@RequestParam String username,@RequestParam String password) {
//		
//		String sessionId = sessions.size()+1+HashGenerator.java8_hash();
//		
//		sessions.put(sessionId, new UserSession(username));
//		
//		return sessionId;
//	}
	
//	
//	@GetMapping("/logout")
//	public String Logoout(@RequestParam String username) {
//				
//		//sessions.get(username);
//		
//		return "user has been logged out";
//	}
//	
	
//	 @GetMapping("/admin")
//	  public String admin() {
//	    return 
//	        """
//            <html>
//              <head>
//                <title>Home</title>
//                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
//              </head>
//              <body>
//                <p>
//                  <a href="http://localhost:8080/users">Users</a>
//                </p>
//                <p>
//                  <a href="http://localhost:8080/admins">Admins</a>
//                </p>
//                <p>
//                  <a href="http://localhost:8080/logout">Log out</a>
//                </p>
//              </body>
//            </html>
//            """;
//	  }
//	 
	
}