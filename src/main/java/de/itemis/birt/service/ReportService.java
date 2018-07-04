package de.itemis.birt.service;

import java.io.FileNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface ReportService {
	public String createReport(final String report, final String xml) throws FileNotFoundException;
	public ResponseEntity<InputStreamResource> getReport(final String report) throws FileNotFoundException;
}
