package ca.skipatrol.cnswap.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.skipatrol.cnswap.jpa.entity.TaxEntry;
import ca.skipatrol.cnswap.jpa.entity.TaxEntryRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendortype;
import ca.skipatrol.cnswap.jpa.entity.VendortypeRepository;
import ca.skipatrol.cnswap.util.CNSwapLogger;

@Service
public class VendortypeService {

	
	Logger LOGGER = LoggerFactory.getLogger(VendortypeService.class);
	
	@Autowired
	private final VendortypeRepository vendortypeRepo;
		
	@Autowired
	private final TaxEntryRepository taxEntryRepo;
	

	public VendortypeService(VendortypeRepository vendortypeRepo,TaxEntryRepository taxEntryRepo) {
		this.vendortypeRepo=vendortypeRepo;		
		this.taxEntryRepo = taxEntryRepo;
	}
	
	public List<Vendortype> getVendortypes(){
		final String METHODNAME = "getVendortypesNames";
		CNSwapLogger.logEntry(LOGGER,METHODNAME, "");
		CNSwapLogger.logExit(LOGGER,METHODNAME,"");
		return vendortypeRepo.findAll();
		
	}

	public List<String> getVendortypesNames(){
		final String METHODNAME = "getVendortypesNames";
		CNSwapLogger.logEntry(LOGGER,METHODNAME, "");
		
		List<Vendortype> vendortype = vendortypeRepo.findAll();
    	List<String> vTypeNames = new ArrayList<String>();
    	for (Vendortype vtype : vendortype) {
    		vTypeNames.add(vtype.getVendortypeName());
    	}
    	
    	List<String> rv = vTypeNames;  
    	CNSwapLogger.logExit(LOGGER,METHODNAME,rv);
		return rv;
	}
	
	public Vendortype createVendortype(Vendortype vendortype) {
		final String METHODNAME = "createVendortype";
		CNSwapLogger.logEntry(LOGGER,METHODNAME,vendortype);
		
		if (vendortype.getVendortypeId() == null) {
			vendortype.setVendortypeId(getNextId());
		}
		CNSwapLogger.trace(LOGGER, vendortype.toString());
		
		Vendortype rv = vendortypeRepo.save(vendortype);
		List taxEntries = vendortype.getTaxEntry();
		Iterator<TaxEntry> iter = taxEntries.iterator();
		while (iter.hasNext()) {
			TaxEntry te = iter.next();
			List<Vendortype> vTypeList = te.getVendortype();
			vTypeList.add(vendortype);
			te.setVendortype(vTypeList);
			taxEntryRepo.save(te);
		}
		
		
		CNSwapLogger.logExit(LOGGER,METHODNAME,rv);
		return rv;
	}
	
	private String getNextId() {
		return String.valueOf(getVendortypes().size()+1);
	}

	public List<TaxEntry> getTaxEntries() {
		List<TaxEntry> taxEntries = taxEntryRepo.findAll();

		return taxEntries;
	}

	public TaxEntry createTaxEntry(TaxEntry taxEntryInfo) {
		final String METHODNAME = "createTaxEntry";
		CNSwapLogger.logEntry(LOGGER,METHODNAME,taxEntryInfo);
		taxEntryInfo.setPercentage(taxEntryInfo.getPercentage().divide(new BigDecimal(100)));
		TaxEntry rv = taxEntryRepo.save(taxEntryInfo);
		CNSwapLogger.logExit(LOGGER,METHODNAME,rv);
		return rv;
	}

	public void deleteTaxEntry(Integer taxEntryId) {
		final String METHODNAME = "deleteTaxEntry";
		CNSwapLogger.logEntry(LOGGER,METHODNAME,taxEntryId);
		
		taxEntryRepo.deleteById(taxEntryId);
		
		CNSwapLogger.logExit(LOGGER,METHODNAME,"");
	}

	public Vendortype getVendorTypeById(String vendorType) {
		
		return vendortypeRepo.findById(vendorType).get();
	}


	
}

