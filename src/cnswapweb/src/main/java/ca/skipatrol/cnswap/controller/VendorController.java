package ca.skipatrol.cnswap.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendortype;
import ca.skipatrol.cnswap.service.VendortypeService;
import ca.skipatrol.cnswap.util.CNSwapLogger;

@RestController
public class VendorController {

	Logger LOGGER = LoggerFactory.getLogger(VendorController.class);
	
	@Autowired
	private final VendortypeService vendortypeService;
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public VendorController(VendortypeService vendortypeService) {
		this.vendortypeService = vendortypeService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/manage/addVendor").setViewName("manage/addVendor");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

    @GetMapping("/manage/addVendor")
    public ModelAndView addVendor(Model model,
    		@RequestParam(required=false) String vtid) {
    	final String METHODNAME = "addVendor"; 
    	CNSwapLogger.logEntry(LOGGER,METHODNAME,vtid);
    	
		addVendorAttributeParam(model);
		if (vtid != null) {
			CNSwapLogger.trace(LOGGER,"Workaround for vtid selection");
			//Workaround for vtid selection
			model.addAttribute("selected", vtid);
		}
	    
		CNSwapLogger.logExit(LOGGER,METHODNAME,vtid);
    	return new ModelAndView("manage/addVendor");
    }
    
    
    public Vendor getVendor(Integer vendorId) {
    	Optional<Vendor> vendorOptional = vendorRepo.findById(vendorId);
    	
    	if (vendorOptional.isEmpty()) {
    		return null;
    	}
    	return vendorOptional.get();
    }
    
    
    @GetMapping("/manage/editVendor")
    public ModelAndView editVendorGet(Model model,
    		@RequestParam(required = true) Integer vid,
    		@RequestParam(required = true) String vtid
    		, RedirectAttributes redirectAttributes) {    	
    	final String METHODNAME = "editVendorGet"; 
    	CNSwapLogger.logEntry(LOGGER,METHODNAME,vtid);
    	
    	Vendor vendor = getVendor(vid);
    	
    	if (vtid != null) {
    		CNSwapLogger.trace(LOGGER,"Workaround for vtid selection");
			//Workaround for vtid selection
			model.addAttribute("selected", vtid);
		}
	    
    	
    	if (vendor == null) {	
    		String errorMessage = String.format(Locale.US, "Unable to find Vendor. [VendorId=%s]",vid);
    		String uiMessage = String.format(Locale.US, "Perhaps you want to create a new vendor?");
    		model.addAttribute("errorMessage", errorMessage);
    		model.addAttribute("message", uiMessage);
    		addVendorAttributeParam(model);
    		return new ModelAndView("manage/addVendor");
    	}
    	
		addVendorAttributeParam(model);
	    model.addAttribute("vendorInfo",vendor);
		
    	return new ModelAndView("manage/addVendor");
    }
    
    
    
    @PostMapping("/manage/addVendor")
    public ModelAndView addVendorSubmit(Model model, 
    		@RequestParam (required = false) String vendorType, // this is a workaround for the selection box 
    		@ModelAttribute Vendor vendorInfo,
    		RedirectAttributes redirectAttributes) {

    	boolean isNewVendor = false;
    	if (vendorType.equalsIgnoreCase("-1")) {
    		redirectAttributes.addFlashAttribute("errorMessage", "No Vendor type selected");
    		return new ModelAndView("redirect:/manage/addVendor"); 
    	}
    	
    	if (vendorInfo.getId() == null) {
    		vendorInfo.setId(Long.valueOf(vendorRepo.count()+1).intValue());
    		isNewVendor=true;
    	}
    	vendorInfo.setVendortype(vendortypeService.getVendorTypeById(vendorType));
    	Vendor vendor = vendorRepo.save(vendorInfo);

    	
    	if (isNewVendor) {
    		model.addAttribute("message", "New vendor added. [" + vendor.getId() + "] " + vendor.getFirstname() +" "+ vendor.getLastname());
    	}
    	else {
    		model.addAttribute("message", "Updated vendor: [" + vendor.getId() + "] " + vendor.getFirstname() +" "+ vendor.getLastname());
    	}
    			
    	
    	addVendorAttributeParam(model);
    	
    	// Not sure why we need this
    	List<Vendortype> vendortypeList = vendortypeService.getVendortypes();    	
	    model.addAttribute("vendortypeList", vendortypeList);
	    
	    return new ModelAndView("manage/addVendor"); 
    }
    
    @PostMapping("/manage/editVendor")
    public ModelAndView editVendorPost(Model model, 
    		@RequestParam(required = false) Integer vendorId
    		, RedirectAttributes redirectAttributes) {
    	
    	Optional<Vendor> vendorOptional = vendorRepo.findById(vendorId);
    	
    	if (vendorOptional.isEmpty()) {
    		String errorMessage = String.format(Locale.US, "Unable to find Vendor. [VendorId=%s]",vendorId);
    		redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
    		return new ModelAndView("/manage/editVendor");
    	}
    	
//    	vendor = vendorOptional.get();
//    	
//    	vendorRepo.save(vendorInfo);
//    	
//    	model.addAttribute("message", "Vendor edited. [" + vendor.getId() + "] " + vendor.getFirstname() +" "+ vendor.getLastname());
//    			
    	
    	addVendorAttributeParam(model);
    	
    	// Not sure why we need this
    	List<Vendortype> vendortypeList = vendortypeService.getVendortypes();    	
	    model.addAttribute("vendortypeList", vendortypeList);
	    
    	return new ModelAndView("/manage/editVendor"); 
    }
    
    public void addVendorAttributeParam(Model model) {
    	
    	// To render the HTMLOptions element with vendorTypes when adding a Vendor
    	List<Vendortype> vTypeNames = vendortypeService.getVendortypes();
		model.addAttribute("vendorTypes", vTypeNames);
		
		 // To populate the HTMLForm to create a new Vendor 
	    model.addAttribute("vendorInfo", new Vendor());
	    model.addAttribute("provinces", new String[]{"NL","PE","NS","NB","QC","ON","MB","SK","AB","BC","YT","NT","NU"});
	    
	    
    	
    }
    
    
}
