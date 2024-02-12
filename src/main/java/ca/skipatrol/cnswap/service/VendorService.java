package ca.skipatrol.cnswap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.skipatrol.cnswap.jpa.entity.CatentryRepository;
import ca.skipatrol.cnswap.jpa.entity.Inventory;
import ca.skipatrol.cnswap.jpa.entity.InventoryRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.jpa.entity.VendorRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendortype;
import ca.skipatrol.cnswap.jpa.entity.VendortypeRepository;
import ca.skipatrol.cnswap.restservice.bean.jasperreports.JRPublicVendor;

@Service
public class VendorService {

	@Autowired
	private final VendorRepository vendorRepo;
	
	@Autowired
	private final VendortypeRepository vendorTypeRepo;
	
	@Autowired
	private final CatentryRepository catentryRepo;
	
	@Autowired
	private final  InventoryRepository invRepo;
	
	
	

	public VendorService(VendorRepository vendorRepo, CatentryRepository catentryRepo, InventoryRepository invRepo, VendortypeRepository vendorTypeRepo) {
		this.vendorRepo = vendorRepo;		
		this.catentryRepo = catentryRepo;
		this.invRepo = invRepo;
		this.vendorTypeRepo = vendorTypeRepo;
	}

	
	public List<JRPublicVendor> getPublicVendorItemsList() {
    	List<JRPublicVendor> rv = new ArrayList<JRPublicVendor>();
    	Optional<Vendortype> publicVT = vendorTypeRepo.findById("1");
    	List<Vendor> publicVendors = vendorRepo.findAllByVendortype(publicVT.get());
    	
    	publicVendors.forEach((vendor) -> {
    		
    		
    		catentryRepo.findAllByVendorId(vendor.getId()).forEach((catentry)-> {
    			JRPublicVendor reportPublicVendor = new JRPublicVendor();
    			reportPublicVendor.setVendorId(String.valueOf(vendor.getId()));
    			reportPublicVendor.setCatentry(catentry.getId());
    			Inventory catEntryInv = invRepo.findOneByCatentry_id(catentry.getId());
    			reportPublicVendor.setStatus(catEntryInv.getStatus());
    			rv.add(reportPublicVendor);
    		});
    		
    	});
    	return rv;
    }
}
	

