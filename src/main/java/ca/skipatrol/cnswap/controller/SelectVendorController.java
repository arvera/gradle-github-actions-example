package ca.skipatrol.cnswap.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
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
import ca.skipatrol.cnswap.restservice.bean.ui.DataloadBean;
import ca.skipatrol.cnswap.service.VendortypeService;

@RestController
public class SelectVendorController {

	
	@Autowired
	private final VendortypeService vendortypeService;
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public SelectVendorController(VendortypeService vendortypeService) {
		this.vendortypeService = vendortypeService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/manage/selectVendor").setViewName("manage/selectVendor");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

    @GetMapping("/manage/selectVendor")
    public ModelAndView addVendor(Model model,
    		@RequestParam(required = false) String filename,
    		@RequestParam(required = false) String action,
    		@RequestParam(required = false) String vtId) {    	

		// To render the HTMLTable element with vendorTypes, for selection
    	List<Vendortype> vendorTypes = vendortypeService.getVendortypes();
		model.addAttribute("vendorTypes", vendorTypes);
		List<Vendor> vendors = new ArrayList(0); //new Vendor[0]
	    model.addAttribute("vendors", vendors);
		

		// Create a RequestVariable to hold the parameters and render them on the URL of the VendorType selection
		model.addAttribute("filename", filename);
		model.addAttribute("action", action);
		
		if (vtId!= null && !vtId.isEmpty()) {
			model.addAttribute("vtId", vtId);
				    	
			
	    	// To populate the HTMLTable
	    	vendors = vendorRepo.findAllByVendortype(vendorTypes.get(Integer.parseInt(vtId)-1));    	
		    model.addAttribute("vendors", vendors);
		}
		
				
		if (action == null || action.equalsIgnoreCase("")) {
			model.addAttribute("dataloadBean", new DataloadBean());
    		return new ModelAndView("manage/selectVendor");
		}
		
		if (action.equalsIgnoreCase("select")) {
			
		}
    		
		
		
        	return new ModelAndView("manage/selectVendor");
    }
    
    
    List<Vendor> toList(Optional<Vendor> optString) {
    	return optString
    			.map(Collections::singletonList)
    			.orElseGet(Collections::emptyList);
    }
     
    
    
    private Vendortype findVendortype(String vtId,List<Vendortype> vendorTypes) {
    	for (Vendortype v : vendorTypes) {
			if (v.getVendortypeId().equalsIgnoreCase(vtId)) {
				return v;
			}
		}
    	return new Vendortype();
    }
    
    
}
