package com.bshg.plc.component.report.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface ReportService {
	public String createReport(String uuid) throws FileNotFoundException, IOException;
	public ResponseEntity<InputStreamResource> getReport(String report) throws FileNotFoundException;
}
