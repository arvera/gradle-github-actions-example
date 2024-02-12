package ca.skipatrol.cnswap.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.CatentryTypeRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendortype;
import ca.skipatrol.cnswap.jpa.entity.VendortypeRepository;
import ca.skipatrol.cnswap.service.ItemService;
import ca.skipatrol.cnswap.util.InventoryStatus;
import jakarta.servlet.http.HttpSession;

@RestController
public class ItemController {

	
	@Autowired
	private final ItemService itemService;
	
	@Autowired
	CatentryTypeRepository catentryTypeRepo;
	
	@Autowired
	VendortypeRepository vendortypeRepo;
	
	@Autowired
	VendorRepository vendorRepo;
	
	// Constructor for populate the service
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/register/lookup").setViewName("register/lookup");
		registry.addViewController("/manage/checkinVendor").setViewName("manage/checkinVendor");
		registry.addViewController("/manage/checkoutVendor").setViewName("manage/checkoutVendor");
		registry.addViewController("/manage/editItem").setViewName("manage/editItem");
		registry.addViewController("/manage/addItem").setViewName("manage/addItem");
		registry.addViewController("/manage/addItem-step1").setViewName("manage/addItem-step1");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }

   
    

  	private Optional<Catentry> getCatentry(String barcode){
  		if (barcode == null || barcode.isEmpty()) {	    	
	    	return Optional.empty();
  		}
  		Catentry catEntry = itemService.getBarcode(barcode);
  		return Optional.of(catEntry);
	    	
  	}
  	
  	
//  	private Optional<Catentry> lookUpCatentryForEdit(Model model, String barcode, String returnView){
//  		if (barcode == null || barcode.isEmpty()) {	    	
//	    	return Optional.empty();
//  		}
//  		Catentry catEntry = itemService.getBarcode(barcode);
//  		return Optional.of(catEntry);
//	    	
//  	}
  	
  	
  	private ModelAndView lookupCatentryForEdit(Model model,String barcode,  String returnView) {
     	
     	Optional<Catentry> catEntryOpt = getCatentry(barcode);
 	    	
     	if (catEntryOpt.isEmpty()) {	
     		String errorMessage = String.format(Locale.US, "Unable to find Item. [barcode=%s]",barcode);
     		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
     		model.addAttribute("errorMessage", errorMessage);
     		//model.addAttribute("message", uiMessage);
     	
     		return new ModelAndView(returnView);
     	}
     	
     	model.addAttribute("itemInfo",catEntryOpt.get());
     	
     	return new ModelAndView(returnView);
     	
     }
  	
  	
    private ModelAndView lookupCatentryForCheckin(Model model,String barcode,  String returnView) {
    	
    	Optional<Catentry> catEntryOpt = getCatentry(barcode);
	    	
    	if (catEntryOpt.isEmpty()) {	
    		String errorMessage = String.format(Locale.US, "Unable to find Item. [barcode=%s]",barcode);
    		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
    		model.addAttribute("errorMessage", errorMessage);
    		//model.addAttribute("message", uiMessage);
    	
    		return new ModelAndView(returnView);
    	}
    	
    	if (!InventoryStatus.isPendingChecking(catEntryOpt.get().getInventory().getStatus())) {
    		String errorMessage = String.format(Locale.US, "Item is in the incorrect Status. [barcode=%s,status=%s]",barcode,catEntryOpt.get().getInventory().getStatus());
    		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
    		model.addAttribute("errorMessage", errorMessage);
    		//model.addAttribute("message", uiMessage);
    	
    		return new ModelAndView(returnView);
    	}
    	 model.addAttribute("itemInfo",catEntryOpt.get());
    	
    	
    	return new ModelAndView(returnView);
    	
    }
    
 private ModelAndView lookupCatentryForCheckout(Model model,String barcode,  String returnView) {
    	
    	Optional<Catentry> catEntryOpt = getCatentry(barcode);
	    	
    	if (catEntryOpt.isEmpty()) {	
    		String errorMessage = String.format(Locale.US, "Unable to find Item. [barcode=%s]",barcode);
    		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
    		model.addAttribute("errorMessage", errorMessage);
    		//model.addAttribute("message", uiMessage);
    	
    		return new ModelAndView(returnView);
    	}
    	
    	if (!InventoryStatus.isAvailable(catEntryOpt.get().getInventory().getStatus())) {
    		String errorMessage = String.format(Locale.US, "Item is in the incorrect Status. [barcode=%s,status=%s]",barcode,catEntryOpt.get().getInventory().getStatus());
    		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
    		model.addAttribute("errorMessage", errorMessage);
    		//model.addAttribute("message", uiMessage);
    	
    		return new ModelAndView(returnView);
    	}
    	 model.addAttribute("itemInfo",catEntryOpt.get());
    	
    	
    	return new ModelAndView(returnView);
    	
    }
    
    
    
    @GetMapping("/manage/editItem")
    public ModelAndView editItem(Model model,@RequestParam(required = false) String barcode) {    	    	

    	ModelAndView rModelView = new ModelAndView("manage/editItem");
    	
    	if (barcode != null && !barcode.isEmpty())
    		rModelView = lookupCatentryForEdit(model, barcode, "manage/editItem");

    	
    	model.addAttribute("catentryTypesList",itemService.getAllTypes());
    	model.addAttribute("invStatusList",itemService.getAllInvStatus());
    	
    	return rModelView;
    }

    @GetMapping("/manage/addItem")
    public ModelAndView addItem(Model model,
    		@ModelAttribute Catentry itemInfo,
    		@RequestParam (required=false) String vtid,
    		@RequestParam (required=false) String vid,
    		@RequestParam (required=false) String action) {    	    	

    	ModelAndView rModelView = new ModelAndView("manage/addItem");

    	if (itemInfo.getId() == null && !action.equalsIgnoreCase("selected")) {
    		rModelView = new ModelAndView("/manage/addItem-Step1");
    		return rModelView;
    	}
    	
    	model.addAttribute("catentryTypesList",itemService.getAllTypes());
    	model.addAttribute("invStatusList",itemService.getAllInvStatus());
    	model.addAttribute("itemInfo",itemInfo);
    	Optional<Vendor> vendorOpt = vendorRepo.findById(Integer.parseInt(vid));
    	model.addAttribute("vendor",vendorOpt.get());
    	
    	
    	if (action.equalsIgnoreCase("selected")){
    		model.addAttribute("vtId",vtid);
    		model.addAttribute("vId",vid);
    		model.addAttribute("action",action);
    	}
    	
    	return rModelView;
    }
    
    @GetMapping("/manage/addItem-step1")
    public ModelAndView addItemStep1(Model model,@ModelAttribute Catentry itemInfo,@RequestParam (required=false) String vtId) {    	    	

    	ModelAndView rModelView = new ModelAndView("manage/addItem-step1");

    	
    	// To render the HTMLTable element with vendorTypes, for selection
    	List<Vendortype> vendorTypes = vendortypeRepo.findAll();
		model.addAttribute("vendorTypes", vendorTypes);
		List<Vendor> vendors = new ArrayList(0); //new Vendor[0]
	    model.addAttribute("vendors", vendors);
		

		if (vtId!= null && !vtId.isEmpty()) {
			model.addAttribute("vtId", vtId);
				    	
			
	    	// To populate the HTMLTable
	    	vendors = vendorRepo.findAllByVendortype(vendorTypes.get(Integer.parseInt(vtId)-1));    	
		    model.addAttribute("vendors", vendors);
		}
	    
		 
		model.addAttribute("action","select");
    	
    	
    	
    	if (itemInfo == null) {
    		rModelView = new ModelAndView("manage/selectVendor");
    		return rModelView;
    	}
    	
    	model.addAttribute("catentryTypesList",itemService.getAllTypes());
    	model.addAttribute("invStatusList",itemService.getAllInvStatus());
    	model.addAttribute("itemInfo",itemInfo);
    	
    	return rModelView;
    }

    
    @GetMapping("/manage/checkinVendor")
    public ModelAndView checkinVendor(Model model,@RequestParam(required = false) String barcode) {    	    	

    	ModelAndView rModelView = new ModelAndView("manage/checkinVendor");
    	
    	if (barcode != null && !barcode.isEmpty())
    		rModelView = lookupCatentryForCheckin(model,barcode, "manage/checkinVendor");
    	
    	model.addAttribute("catentryTypesList",itemService.getAllTypes());
    	model.addAttribute("invStatusList",itemService.getAllInvStatus());
    	
    	
    	return rModelView;
    }
    
    @GetMapping("/manage/checkoutVendor")
    public ModelAndView checkoutVendor(Model model,@RequestParam(required = false) String barcode) {    	    	
    	ModelAndView rModelView = new ModelAndView("manage/checkoutVendor");
    	
    	if (barcode != null && !barcode.isEmpty()) 
    		rModelView = lookupCatentryForCheckout(model,barcode, "manage/checkoutVendor");
    	
    	model.addAttribute("catentryTypesList",itemService.getAllTypes());
    	//model.addAttribute("invStatusList",itemService.getAllInvStatus());
    	InventoryStatus [] invStatusList = new InventoryStatus[] {InventoryStatus.AVAILABLE,InventoryStatus.CHECKOUT}; 
    	model.addAttribute("invStatusList",invStatusList);
    	
    	return rModelView;
    }
    
    @GetMapping("/register/lookup")
    public ModelAndView lookup(Model model,	@RequestParam(required = false) String barcode) {    	
		    	
    	
    	if (barcode != null && !barcode.isEmpty()) {
	    	Catentry catEntry =itemService.getBarcode(barcode);
	    	
	    	if (catEntry == null) {	
	    		String errorMessage = String.format(Locale.US, "Unable to find Item. [barcode=%s]",barcode);
	    		//String uiMessage = String.format(Locale.US, "You might be looking for: %s",);
	    		model.addAttribute("errorMessage", errorMessage);
	    		//model.addAttribute("message", uiMessage);
	    	
	    		return new ModelAndView("register/lookup");
	    	}
	    	 model.addAttribute("itemInfo",catEntry);
    	}
    	
		
    	return new ModelAndView("register/lookup");
    }    

    //*********** POST
    
    @PostMapping("/manage/checkinVendor")
    public ModelAndView checkinVendorPost(Model model,@ModelAttribute Catentry item, HttpSession session) {    	    	

    	item = itemService.mergeAndOverwrite(item);
    	
    	if (model.containsAttribute("item")) {
    		model.addAttribute("item", null);
    	}
    	
    	List<Catentry> checkinList = (List<Catentry>)session.getAttribute("checkinList");
    	if (checkinList==null) {
    		checkinList = new ArrayList<Catentry>();
    	}
    	checkinList.add(item);
    	
    	model.addAttribute("checkinList", checkinList);
    	
    	
    	
    	return new ModelAndView("manage/checkinVendor");
    }
    
    @PostMapping("/manage/checkoutVendor")
    public ModelAndView checkoutVendorPost(Model model,@ModelAttribute Catentry item, HttpSession session) {    	    	

    	item = itemService.mergeAndOverwrite(item);
    	
    	if (model.containsAttribute("item")) {
    		model.addAttribute("item", null);
    	}
    	
    	List<Catentry> checkoutList = (List<Catentry>)session.getAttribute("checkoutList");
    	if (checkoutList==null) {
    		checkoutList = new ArrayList<Catentry>();
    	}
    	checkoutList.add(item);
    	
    	model.addAttribute("checkoutList", checkoutList);
    	
    	
    	
    	return new ModelAndView("manage/checkoutVendor");
    }
    
    
    @PostMapping("/manage/editItem")
    public ModelAndView editItemPost(Model model,@ModelAttribute Catentry item, HttpSession session) {    	    	

    	item = itemService.mergeAndOverwrite(item);
    	
    	if (model.containsAttribute("item")) {
    		model.addAttribute("item", null);
    	}
    	
    	List<Catentry> modifiedList = (List<Catentry>)session.getAttribute("modifiedList");
    	if (modifiedList==null) {
    		modifiedList = new ArrayList<Catentry>();
    	}
    	modifiedList.add(item);
    	
    	model.addAttribute("modifiedList", modifiedList);
    	
    	
    	return new ModelAndView("manage/editItem");
    }
    
 
    @PostMapping("/manage/addItem")
    public RedirectView addItemPost(ModelMap model,@ModelAttribute Catentry item,@RequestParam String vid, RedirectAttributes attributes, HttpSession session) {    	    	

    	if (item.getId() == null || item.getId().isBlank() || item.getId().isEmpty()) {
    		String errorMessage = String.format(Locale.US, "Invalid barcode. Please enter a valid barcode");
    		model.addAttribute("errorMessage", errorMessage);

        	return new RedirectView("manage/editItem");
    	}
    	
    	if (itemService.getBarcode(item.getId()) != null){
    		String errorMessage = String.format(Locale.US, "An item with that barcode already exists. Use a different barcode");
    		attributes.addFlashAttribute("errorMessage", errorMessage);
    		//attributes.addFlashAttribute("vid", vid);
    		//attributes.addFlashAttribute("action", "selected");
        	return new RedirectView("manage/addItem?vid="+vid+"&action=selected");
    		
    	}
    	
    	Optional<Vendor> vendorOpt = vendorRepo.findById(Integer.parseInt(vid));
    	item.setVendor(vendorOpt.get());
    	item.getInventory().setId( Integer.parseInt(item.getId().replaceAll("\\D", "")) );
    	item.getInventory().setCatentry(item);
    	item.setType(catentryTypeRepo.findById(item.getType().getCatentryTypeId()).get());
    	item = itemService.save(item);    	
    	
    	if (item != null) {
    		String message = String.format(Locale.US, "Item [%s] has been added",item.getId());
    		attributes.addFlashAttribute("message", message);

        	return new RedirectView("manage/addItem");
    		
    	}
    	
    	
    	List<Catentry> modifiedList = (List<Catentry>)session.getAttribute("modifiedList");
    	if (modifiedList==null) {
    		modifiedList = new ArrayList<Catentry>();
    	}
    	modifiedList.add(item);
    	
    	attributes.addFlashAttribute("modifiedList", modifiedList);
    
    	
    	return new RedirectView("manage/addItem");
    }

    
  
    
}
