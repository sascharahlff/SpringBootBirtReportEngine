package com.bshg.plc.component.report.controller;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.service.ReportService;
import com.bshg.plc.component.report.service.UploadService;

@RestController
@RequestMapping("/report")
public class BirtReportController {
	public static final String COMPONENT_PATH = "/component";
	public static final String COMPONENT_UUID_PATH = "/component/{uuid}";
	public static final String COMPONENT_UUID_DATA_PATH = "/component/{uuid}/data";
	public static final String COMPONENT_UUID_RESOURCES_PATH = "/component/{uuid}/resources";

	@Autowired
	ReportService reportService;

	@Autowired
	UploadService uploadService;

	@PostMapping(value = COMPONENT_PATH, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> createTemporaryFolder(HttpServletRequest request) throws FileNotFoundException {
		String uniqueFolderName = uploadService.createTempFolder();
		HttpHeaders responseHeader = createResponseHeader(uniqueFolderName, request);

		return new ResponseEntity<Object>(responseHeader, HttpStatus.CREATED);
	}
	
	@PostMapping(value = COMPONENT_UUID_RESOURCES_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody List<ReportAsset> uploadMultipartFiles(@PathVariable String uuid, @RequestParam("files") List<MultipartFile> files) throws Exception {
		if (files == null || files.size() == 0) {
			throw new IllegalArgumentException("No files provided for upload.");
		}
		
		List<ReportAsset> assetList = uploadService.uploadMultipartFiles(files, uuid);
		
		return assetList;
	}

	@PostMapping(value = COMPONENT_UUID_DATA_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadXML(@PathVariable String uuid, @RequestParam("data") MultipartFile file) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("No file provided for upload.");
		}

		List<MultipartFile> files = new ArrayList<MultipartFile>();
		files.add(file);
		uploadService.uploadMultipartFiles(files, uuid);
	}

	
	@GetMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeTemporaryFolder(@PathVariable String uuid) {
		uploadService.removeTemporaryFolder(uuid);
	}
	
	private HttpHeaders createResponseHeader(final String uuid, final HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		URI location = URI.create(currentUrl + "/" + uuid + "/resources");
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.setLocation(location);

		return responseHeader;
	}
}