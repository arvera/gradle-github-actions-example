package ca.skipatrol.cnswap.service;


import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import ca.skipatrol.cnswap.jpa.entity.Catentry;
import ca.skipatrol.cnswap.jpa.entity.Vendor;
import ca.skipatrol.cnswap.restservice.bean.CSVBadRecord;

public interface StorageService {

	void init();

	void store(MultipartFile file);
	
	void storeErrors(String file, List<CSVBadRecord> badRecords);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();
	
	List<Catentry> processCsvFile(String filename,Vendor vendor);
	
	List<CSVBadRecord> validateCsvFile(String filename);

}