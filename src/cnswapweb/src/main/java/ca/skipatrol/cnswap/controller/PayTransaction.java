package ca.skipatrol.cnswap.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.Payment;
import ca.skipatrol.cnswap.jpa.entity.PaymentRepository;
import ca.skipatrol.cnswap.jpa.entity.PaymentType;
import ca.skipatrol.cnswap.jpa.entity.PaymentTypeRepository;
import ca.skipatrol.cnswap.jpa.entity.User;
import ca.skipatrol.cnswap.jpa.entity.UsersRepository;
import ca.skipatrol.cnswap.service.TransactionService;
import ca.skipatrol.cnswap.util.OrderStatus;
import ca.skipatrol.cnswap.util.SystemMessages;

@RestController
@SessionAttributes("order")
public class PayTransaction {

	
	@Autowired
	private final TransactionService transactionService;
	
	@Autowired
	PaymentTypeRepository paymentTypeRepo;
	
	@Autowired
	UsersRepository userRepo;
	
	@Autowired
	PaymentRepository paymentRepo;
	
	// Constructor for populate the service
	public PayTransaction(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/paytransaction").setViewName("register/paytransaction");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    
    public List<PaymentType> getPaymentTypes(){
       	// To render the HTMLOptions element with vendorTypes when adding a Vendor
    	Iterable<PaymentType> paymentTypes = paymentTypeRepo.findAll();
    	List<PaymentType> paymentTypeList = new ArrayList<PaymentType>();
    	
    	paymentTypes.forEach( (paymentType) -> {
    		paymentTypeList.add(paymentType);
    	});
		return paymentTypeList;
    }
	

    @GetMapping("/register/paytransaction")
    public ModelAndView newTransaction(Model model,
    		@RequestParam(required = false) String orderId,
    		WebRequest request,
    		@ModelAttribute("order") Order order) {    
		
    	ModelAndView rv= new ModelAndView("register/paytransaction");

    	// To render the HTMLOptions element with vendorTypes when adding a Vendor
    	List<PaymentType> paymentTypes = getPaymentTypes();
		model.addAttribute("paymentTypes", paymentTypes);
		
		 // To populate the HTMLForm to create a new Payment in memory object model 
		model.addAttribute("paymentInfo", new Payment());
	    
		// Get all payments for this order
	    List<Payment> payments = paymentRepo.findByOrdersId(order);
	    BigDecimal paymentLeft = order.getTotal();
	    if (!payments.isEmpty()) {
	    	for (Payment payment:payments ) {
	    		paymentLeft = paymentLeft.subtract(payment.getTotal()).setScale(2, RoundingMode.HALF_UP);
	    	}
	    }
	    

		// Find out if the payment is completed
	    Boolean isPaymentCompleted=false;
	    if (paymentLeft.compareTo(BigDecimal.ZERO)<= 0){
	    	isPaymentCompleted = true;
	    	
	    }
	    
	    model.addAttribute("isPaymentCompleted",isPaymentCompleted);
	    model.addAttribute("payments",payments);
	    model.addAttribute("paymentTotalLeft",paymentLeft);
		
    	return rv;
    }
    
    @PostMapping("/register/paytransaction")
    public ModelAndView newTransaction(Model model, 
    		@ModelAttribute Payment paymentInfo,
    		RedirectAttributes redirectAttributes) {
    	ModelAndView rv = new ModelAndView("redirect:/register/paytransaction");
    	
    	String errorMessage = validate(paymentInfo);
    	if (!errorMessage.isEmpty()) {
    		redirectAttributes.addFlashAttribute("errorMessage",errorMessage);
    		return rv;
    	}
    	
    	Order order = (Order)model.getAttribute("order");
    	if (order.getTotal().compareTo(paymentInfo.getTotal()) > 0) {
    		model.addAttribute("message", "payment is incomplete, please add another payment to complete the total");
    	}

    	// check if it is CASH payment and override the value of AUTH to be the user logged in
    	if (paymentInfo.getPaymentType().getName().equalsIgnoreCase("CASH")) {
    		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	User user = userRepo.findByUsername(auth.getName());
    		paymentInfo.setAuth(user.getUsername());
    	}
    	
    	paymentInfo.setOrdersId(order);
    	paymentRepo.save(paymentInfo);
    	
    	order.setStatus(OrderStatus.COMPLETED.toString());
    	transactionService.save(order);
    	
    	transactionService.processInventory(order);
    	
    	
    	    
    	return rv;  
    }
    
    private String validate(Payment payment) {
    	if (payment == null) 
    		return "Unable to find payment information. An unknown error has occured. "+SystemMessages.CNSWAP_ERR003;
    	else if (payment.getTotal() == null)
    		return "Invalid Payment total. Payment total must be a number.";
    	else if (payment.getAuth() == null || (payment.getAuth().isBlank() && !payment.getPaymentType().getName().equalsIgnoreCase("CASH")) )
    		return "No Authorization code was entered. Review the form and enter a \"Auth\" token for the payment.";
    	//no error
    	return "";
    }
}
