package de.itemis.birt.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.itemis.birt.service.ReportService;

@RestController
@RequestMapping("/report")
public class BirtReportController {
	public static final String REPORT_PATH = "./reports/";
	public static final String CREATE_REPORT = "/create";
	public static final String GET_REPORT = "/get";
	public static final String PARAM_REPORT = "report";
	public static final String PARAM_REPORT_XML = "xml";

	@Autowired
	ReportService reportService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "Hello World";
	}

	@RequestMapping(value = CREATE_REPORT, method = { RequestMethod.GET, RequestMethod.POST })
	public String createReport(@RequestParam(PARAM_REPORT) final String report,
			@RequestParam(PARAM_REPORT_XML) final String xml) {
		String fileName = "";

		if (report == null || report.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'report' can not be null or empty");
		}
		else if (xml == null || xml.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'xml' can not be null or empty");
		}
		else {
			fileName = reportService.createReport(report, xml);

			if (fileName == "") {
				throw new RuntimeException("Service was not able to render the PDF");
			}
		}

		return fileName;
	}

	@RequestMapping(value = GET_REPORT, method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<InputStreamResource> getReport(@RequestParam(PARAM_REPORT) final String report) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add("content-disposition", "attachment; filename=" + report);

		if (report == null || report.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'report' can not be null or empty");
		}
		else {
			File reportFile = new File(REPORT_PATH + report);
						
			if (!reportFile.exists()) {
				throw new IllegalArgumentException("Report '"+ report +"' does not exist");
			}
			else {
				InputStream reportStream = new FileInputStream(reportFile);

			    return ResponseEntity
			            .ok()
			            .headers(headers)
			            .contentLength(reportFile.length())
			            .contentType(MediaType.parseMediaType("application/octet-stream"))
			            .body(new InputStreamResource(reportStream));
			}
		}
 	}
}