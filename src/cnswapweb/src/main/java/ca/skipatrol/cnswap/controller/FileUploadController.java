package ca.skipatrol.cnswap.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.skipatrol.cnswap.jpa.entity.CNSFile;
import ca.skipatrol.cnswap.jpa.entity.CNSFileRepository;
import ca.skipatrol.cnswap.restservice.bean.CSVBadRecord;
import ca.skipatrol.cnswap.service.StorageService;
import ca.skipatrol.cnswap.storage.StorageFileNotFoundException;
import ca.skipatrol.cnswap.util.CNSwapUtil;

@Controller
public class FileUploadController {

	private final StorageService storageService;
	
	@Autowired
	CNSFileRepository fileRepo;
	

    public static final String CNSTYPE_CSV = "CSV";
    public static final String CNSTYPE_CSV_VALID = "CSV_V";
    public static final String CNSTYPE_CSV_PROCESSED = "CSV_D";
    public static final String CNSTYPE_ERROR = "ERROR";
    
    
    
	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	
	
	//*********************	
	//**** GET REQUESTS
	//*********************
	
	@GetMapping("/manage/uploadForm")
	public String listUploadedFiles(Model model) throws IOException {
		    
		 Iterator <CNSFile> iter = fileRepo.findAll().iterator();
		 
		 List<CNSFile> fileList = new ArrayList<CNSFile>();
		 iter.forEachRemaining(fileList::add);
	
		 model.addAttribute("files", fileList);
			
			
		return "manage/uploadForm";
	}

	@GetMapping("/manage/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@GetMapping(path = "/manage/files/{filename:.+}", params = "action")
	public String getFile(@PathVariable String filename,@RequestParam String action, RedirectAttributes redirectAttributes) {

		StringBuffer uiMessage= new StringBuffer();
		StringBuffer uiErrorMessage= new StringBuffer();
		
		// We do a quick check on the file
		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			redirectAttributes.addFlashAttribute("error",
					"Unable to process file: " + filename + ". This is most likely a system error. CNSWAP_ERR001_FILE_CNTRL");
		
		
		if (action.compareToIgnoreCase("VALIDATE")==0) {
			List<CSVBadRecord> badRecords = storageService.validateCsvFile(filename);
			
			if (badRecords.size() >=1) {
				String errorFileName= filename+"_ERRORS";
				storageService.storeErrors(errorFileName,badRecords);
				
				try {
					createErrorFileRecord(errorFileName);
				} catch (IOException e) {
					uiErrorMessage.append("Error in writting the report, check the CSV file in detailed and retry. CNSWAP_ERR002_FILE_CNTRL");
					e.printStackTrace();
				}
				
				uiErrorMessage.append("There are errors in the CSV file. Download <a href=\"/manage/files/"+errorFileName+"\" class=\"alert-link\">"+errorFileName+"</a> review and fix the error");
				
				
			}
			else {
				updateCSVFileRecord(filename,FileUploadController.CNSTYPE_CSV_VALID);
				uiMessage.append("Looking good!");
				
			}
			uiMessage.append("File validation completed");
			
			if (!uiMessage.isEmpty()) 
				redirectAttributes.addFlashAttribute("message",uiMessage);
			
			if (!uiErrorMessage.isEmpty()) 	
				redirectAttributes.addFlashAttribute("errorMessage",uiErrorMessage);
			
		}			
		
		return ("redirect:/manage/uploadForm");
	}
	
	
	//*********************	
	//**** POST REQUESTS
	//*********************
	
	@PostMapping("/manage/uploadForm")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);

		createCSVFileRecord(file,FileUploadController.CNSTYPE_CSV);
		
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/manage/uploadForm";
	}
	
	
	
	//*********************	
	//**** PRIVATE METHODS AND UTILS
	//*********************	
	
	
	private void createErrorFileRecord(String filename) throws IOException{
		// Store the filename in the Database 
		CNSFile fileJpa = new CNSFile();
		fileJpa.setFilename(filename);
		fileJpa.setUploadedTime( CNSwapUtil.getCurrentTimestamp());
		fileJpa.setCnstype(FileUploadController.CNSTYPE_ERROR);
		fileJpa.setUrl(MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "getFile", filename).build().toString());
		
		
		// Store the filename in the Database 
		fileRepo.save(fileJpa);
		
	}
	
	private void updateCSVFileRecord(String filename,String cnstype) {
		
		//  Store the filename in the Database
		Optional<CNSFile> fileJpaResultSet = fileRepo.findById(filename);
		CNSFile fileJpa;
		
		if (fileJpaResultSet.isEmpty()) {
			System.out.println("Unable to find stored filed in DB. CNSWAP_ERR003_FILE_CNTRL");
			return;
		}
		fileJpa = fileJpaResultSet.get();
			
		fileJpa.setCnstype(cnstype);
		
		// Store the filename in the Database 
		fileRepo.save(fileJpa);
				
	}
	
	
	
	private void createCSVFileRecord(MultipartFile file,String cnstype) {
		// Store the filename in the Database 
		CNSFile fileJpa = new CNSFile();
		fileJpa.setFilename(file.getOriginalFilename());
		fileJpa.setUploadedTime( new Timestamp(System.currentTimeMillis()));
		fileJpa.setSize(file.getSize());
		fileJpa.setCnstype(cnstype);
		fileJpa.setHRSize(CNSwapUtil.bytesToMeg(file.getSize()));
		fileJpa.setUrl(MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "getFile", file.getOriginalFilename().toString()).build().toString());
		
		
		// Store the filename in the Database 
		fileRepo.save(fileJpa);
				
	}
	
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}