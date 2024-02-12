package ca.skipatrol.cnswap.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
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
import ca.skipatrol.cnswap.service.TransactionService;
import ca.skipatrol.cnswap.util.CNSwapLogger;
import ca.skipatrol.cnswap.util.CNSwapUtil;

@RestController
public class LookupTransaction {
	Logger LOGGER = LoggerFactory.getLogger(LookupTransaction.class);
	
	@Autowired
	private final TransactionService transactionService;
	
	@Autowired
	private final AuthenticationManager authManager;

	
	// Constructor for populate the service
	public LookupTransaction(TransactionService transactionService,AuthenticationManager authManager) {
		this.transactionService = transactionService;
		this.authManager = authManager;
	}
	
	
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/orderLookup").setViewName("register/orderLookup");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

    public Order getOrder(Integer orderId) {
    	return transactionService.getOrder(Integer.valueOf(orderId));
    }
    
    


    // ****************    
    // **    GET    **
    // ****************
    

    
    @GetMapping("/register/orderLookup")
    public ModelAndView orderLookupGet(Model model,
    		@RequestParam(required = false) String orderId,
    		WebRequest request) { 
    	final String METHODNAME = "orderLookupGet";
    	CNSwapLogger.logEntry(LOGGER,METHODNAME,orderId);
    	CNSwapLogger.trace(LOGGER, "GET:/register/orderLookup");
    	
    	ModelAndView rv= new ModelAndView("register/orderLookup");
    	String errorMessage = null;
    	
    	if (orderId != null) {
    		Order order = null; 
    		if( CNSwapUtil.isParsableInteger(orderId)) {
				Integer orderIdInt = Integer.parseInt(orderId);
				order = getOrder(orderIdInt);
	    		CNSwapLogger.trace(LOGGER, "found order="+order);
    		 }
    		
    		if (order == null) {
    			errorMessage = String.format(Locale.US, "Unable to find ORDER_ID=[%s]",orderId);
    			CNSwapLogger.trace(LOGGER, errorMessage);
    			model.addAttribute("errorMessage",errorMessage);
    			CNSwapLogger.logExit(LOGGER, METHODNAME, rv);
    			return rv;
    		}
    			
    		model.addAttribute("order",order);
    	}

    	model.addAttribute("errorMessage",errorMessage);
    	
		CNSwapLogger.logExit(LOGGER, METHODNAME, rv);
    	return rv;
    }
    
    
    // ****************    
    // **    POST    **
    // ****************
    
    @PostMapping("/register/orderLookup")
    public ModelAndView orderLookupPost(Model model,
    		@RequestParam(required = true) String orderId,
    		WebRequest request,
    		RedirectAttributes redirectAttributes) {    
		
    	
    	ModelAndView rv= new ModelAndView("register/orderLookup");
    	Order displayOrder = null;
    	String errorMessage = new String();
    	
    	
		
		// If user is AUTHENTICATED and AUTHORIZED then refund the order 
		if ((errorMessage == null || errorMessage.isEmpty()) && CNSwapUtil.isParsableInteger(orderId)) {
			displayOrder = getOrder(Integer.parseInt(orderId));
		}
    	
	
    	
    	
    	if (displayOrder != null) {
    		redirectAttributes.addFlashAttribute("orderId",displayOrder.getId());
    		redirectAttributes.addFlashAttribute("order",displayOrder);
    		redirectAttributes.addFlashAttribute("originalOrderId",orderId);
    		rv = new ModelAndView("redirect:/register/orderLookup");
    		model.addAttribute("order",displayOrder);
    		model.addAttribute("orderId",displayOrder.getId());
    		model.addAttribute("originalOrderId",orderId);
    		
    	}
    
    	
    	if (errorMessage != null || !errorMessage.isEmpty())
    		model.addAttribute("errorMessage",errorMessage);

		
    	return rv;
    }
    
    
}
