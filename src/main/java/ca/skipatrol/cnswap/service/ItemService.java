package ca.skipatrol.cnswap.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.skipatrol.cnswap.exception.CNSWAPException;
import ca.skipatrol.cnswap.exception.CNSWAPExceptionItemError;
import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.CatentryRepository;
import ca.skipatrol.cnswap.jpa.entity.CatentryType;
import ca.skipatrol.cnswap.jpa.entity.CatentryTypeRepository;
import ca.skipatrol.cnswap.jpa.entity.InventoryRepository;
import ca.skipatrol.cnswap.util.InventoryStatus;

@Service
public class ItemService {

	@Autowired
	private  CatentryRepository catentryRepo;
	
	@Autowired
	private CatentryTypeRepository catentryTypeRepo;
	
	@Autowired
	private InventoryRepository invRepo;
	
	
	public Catentry getBarcode(String barcode) {
		Optional<Catentry> catentryOptional = catentryRepo.findById(barcode);
		if (catentryOptional.isEmpty()) {
			return null;
		}
		return catentryOptional.get();
	}
	
	public int getQuantity(String barcode) {
		Catentry catEntry = getBarcode(barcode);
		if (catEntry != null && catEntry.getInventory() != null) {
			return catEntry.getInventory().getQuantity();
		}
		return 0;
	}

	public Iterable getAllTypes() {
		Iterable<CatentryType> typesIter = catentryTypeRepo.findAll();
		
		return typesIter;
	}

	public List<InventoryStatus> getAllInvStatus() {
		
		return Arrays.asList(InventoryStatus.values());
	}

	public Catentry reload(Catentry item) {
		return catentryRepo.findById(item.getId()).get();
	}
	
	public Catentry save(Catentry item) {
		return catentryRepo.save(item);
	}
	public Catentry mergeAndOverwrite(Catentry item) throws CNSWAPException {
		if (!catentryRepo.existsById(item.getId())) {
			throw new CNSWAPExceptionItemError("Unable to find item id:"+item.getId());
		}
		
		Catentry dbCatentry = catentryRepo.findById(item.getId()).get();
		// We can update this entry directly on the bean
		dbCatentry.getInventory().setStatus(item.getInventory().getStatus());
		dbCatentry.getInventory().setQuantity(item.getInventory().getQuantity());
		dbCatentry.setDescription(item.getDescription());
		dbCatentry.setIsNew(item.getIsNew());
		dbCatentry.setName(item.getName());
		dbCatentry.setPrice(item.getPrice());
		// We need to get the new type from the DB and assign it to the entity
		dbCatentry.setType(getCatEntryType(item.getType().getCatentryTypeName()));
		
		return catentryRepo.save(dbCatentry);		
	}

	private CatentryType getCatEntryType(String catentryTypeName) {
		return catentryTypeRepo.findByCatentryTypeName(catentryTypeName).get();
	}
	
}
