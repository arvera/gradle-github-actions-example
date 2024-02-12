package ca.skipatrol.cnswap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.restservice.bean.ui.DataloadBean;
import ca.skipatrol.cnswap.service.VendortypeService;

@RestController
public class DataloadResults {

	
	@Autowired
	private final VendortypeService vendortypeService;
	

	
	// Constructor for populate the service
	public DataloadResults(VendortypeService vendortypeService) {
		this.vendortypeService = vendortypeService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
    	registry.addViewController("/manage/dataloadResults").setViewName("manage/dataloadResults");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

    @GetMapping("/manage/dataloadResults")
    public ModelAndView addVendor(@ModelAttribute DataloadBean dataloadBean) {    	

    	System.out.println(this.getClass().getCanonicalName() + ": DEBUG");
		
		
    	return new ModelAndView("manage/dataloadResults");
    }
    
    
    
}
