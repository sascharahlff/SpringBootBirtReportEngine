package com.bshg.plc.component.report.controller;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	public static final String COMPONENT_UUID_PATH = "/component/{uuid}/resources";

	@Autowired
	ReportService reportService;

	@Autowired
	UploadService uploadService;

	@PostMapping(value = COMPONENT_PATH, consumes = MediaType.ALL_VALUE)
	public ResponseEntity<Object> createTempFolder(HttpServletRequest request) throws FileNotFoundException {
		String uniqueFolderName = uploadService.createUniqueFolder();
		HttpHeaders responseHeader = getResponseHeader(uniqueFolderName, request);

		return new ResponseEntity<Object>(responseHeader, HttpStatus.CREATED);
	}
	
	@PostMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody List<ReportAsset> uploadFile(@PathVariable String uuid, @RequestParam("files") List<MultipartFile> files) throws Exception {
		List<ReportAsset> assetList = uploadService.uploadFiles(files, uuid);

		//MultiValueMap<String, Person> map = new LinkedMultiValueMap<>();
		
		return assetList;
	}

	private HttpHeaders getResponseHeader(final String uuid, final HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		URI location = URI.create(currentUrl + "/" + uuid + "/resources");
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.setLocation(location);

		return responseHeader;
	}
}