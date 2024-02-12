package ca.skipatrol.cnswap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.OrdersRepository;
import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.service.TransactionService;
import ca.skipatrol.cnswap.util.CNSwapUtil;
import ca.skipatrol.cnswap.util.OrderStatus;
import ca.skipatrol.cnswap.util.SwapyAppConfig;
import ca.skipatrol.cnswap.util.SystemMessages;
import jakarta.servlet.http.HttpSession;

@RestController
@SessionAttributes("order")
public class EndTransaction {

	
	@Autowired
	private final TransactionService transactionService;
	
	@Autowired
	UsersRepository userRepo;
	
	
	@Autowired
	OrdersRepository ordersRepo;
	
	// Constructor for populate the service
	public EndTransaction(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/endtransaction").setViewName("register/endtransaction");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    
    // Following the documentation at: https://www.baeldung.com/spring-mvc-session-attributes
    @ModelAttribute("order")
	public Order order() {
    	Order order = new Order();
    	// Set the username on the order
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userRepo.findByUsername(auth.getName());
    	order.setTransUsername(user);
    	order.setTransTime(CNSwapUtil.getCurrentTimestamp());
    	order = transactionService.save(order);
	    return order;
	}
	
	

    @GetMapping("/register/endtransaction")
    public ModelAndView endTransaction(Model model,
    		@RequestParam(required = false) String orderId,
    		WebRequest request,
    		@ModelAttribute("order") Order order) {    
		
    	ModelAndView rv= new ModelAndView("register/endtransaction");
    	
    	//refresh the order
    	order = ordersRepo.findOneById(order.getId());
    	
    	// ACTION=<empty>, default action print
    	if (order == null) {
    		if (!orderId.isEmpty()) {
    			order = ordersRepo.findOneById(Integer.parseInt(orderId));
    			if (order == null) {
    				model.addAttribute("errorMessage", "Order not Found!");
    			}
    		}
    	}
    	
    	model.addAttribute("order", order);
    	
    	
    	String copiesToPrint = CNSwapUtil.getCopiesToPrintInHTMLAttribute(); 
    	model.addAttribute("copiesToPrint", 2);
   	
    	transactionService.completeOrder(order);
		
    	return rv;
    }
    
  
  
    
    @PostMapping("/register/endtransaction")
    public ModelAndView endTransaction( 
    		@RequestParam(required = true) String orderId,
    		WebRequest request,
    		RedirectAttributes redirectAttributes) {
    	ModelAndView rv = new ModelAndView("redirect:/register/newtransaction");
    	
    	
	
		Order order = ordersRepo.findOneById(Integer.parseInt(orderId));
		if (order == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Order not Found!");
			rv = new ModelAndView("redirect:/register/endtransaction");
			return rv;
		}
		
		
		
		try {
			// This code appears to remove the order, but for some reason the session in the redirect still has the order
			HttpSession session = getSession();
			session.removeAttribute("order");
			request.removeAttribute("order", WebRequest.SCOPE_SESSION);
			request.removeAttribute("orderId", WebRequest.SCOPE_SESSION);
        } catch (RuntimeException ex) {
            String errorMessage = SystemMessages.CNSWAP_ERR004_MSG;
            System.out.println(SystemMessages.CNSWAP_ERR004 +": "+ ex);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
    	return rv;  
    }
    
    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true == allow create
    }
    
}
