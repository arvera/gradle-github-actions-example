package ca.skipatrol.cnswap.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.CatentryJPARepository;
import ca.skipatrol.cnswap.jpa.entity.Order;
import ca.skipatrol.cnswap.jpa.entity.OrdersJPARepository;
import ca.skipatrol.cnswap.restservice.bean.jasperreports.JRPublicVendor;
import ca.skipatrol.cnswap.service.VendorService;
import ca.skipatrol.cnswap.util.CNSwapUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
public class ReportControler {

	
	@Autowired
	private final VendorService vendorService;
	
	@Autowired
	OrdersJPARepository orderRepo;
	
	@Autowired
	CatentryJPARepository catEntryJPARepo;
	
	// Constructor for populate the service
	public ReportControler(VendorService vendorService) {
		this.vendorService = vendorService;
	}
	
	// Register the views
    public void addViewControllers(ViewControllerRegistry registry) {
		// 2nd level views: Manage
		registry.addViewController("/manage/publicReport").setViewName("manage/publicReport");
		registry.addViewController("/manage/reports/transctionReport").setViewName("manage/reports/transactionReport");
		registry.addViewController("/manage/reports/itemReport").setViewName("manage/reports/itemReport");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
    }
    

//    @GetMapping("/manage/publicReport")
//    public ModelAndView publicReport(Model model) throws FileNotFoundException, JRException {    	
//		    	
//		
//		List<JRPublicVendor> publicVendorList = vendorService.getPublicVendorItemsList();
//		JRBeanCollectionDataSource jrDatasource = new JRBeanCollectionDataSource(publicVendorList);
//		
//		JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/static/reports/PublicVendorList.jrxml"));
//		HashMap<String,Object> map = new HashMap<String,Object>();
//		JasperPrint jasperReport = JasperFillManager.fillReport(compileReport, map,jrDatasource);
//		//JasperExportManager.exportReportToPdfFile(jasperReport,"publicVendorRrport.pdf");
//		byte data[] = JasperExportManager.exportReportToPdf(jasperReport);
//		//JasperExportManager.exportReportToHtmlFile(jasperReport,"src/main/resources/static/reports/PublicVendorList.html");
//		
//	    
//		
//    	return new ModelAndView("redirect:/resources/reports/PublicVendorList.html");
//    }
//    
    @GetMapping("/manage/publicReport")
    public ResponseEntity<byte[]> publicReport() throws FileNotFoundException, JRException {    	
		    	
		
    	List<JRPublicVendor> publicVendorList = vendorService.getPublicVendorItemsList();
    	
    	if (publicVendorList.isEmpty()) {
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Location", "/manage?errorMessage=No public vendor found");    
    		return new ResponseEntity<byte[]>(headers,HttpStatus.FOUND);
    		
    	}
		JRBeanCollectionDataSource jrDatasource = new JRBeanCollectionDataSource(publicVendorList);
		
		JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream("src/main/resources/static/reports/PublicVendorList.jrxml"));
		HashMap<String,Object> map = new HashMap<String,Object>();
		JasperPrint jasperReport = JasperFillManager.fillReport(compileReport, map,jrDatasource);
		//JasperExportManager.exportReportToPdfFile(jasperReport,"publicVendorRrport.pdf");
		byte data[] = JasperExportManager.exportReportToPdf(jasperReport);
		//JasperExportManager.exportReportToHtmlFile(jasperReport,"src/main/resources/static/reports/PublicVendorList.html");
		
	    
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;infile=publicreport.pdf");
		
		
		
    	return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }
    
    @GetMapping("/manage/reports/transactionReport")
    public ModelAndView transactionReport(Model model, 
    		  @RequestParam(required = false) String keyword,
    		  @RequestParam(defaultValue = "1") int page,
    	      @RequestParam(defaultValue = "25") int size,
    	      @RequestParam(defaultValue = "id,asc") String[] sort) {  
    	
    	try {
	    	List<Order> allOrders = new ArrayList<Order>();
	    	
	    	String sortField = sort[0];
	    	String sortDirection = sort[1];
	    	
	    	Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
	    	org.springframework.data.domain.Sort.Order order = new org.springframework.data.domain.Sort.Order(direction, sortField);
			
	    	Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));
	
	        Page<Order> pageOrders;
       
	        if (keyword == null) {
	        	pageOrders = orderRepo.findAll(pageable);
	        } 
	        else {
	        	pageOrders = orderRepo.findByTransUsernameUsernameIgnoreCase(keyword, pageable);
	            model.addAttribute("keyword", keyword);
	        }
	
	        allOrders = pageOrders.getContent();
	          
	        model.addAttribute("orders", allOrders);
	        model.addAttribute("currentPage", pageOrders.getNumber() + 1);
	        model.addAttribute("totalItems", pageOrders.getTotalElements());
	        model.addAttribute("totalPages", pageOrders.getTotalPages());
	        model.addAttribute("pageSize", size);
	        model.addAttribute("sortField", sortField);
	        model.addAttribute("sortDirection", sortDirection);
	        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
	       
        }
    	catch (Exception e) {
    		model.addAttribute("errorMessage", e.getMessage());
        }
    	
    	
		
    	return new ModelAndView("manage/reports/transactionReport");
    }
    
    
    @GetMapping("/manage/reports/itemReport")
    public ModelAndView itemReport(Model model, 
    		  @RequestParam(required = false) String keyword,
    		  @RequestParam(defaultValue = "1") int page,
    	      @RequestParam(defaultValue = "25") int size,
    	      @RequestParam(defaultValue = "id,asc") String[] sort) {  
    	
    	try {
	    	List<Catentry> allCatentries = new ArrayList<Catentry>();
	    	
	    	String sortField = sort[0];
	    	String sortDirection = sort[1];
	    	
	    	Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
	    	org.springframework.data.domain.Sort.Order order = new org.springframework.data.domain.Sort.Order(direction, sortField);
			
	    	Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));
	
	        Page<Catentry> pageCatentry;
       
	        if (keyword == null) {
	        	pageCatentry = catEntryJPARepo.findAll(pageable);
	        } 
	        else {
	        	if (CNSwapUtil.isParsableInteger(keyword)) {
	        		pageCatentry = catEntryJPARepo.findAllByVendorId(Integer.parseInt(keyword), pageable);
		            model.addAttribute("keyword", keyword);	
	        	}
	        	else {
	        		pageCatentry = null;
	        		model.addAttribute("errorMessage", "The enter keyword does not match any Vendor ID");
	        	}
	        	
	        }
	
	        allCatentries = pageCatentry.getContent();
	          
	        model.addAttribute("items", allCatentries);
	        model.addAttribute("currentPage", pageCatentry.getNumber() + 1);
	        model.addAttribute("totalItems", pageCatentry.getTotalElements());
	        model.addAttribute("totalPages", pageCatentry.getTotalPages());
	        model.addAttribute("pageSize", size);
	        model.addAttribute("sortField", sortField);
	        model.addAttribute("sortDirection", sortDirection);
	        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
	       
        }
    	catch (Exception e) {
    		model.addAttribute("errorMessage", e.getMessage());
        }
    	
    	
		
    	return new ModelAndView("manage/reports/itemReport");
    }
    
    
    
//    @GetMapping("/manage/reports/transactionReport/orders/{id}")
//    public ModelAndView transactionReport(Model model, 
//    		  @PathVariable("id") Integer orderId,
//    		  RedirectAttributes redirectAttributes) {  
//    	
//    	try {
//	    	List<Order> allOrders = new ArrayList<Order>();
//	    	
//	    	String sortField = sort[0];
//	    	String sortDirection = sort[1];
//	    	
//	    	Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//	    	org.springframework.data.domain.Sort.Order order = new org.springframework.data.domain.Sort.Order(direction, sortField);
//			
//	    	Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));
//	
//	        Page<Order> pageOrders;
//       
//	        if (keyword == null) {
//	        	pageOrders = orderRepo.findAll(pageable);
//	        } 
//	        else {
//	        	pageOrders = orderRepo.findByTransUsernameUsernameIgnoreCase(keyword, pageable);
//	            model.addAttribute("keyword", keyword);
//	        }
//	
//	        allOrders = pageOrders.getContent();
//	          
//	        model.addAttribute("orders", allOrders);
//	        model.addAttribute("currentPage", pageOrders.getNumber() + 1);
//	        model.addAttribute("totalItems", pageOrders.getTotalElements());
//	        model.addAttribute("totalPages", pageOrders.getTotalPages());
//	        model.addAttribute("pageSize", size);
//	        model.addAttribute("sortField", sortField);
//	        model.addAttribute("sortDirection", sortDirection);
//	        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
//	       
//        }
//    	catch (Exception e) {
//    		model.addAttribute("errorMessage", e.getMessage());
//        }
//    	
//    	
//		
//    	return new ModelAndView("/manage/reports/transactionReport");
//    }
//    
    
    
    
}
