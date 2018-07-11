package de.itemis.birt.controller;

import java.io.FileNotFoundException;

import org.jboss.logging.FormatWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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

	@RequestMapping(value = CREATE_REPORT, consumes = MediaType.ALL_VALUE, method = RequestMethod.POST)
	public String createReport(@RequestParam(PARAM_REPORT) final String report, @RequestBody final String xml) throws FileNotFoundException {
		System.out.println(report);
		System.out.println(xml);
		System.out.println("--------------");
		
		return reportService.createReport(report, xml);
	}

	@RequestMapping(value = GET_REPORT, produces = MediaType.APPLICATION_PDF_VALUE, method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getReport(@RequestParam(PARAM_REPORT) final String report) throws FileNotFoundException, IllegalArgumentException {
		if (report == null || report.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'report' can not be null or empty");
		}
		else {
			return reportService.getReport(report);
		}
 	}
}