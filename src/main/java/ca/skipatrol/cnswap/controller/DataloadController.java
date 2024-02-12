package ca.skipatrol.cnswap.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.restservice.bean.ui.DataloadBean;
import ca.skipatrol.cnswap.service.CSVFileService;

@RestController
public class DataloadController {

	
	@Autowired
	private final CSVFileService csvFileService;
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public DataloadController(CSVFileService csvFileService) {
		this.csvFileService = csvFileService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/manage/dataloadAction").setViewName("manage/dataloadAction");
		registry.addViewController("/manage/dataloadResults").setViewName("manage/dataloadResults");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    
    
    @GetMapping("/manage/dataloadAction")
    public ModelAndView dataloadActionGet(Model model,@ModelAttribute DataloadBean dataloadBean, RedirectAttributes redirectAttributes) {
    	String redirectRoute = "redirect:/manage/dataloadResults";
    	System.out.println(this.getClass().getCanonicalName()+"HTTP GET");
    	return new ModelAndView(redirectRoute); 
    }
    
    
    @PostMapping("/manage/dataloadAction")
    public ModelAndView listVendorsByVendorType(Model model,@ModelAttribute DataloadBean dataloadBean, @Validated String vendortypeInfo,RedirectAttributes redirectAttributes) {

    	StringBuffer uiErrorMessage= new StringBuffer();
    	StringBuffer uiSuccessMessage= new StringBuffer();
    	String redirectRoute = "redirect:/manage/dataloadResults";
//    	System.out.println(vendortypeInfo);
//    	System.out.println(dataloadBean);

    	// *********************
    	// **** Dataload process
    	// *********************
    	// ** check that a vendor exists with that ID, otherwise fail
    	Optional<Vendor> vendors = vendorRepo.findById(Integer.parseInt(dataloadBean.vendorId));
    	if (vendors.isEmpty()) {
    		uiErrorMessage.append("Application error, unable to find vendorId="+dataloadBean.vendorId);
    		redirectRoute = "redirect:/manage/uploadForm";
    		
    	}
    	Vendor vendor = vendors.get();
    	redirectAttributes.addFlashAttribute("vendor",vendor);
    	redirectAttributes.addFlashAttribute("vendortype",vendor.getVendortype());
    	
    	// ** 1. DO the dataload of the entries
    	List importedItems = csvFileService.processCsvFile(dataloadBean.getFilename(),vendor);
	    
    	if (importedItems.size()>0)
    		uiSuccessMessage.append(String.format("Succesfully imported: %s items",importedItems.size()));
    	
    	redirectAttributes.addFlashAttribute("importedItems",importedItems);
    	redirectAttributes.addFlashAttribute("message",uiSuccessMessage);
    	
    	
    	if (!uiErrorMessage.isEmpty()) 	
			redirectAttributes.addFlashAttribute("errorMessage",uiErrorMessage);
    	

    	return new ModelAndView(redirectRoute); 
    }
    
    public static <T> T getFirstElementOf(Iterable<T> iterable) {
        return iterable.iterator().next();
    }
    
    
}
