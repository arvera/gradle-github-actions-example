package ca.skipatrol.cnswap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.service.TransactionService;
import ca.skipatrol.cnswap.util.CNSwapUtil;
import ca.skipatrol.cnswap.util.InventoryStatus;
import ca.skipatrol.cnswap.util.OrderStatus;

@RestController
@SessionAttributes("order")
public class NewTransaction {

	
	@Autowired
	private final TransactionService transactionService;
	
	@Autowired
	UsersRepository userRepo;
	
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public NewTransaction(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/newtransaction").setViewName("register/newtransaction");
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
    	order.setStatus(OrderStatus.PENDING.toString());
    	order = transactionService.save(order);    	
	    return order;
	}
	
	

    @GetMapping("/register/newtransaction")
    public ModelAndView newTransaction(Model model,
    		@RequestParam(required = false) String barcode,
    		@RequestParam(required = false) String action,
    		@RequestParam(required = false) String orderId,
    		@RequestParam(required = false) String orderItemId,
    		WebRequest request,
    		@ModelAttribute("order") Order order) {    
		
    	ModelAndView rv= new ModelAndView("register/newtransaction");
    	
    	if (order != null) {
    		order = transactionService.refresh(order);
    	}
    	// If the orderStatus is COMPLETED, then create a new order
    	if(CNSwapUtil.isOrderStatus(OrderStatus.COMPLETED, order)) {
    		order = order();
    	}
    		
    	// ACTION=remove
    	if (action!=null && action.equalsIgnoreCase("remove")) {
    		transactionService.removeOrderItem(order.getId(),orderItemId);
    	}

    	// ACTION=cancel
    	if (action!=null && (action.equalsIgnoreCase("cancel") || action.equalsIgnoreCase("cancelredirect")) ) {
    		if (transactionService.cancelOrder(order.getId())){
    			if (action.equalsIgnoreCase("cancelredirect")) {
    				request.removeAttribute("order", WebRequest.SCOPE_SESSION);
    				rv = new ModelAndView("/register");
    			}
    			else {
    				order = order();
    			}
    		}
    		
    	}    	
    	
    	// ACTION=<empty>, default action = AddOrderItem
    	if (barcode != null && !barcode.isEmpty()) { 
    		boolean itemAdded = transactionService.addOrderItems(order.getId(),barcode);
    		if (!itemAdded) {
    			String status = transactionService.getBarcodeStatus(barcode);
    			Integer inHandInv = transactionService.getBarcodeQuantity(barcode);
    			model.addAttribute("errorMessage", "Item not Found or out of stock.[barcode="+barcode+","+ status+",Q="+inHandInv+"]" );
    		}
    	}
    	
    	order = transactionService.refresh(order);
    	model.addAttribute("order", order);
		
    	return rv;
    }
    
    
    
    
}
