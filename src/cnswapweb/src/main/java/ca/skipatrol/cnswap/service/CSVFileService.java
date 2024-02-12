package ca.skipatrol.cnswap.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import ca.skipatrol.cnswap.configuration.StorageConfig;
import ca.skipatrol.cnswap.jpa.entity.CNSFile;
import ca.skipatrol.cnswap.jpa.entity.CNSFileRepository;
import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.CatentryRepository;
import ca.skipatrol.cnswap.jpa.entity.CatentryType;
import ca.skipatrol.cnswap.jpa.entity.CatentryTypeRepository;
import ca.skipatrol.cnswap.jpa.entity.Inventory;
import ca.skipatrol.cnswap.jpa.entity.InventoryRepository;
import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.restservice.bean.CSVBadRecord;
import ca.skipatrol.cnswap.storage.StorageException;
import ca.skipatrol.cnswap.storage.StorageFileNotFoundException;
import ca.skipatrol.cnswap.storage.StorageFileParsingException;
import ca.skipatrol.cnswap.util.InventoryStatus;

@Service
public class CSVFileService implements StorageService {

	private final Path rootLocation;
	
	@Autowired
	CatentryRepository catentryRepo;
	
	@Autowired
	CatentryTypeRepository catentryTypeRepo;
	
	@Autowired
	CNSFileRepository cnsFileRepo;
	
	@Autowired
	InventoryRepository invRepo;
	
	
	public static final String CSV_HEADER_LINE = "Barcode,Type,Description,Size,Price,New";
	

	@Autowired
	public CSVFileService(StorageConfig properties) {
        
        if(properties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
        }

		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}

			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
			
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}
	
	
	@Override
	public void storeErrors(String filename,List<CSVBadRecord> badRecords) {
		try {
		
			Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
			if (destinationFile.toFile().exists()){
				destinationFile.toFile().delete();
			}
			
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException("Cannot store file outside current directory.");
			}
			
			//BufferedWriter writer = new BufferedWriter(new FileWriter(file.getFilename()));
			BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile.toFile(), true));
			
			
			for (int i=0; i<badRecords.size();i++) {
				writer.append(((CSVBadRecord)badRecords.get(i)).toString());
				writer.newLine();
			}
			
			writer.append("---EOF--");
			writer.append("--------Expected file content format:-------\n");
			writer.append("Barcode,Type,Description,Size,Price,New\n");
			writer.append("PU20231,DH Boots,jr boots,15.5,79,Y\n");
			
			
			writer.flush();
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}
	
	
	
	@Override
	public Stream<Path> loadAll() {
		try {
			
			Stream<Path> fStream = Files.walk(this.rootLocation, 1) 
					.filter(
					path -> !path.equals(this.rootLocation)
				).map(this.rootLocation::relativize);
			
			
			return fStream;
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}
	
	public List<Catentry> processCsvFile(String filename,Vendor vendor){
		
		List<Catentry> returnList = new ArrayList();
		Resource resource;
		Path file = load(filename);
		
		// **********
		// Try to LOAD the file
		// **********
		
		try {
			file = load(filename);
			resource = new UrlResource(file.toUri());
			if (! resource.exists() || !resource.isReadable()) {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
		
		// **********
		// Try to OPEN the file
		// **********
		
	   BufferedReader lineReader;
		try {
			lineReader = new BufferedReader(new FileReader(file.toFile()));
		} catch (FileNotFoundException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
		
       String lineText = null;

       int count = 1;

       // ********** 
       // Try to READ a line in the file
       // **********
       
       try {
			
			while ((lineText = lineReader.readLine()) != null) {
	    	   //CSVBadRecord aCsvLine = new CSVBadRecord(count++,lineText);
	    	   
	           String[] data = lineText.split(",");
	           
	           
	           if (data.length == 6) {
	        	   if(lineText.equalsIgnoreCase(CSV_HEADER_LINE)) {
	        		   // Header file matches. Nothing to do, just skip this line
	        	   }
	        	   else {
		        	   
	        		   // Line data is complete, process each entry
		        	   String catentry = data[0];
			           String catentryType = data[1];
			           String catentryDesc = data[2];
			           String size = data[3];
			           String price = data[4];
			           String catEntryNew = data[5];
			            
	        		   
	        		   Catentry anItem = new Catentry();
	        		   // **** First populated the association
	        		   // ** catentryType association
	        		   Optional<CatentryType> catentryTypeList = catentryTypeRepo.findByCatentryTypeName(catentryType);
	        		   anItem.setType(catentryTypeList.get());

	        		   // ** vendor association
	        		   anItem.setVendor(vendor);
	        		   
	        		   // * Now the csv data 
	        		   anItem.setId(catentry);
	        		   anItem.setName(catentryDesc);
	        		   anItem.setSize(size);
	        		   anItem.setPrice(Long.valueOf(price));
	        		   if (!catEntryNew.isEmpty() && catEntryNew.equalsIgnoreCase("Y")) {
	        			   catEntryNew = "true";
	        		   }
	        		   else {
	        			   catEntryNew = "false";
	        		   }
	        		   anItem.setIsNew(Boolean.valueOf(catEntryNew));
	        		   
	        		   
	        		   // * Now the Inventory, there is a single item per line
	        		   // TODO: Better inventory management, and read from the CSV
	        		   Inventory inventory = new Inventory();
	        		   inventory.setId(Integer.parseInt(anItem.getId().replaceAll("\\D", "")));
	        		   inventory.setCatentry(anItem);
	        		   inventory.setQuantity(1);
	        		   inventory.setStatus(InventoryStatus.PENDING_CHECKIN.toString());
	        		   
	        		   anItem.setInventory(inventory);
	        		   catentryRepo.save(anItem);
	        		   //invRepo.save(inventory);
	        		   
	        		   
	        		   
	        		   returnList.add(anItem);
			           
	        	   }
	        	  
		           
	           }	           
	         
       }

       lineReader.close();
		
       } catch (IOException e) {
			throw new StorageFileParsingException("IO Exceptionfile: " + filename, e);
       } 
		
 
		
		return returnList;
	}
	
	public List<CSVBadRecord> validateCsvFile(String filename){
		ArrayList <CSVBadRecord> badRecords = new ArrayList<CSVBadRecord>(); 
		
		
		Resource resource;
		Path file = load(filename);
		
		// **********
		// Try to LOAD the file
		// **********
		
		try {
			file = load(filename);
			resource = new UrlResource(file.toUri());
			if (! resource.exists() || !resource.isReadable()) {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
		
		// **********
		// Try to OPEN the file
		// **********
		
	   BufferedReader lineReader;
		try {
			lineReader = new BufferedReader(new FileReader(file.toFile()));
		} catch (FileNotFoundException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
		
       String lineText = null;

       int count = 1;

       // ********** 
       // Try to READ a line in the file
       // **********
       
       try {
			
			while ((lineText = lineReader.readLine()) != null) {
	    	   CSVBadRecord aCsvLine = new CSVBadRecord(count++,lineText);
	    	   
	           String[] data = lineText.split(",");
	           
	           
	           if (data.length < 6 ) {
	        	   aCsvLine.setPossibleError("Records expected: 6, found: "+data.length);
	        	   badRecords.add(aCsvLine);

	           }
	           else if (data.length == 6) {
	        	   if(lineText.equalsIgnoreCase(CSV_HEADER_LINE)) {
	        		   // Header file matches. Nothing to do, just skip this line
	        	   }
	        	   else {
		        	   // Line data is complete, process each entry
		        	   String catentry = data[0];
			           String catentryType = data[1];
			           String catentryDesc = data[2];
			           String size = data[3];
			           String price = data[4];
			           String catEntryNew = data[5];
			            
			           Optional<CatentryType> typeResultSet = catentryTypeRepo.findByCatentryTypeName(catentryType);
			           if(typeResultSet.isEmpty()) {
			        	   aCsvLine.setPossibleError("Unable to find Type:"+catentryType);
			        	   badRecords.add(aCsvLine);   
			           }
	        	   }
	        	  
		           
	           } else if (data.length > 6) {
	        	   aCsvLine.setPossibleError("Records expected: 6, found: "+data.length);
	        	   badRecords.add(aCsvLine);
	           }	           
           
	         
       }

       lineReader.close();
		
       } catch (IOException e) {
			throw new StorageFileParsingException("IO Exceptionfile: " + filename, e);
       } 
		
       
       Optional<CNSFile> cnsFileResultSet = cnsFileRepo.findById(filename);
		
       if (!cnsFileResultSet.isEmpty()) {
    	   CNSFile cnsFile = cnsFileResultSet.get();
    	   cnsFile.setTotalOfRecords(count);
    	   cnsFileRepo.save(cnsFile);
       }
		
		
		return badRecords;
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}
	
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
	
}