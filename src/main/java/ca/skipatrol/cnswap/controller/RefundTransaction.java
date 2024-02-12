package ca.skipatrol.cnswap.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.service.TransactionService;
import ca.skipatrol.cnswap.util.CNSwapLogger;
import ca.skipatrol.cnswap.util.OrderStatus;
import ca.skipatrol.cnswap.util.SystemMessages;

@RestController
public class RefundTransaction {
	Logger LOGGER = LoggerFactory.getLogger(RefundTransaction.class);
	
	@Autowired
	private final TransactionService transactionService;
	
	@Autowired
	private final AuthenticationManager authManager;
	
	@Autowired
	UsersRepository userRepo;
	
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public RefundTransaction(TransactionService transactionService,AuthenticationManager authManager) {
		this.transactionService = transactionService;
		this.authManager = authManager;
	}
	
	
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/refundtransaction").setViewName("register/refundtransaction");
		registry.addViewController("/register/refundconfirmation").setViewName("register/refundconfirmation");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

    public Order getOrder(String orderId) {
    	return transactionService.getOrder(Integer.valueOf(orderId));
    }
    
    
    public Order refundOrderId(String orderId,String refundApprovalUsername) {
    	return transactionService.refundOrder(orderId,refundApprovalUsername);
    }
    
    
    private boolean isUserAuthorized(String username,String password) {
    
    	UsernamePasswordAuthenticationToken authReq	 = new UsernamePasswordAuthenticationToken(username, password);
    	
    	try {
    		Authentication auth = authManager.authenticate(authReq);
    		Collection<SimpleGrantedAuthority> roles = (Collection<SimpleGrantedAuthority>) auth.getAuthorities();
    		Iterator<SimpleGrantedAuthority> roleIterator = roles.iterator();
    		while (roleIterator.hasNext()) {
    			SimpleGrantedAuthority aRole = (SimpleGrantedAuthority )roleIterator.next();
    			if (aRole.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || aRole.getAuthority().equalsIgnoreCase("ROLE_MANAGER")) {
    				return true;
    			}
    		}
    		
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	return false;
    }
    
    private boolean isUserAuthenticated(String username, String password ) {
    	
    	User user = userRepo.findByUsername(username);
    	
    	if (user == null) {
    		return false;
    	}
    	
    	if (user.getEnabled() <= 0) {
			return false;
    	}
    	
    	UsernamePasswordAuthenticationToken authReq	 = new UsernamePasswordAuthenticationToken(username, password);
    	try {
    		Authentication auth = authManager.authenticate(authReq);
    		if (!auth.isAuthenticated()) {
            	return false;    			
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	return true;
    	
    }


    // ****************    
    // **    GET    **
    // ****************
    
    @GetMapping("/register/refundconfirmation")
    public ModelAndView refundConfirmationGet(Model model,
    		@RequestParam(required = false) String orderId,
    		@RequestParam(required = false) String originalOrderId,
    		WebRequest request) {    
		
    	ModelAndView rv= new ModelAndView("register/refundconfirmation");

		
    	return rv;
    }
    
    
    @GetMapping("/register/refundtransaction")
    public ModelAndView refundTransactionGet(Model model,
    		@RequestParam(required = false) String orderId,
    		WebRequest request) { 
    	final String METHODNAME = "refundTransactionGet";
    	CNSwapLogger.logEntry(LOGGER,METHODNAME,orderId);
    	CNSwapLogger.trace(LOGGER, "GET:/register/refundtransaction");
    	
    	ModelAndView rv= new ModelAndView("register/refundtransaction");
    	String errorMessage = null;
    	
    	if (orderId != null) {
    		Order order = getOrder(orderId);
    		CNSwapLogger.trace(LOGGER, "found order="+order);
    		if (order == null) {
    			errorMessage = String.format(Locale.US, "Unable to find ORDER_ID=[%s]",orderId);
    			CNSwapLogger.trace(LOGGER, errorMessage);
    			model.addAttribute("errorMessage",errorMessage);
    			CNSwapLogger.logExit(LOGGER, METHODNAME, rv);
    			return rv;
    		}
    			
    		model.addAttribute("order",order);
    		
    		CNSwapLogger.trace(LOGGER,"Check orderStatus");
    		if (order.getStatus() == null || OrderStatus.isRefunded(order.getStatus()) || OrderStatus.isPending(order.getStatus()) ){
    			errorMessage = String.format(Locale.US, "Unable to start refund for ORDER_ID=[%s], because of an invalid order state.[OrderState=%s]",order.getId().toString(),order.getStatus());
    			CNSwapLogger.trace(LOGGER,errorMessage);
    		}
    		
    	}

    	model.addAttribute("errorMessage",errorMessage);
    	
		CNSwapLogger.logExit(LOGGER, METHODNAME, rv);
    	return rv;
    }
    
    
    // ****************    
    // **    POST    **
    // ****************
    
    @PostMapping("/register/refundconfirmation")
    public ModelAndView refundConfirmationPost(Model model,
    		@RequestParam(required = true) String orderId,
    		@RequestParam(required = true) String originalOrderId,
    		@RequestParam(required = true) String authToken,
    		@RequestParam(required = false) String comment,
    		WebRequest request) {
    	final String METHODNAME = "refundConfirmationPost";
    	CNSwapLogger.logEntry(LOGGER,METHODNAME,originalOrderId,authToken,comment);
    	CNSwapLogger.trace(LOGGER, "POST:/register/refundconfirmation");
		
    	ModelAndView rv= new ModelAndView("register/refundconfirmation");
    	
    	CNSwapLogger.trace(LOGGER, "Getting orderId="+orderId);
    	Order order = getOrder(orderId);
		
		String errorMessage = new String();
		CNSwapLogger.trace(LOGGER,"Is order in pending?");
		if (OrderStatus.isPending(order.getStatus())){
			transactionService.completeRefund(order,originalOrderId,authToken,comment);
			String message = String.format(Locale.US, "Order [%s] has been returned. [%s] Items on that order have been made available.", originalOrderId,String.valueOf(order.getOrderItems().size()));
			model.addAttribute("message",message);
		}
		else {
			errorMessage = String.format(Locale.US, "Unable to complete refund for %s, because of an invalid order state.[%s,%s]",originalOrderId,order.getId(),order.getStatus());
			CNSwapLogger.trace(LOGGER,errorMessage);
		}			
		
    	if (errorMessage != null && !errorMessage.isEmpty())
    		model.addAttribute("errorMessage",errorMessage);
    	
    	CNSwapLogger.logExit(LOGGER,METHODNAME,rv);
    	return rv;
    }
    
    
    @PostMapping("/register/refundtransaction")
    public ModelAndView refundTransactionPost(Model model,
    		@RequestParam(required = true) String orderId,
    		@RequestParam(required = true) String refundUserId,
    		@RequestParam(required = true) String refundPassword,
    		WebRequest request,
    		RedirectAttributes redirectAttributes) {    
		
    	
    	ModelAndView rv= new ModelAndView("register/refundtransaction");
    	Order refundOrder = null;
    	String errorMessage = new String();
    	
    	
    	if (!isUserAuthenticated(refundUserId,refundPassword)) {
    		errorMessage = String.format(Locale.US, "Unable to authenticate user: %s",refundUserId);
    		model.addAttribute("errorMessage",errorMessage);
        	return rv; 
    	}
    	
    	
		if (!isUserAuthorized(refundUserId,refundPassword)) {
			errorMessage = String.format(Locale.US, "User %s does not have authorization for refund. Elevated authorization is required",refundUserId);
			model.addAttribute("errorMessage",errorMessage);
        	return rv; 
		}
		
		// If user is AUTHENTICATED and AUTHORIZED then refund the order 
		if (errorMessage == null || errorMessage.isEmpty()) {
			refundOrder = refundOrderId(orderId,refundUserId);
			CNSwapLogger.trace(LOGGER,"refundOrder="+refundOrder);
		}
    	
		// did the REFUND worked? 
    	if (refundOrder == null) {
    		errorMessage = String.format(Locale.US, SystemMessages.CNSWAP_ERR006_MSG,orderId,SystemMessages.CNSWAP_ERR006);
    		redirectAttributes.addFlashAttribute("errorMessage",errorMessage);
    		rv = new ModelAndView("/register/refundtransaction");
    		model.addAttribute("errorMessage",errorMessage);
        	return rv; 
    	}
    	
    	
    	if (refundOrder != null) {
    		redirectAttributes.addFlashAttribute("orderId",refundOrder.getId());
    		redirectAttributes.addFlashAttribute("order",refundOrder);
    		redirectAttributes.addFlashAttribute("originalOrderId",orderId);
    		rv = new ModelAndView("redirect:/register/refundconfirmation");
    		model.addAttribute("order",refundOrder);
    		model.addAttribute("orderId",refundOrder.getId());
    		model.addAttribute("originalOrderId",orderId);
    		
    	}
    
    	
    	if (errorMessage != null || !errorMessage.isEmpty())
    		model.addAttribute("errorMessage",errorMessage);

		
    	return rv;
    }
    
    
}
