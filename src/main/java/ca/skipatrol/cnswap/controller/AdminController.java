package ca.skipatrol.cnswap.controller;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import ca.skipatrol.cnswap.jpa.entity.TaxEntry;
import ca.skipatrol.cnswap.jpa.entity.Vendortype;
import ca.skipatrol.cnswap.service.VendortypeService;
import ca.skipatrol.cnswap.util.CNSwapLogger;

@RestController
public class AdminController {

	@Autowired
	private final VendortypeService vendortypeService;
	
	Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
	
	// Constructor for populate the service
	public AdminController(VendortypeService vendortypeService) {
		this.vendortypeService = vendortypeService;
	}
	
    public void addViewControllers(ViewControllerRegistry registry) {
	 
		// 2nd level views: Admin
    	registry.addViewController("/admin/addTaxEntry").setViewName("admin/addTaxEntry");
    	registry.addViewController("/admin/addVendorType").setViewName("admin/addVendorType");
		
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
    
    
    // ******************** 
    // ********       GET  
    // ********************
    
    @GetMapping("/admin/addVendorType")
    public ModelAndView addVendorType(Model model) {
    	final String METHODNAME = "addVendorType";
    	CNSwapLogger.logEntry(LOGGER, METHODNAME, model);
    	// To populate the HTMLTable
    	List<Vendortype> vendortypeList = vendortypeService.getVendortypes();
	    model.addAttribute("vendortypeList", vendortypeList);
	    for (Vendortype vt : vendortypeList) {
	    	CNSwapLogger.trace(LOGGER, "vt="+vt);
	    	
	    	Iterator<TaxEntry> assignedTax = vt.getTaxEntry().iterator();
	    	
	    	CNSwapLogger.trace(LOGGER, ""+vt.getTaxEntry().size());
	    	
	    	while(assignedTax.hasNext()){
	    		CNSwapLogger.trace(LOGGER, assignedTax.next().getName()+",");
	    	}
	    	
	    }
	    
	    
		// To display all TAXES 
    	List<TaxEntry> availableTaxList = vendortypeService.getTaxEntries();
	    model.addAttribute("availableTaxList", availableTaxList);
	    
	    // To populate the HTMLForm to create a new Vendortype 
	    model.addAttribute("vendortypeInfo", new Vendortype());
	    model.addAttribute("taxentryInfo", new TaxEntry());
	    
	    
	    
    	return new ModelAndView("admin/addVendorType");
    }
    
    @GetMapping("/admin/addTaxEntry")
    public ModelAndView addTaxEntry(Model model) {
    	  
    	// To populate the HTMLTable
    	List<TaxEntry> availableTaxList = vendortypeService.getTaxEntries();
    	model.addAttribute("taxEntryList", availableTaxList);
	    
	    // To populate the HTMLForm to create a new Vendortype 
	    model.addAttribute("taxEntryInfo", new TaxEntry());
	    
	    
    	return new ModelAndView("admin/addTaxEntry");
    }
    
    // ******************** 
    // ********       POST 
    // ********************
    
    @PostMapping("/admin/addVendorType")
    public ModelAndView addVendorTypeSubmit(Model model, @ModelAttribute Vendortype vendortypeInfo) {
    	
    	Vendortype vendortype = vendortypeService.createVendortype(vendortypeInfo);
    	
    	
    	// To populate the HTMLTable
    	List<Vendortype> vendortypeList = vendortypeService.getVendortypes();    	
	    model.addAttribute("vendortypeList", vendortypeList);
	    
	    
	    
    	return new ModelAndView("redirect:addVendorType"); 
    }
    
    @PostMapping("/admin/addTaxEntry")
    public ModelAndView addTaxEntrySubmit(Model model, @ModelAttribute TaxEntry taxentryInfo) {
    	
    	TaxEntry taxEntry = vendortypeService.createTaxEntry(taxentryInfo);
    	
    	
    	// To populate the HTMLTable
    	List<TaxEntry> taxEntryList = vendortypeService.getTaxEntries();    	
    	model.addAttribute("taxEntryList", taxEntryList);
	    
    	return new ModelAndView("redirect:addTaxEntry"); 
    }
    
    @PostMapping("/admin/taxEntryDelete")
    public ModelAndView deleteTaxEntry(Model model, 
    		@RequestParam(required = true) Integer taxEntryId) {
    	
    	vendortypeService.deleteTaxEntry(taxEntryId);
    	
    	
    	// To populate the HTMLTable
    	List<TaxEntry> taxEntryList = vendortypeService.getTaxEntries();    	
    	model.addAttribute("taxEntryList", taxEntryList);
	    
    	return new ModelAndView("redirect:addTaxEntry"); 
    }
    
    
    
}
