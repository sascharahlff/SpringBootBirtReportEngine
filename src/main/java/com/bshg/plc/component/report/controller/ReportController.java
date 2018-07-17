package com.bshg.plc.component.report.controller;

import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.service.ReportService;
import com.bshg.plc.component.report.service.ResourceService;

@RestController
@RequestMapping("/report")
public class ReportController {
	private static final String COMPONENT_PATH = "/component";
	private static final String COMPONENT_UUID_PATH = "/component/{uuid}";
	private static final String COMPONENT_UUID_DATA_PATH = "/component/{uuid}/data";
	private static final String COMPONENT_UUID_RESOURCES_PATH = "/component/{uuid}/resources";

	@Autowired
	ReportService reportService;

	@Autowired
	ResourceService resourceService;

	@PostMapping(value = COMPONENT_PATH, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> createTemporaryFolder(HttpServletRequest request) throws FileNotFoundException {
		String uniqueFolderName = resourceService.createTempFolder();
		HttpHeaders responseHeader = createResponseHeader(uniqueFolderName, request);

		return new ResponseEntity<Object>(responseHeader, HttpStatus.CREATED);
	}

	@PostMapping(value = COMPONENT_UUID_RESOURCES_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody List<ReportAsset> uploadMultipartFiles(@PathVariable String uuid, @RequestParam(Constants.REQUEST_PARAM_MULTIPART) List<MultipartFile> files) throws Exception {
		if (!folderExists(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' does not exists.");
		}

		if (files == null || files.size() == 0) {
			throw new IllegalArgumentException("No files provided for upload.");
		}

		List<ReportAsset> assetList = resourceService.uploadMultipartFiles(uuid, files);

		return assetList;
	}

	@PostMapping(value = COMPONENT_UUID_DATA_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadXML(@PathVariable String uuid, @RequestParam(Constants.REQUEST_PARAM_XML) MultipartFile file) throws Exception {
		if (!folderExists(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' does not exists.");
		}

		if (file == null) {
			throw new IllegalArgumentException("No xml file provided for upload.");
		}

		resourceService.uploadDataXml(uuid, file);
	}

	@GetMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<byte[]> createReport(@PathVariable String uuid) throws Exception {
		if (!folderExists(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' does not exists.");
		}

		byte[] content = reportService.createReport(uuid);
		String filename = Constants.DEFAULT_REPORT_NAME;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);

		return response;
	}

	@DeleteMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeTemporaryFolder(@PathVariable String uuid) throws FileNotFoundException {
		if (!folderExists(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' does not exists.");
		}

		resourceService.removeTemporaryFolder(uuid);
	}

	private HttpHeaders createResponseHeader(final String uuid, final HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		URI location = URI.create(currentUrl + "/" + uuid);
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.setLocation(location);

		return responseHeader;
	}

	private boolean folderExists(final String uuid) {
		Path path = Paths.get(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

		return Files.exists(path);
	}
}