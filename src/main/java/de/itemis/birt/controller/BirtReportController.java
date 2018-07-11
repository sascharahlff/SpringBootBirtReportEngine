package de.itemis.birt.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.itemis.birt.service.ReportService;

@RestController
@RequestMapping("/report")
public class BirtReportController {
	public static final String COMPONENT_PATH = "/component";
	public static final String COMPONENT_UUID_PATH = "/component/{uuid}/resources";

//	@Value("${report.url}")
//	private String REST_SERVICE_URL;

//	public static final String CREATE_REPORT = "/create";
//	public static final String GET_REPORT = "/get";
//	public static final String PARAM_REPORT = "report";
//	public static final String PARAM_REPORT_XML = "xml";
	
	@Autowired
	ReportService reportService;

//	@RequestMapping(value = CREATE_REPORT, method = { RequestMethod.POST })
//	public String createReport(@RequestParam(PARAM_REPORT) final String report, @RequestParam(PARAM_REPORT_XML) final String xml) throws FileNotFoundException {
//		String fileName = "";
//		
//		if (report == null || report.isEmpty()) {
//			throw new IllegalArgumentException("Parameter 'report' can not be null or empty");
//		}
//		else if (xml == null || xml.isEmpty()) {
//			throw new IllegalArgumentException("Parameter 'xml' can not be null or empty");
//		}
//		else {
//			fileName = reportService.createReport(report, xml);
//
//			if (fileName == "") {
//				throw new RuntimeException("Service was not able to render the PDF");
//			}
//		}
//
//		return fileName;
//	}

	@RequestMapping(value = COMPONENT_PATH, consumes = MediaType.ALL_VALUE, method = RequestMethod.POST)
	public ResponseEntity<Object> createTempFolder(HttpServletRequest request) throws FileNotFoundException {
		String uuid = UUID.randomUUID().toString();
		
		if (!createTempFolder(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' could not be created");
		}
		
		HttpHeaders responseHeader = getResponseHeader(uuid, request);
		return new ResponseEntity<>(responseHeader, HttpStatus.CREATED);
	}

	@RequestMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
	public void uploadFile() {
		System.out.println("upload file");
	}
	
	private HttpHeaders getResponseHeader(final String uuid, final HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		URI location = URI.create(currentUrl + "/"+ uuid + "/resources");
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.setLocation(location);
		
		return responseHeader;
	}
	
	private boolean createTempFolder(String uuid) {
		File folder = new File("reports/temp/" + uuid);
		
		try {
			if (!folder.exists()) {
				folder.mkdir();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
//	@RequestMapping(value = GET_REPORT, produces = MediaType.APPLICATION_PDF_VALUE, method = RequestMethod.GET)
//	public ResponseEntity<InputStreamResource> getReport(@RequestParam(PARAM_REPORT) final String report) throws FileNotFoundException, IllegalArgumentException {
//		if (report == null || report.isEmpty()) {
//			throw new IllegalArgumentException("Parameter 'report' can not be null or empty");
//		}
//		else {
//			return reportService.getReport(report);
//		}
// 	}
}