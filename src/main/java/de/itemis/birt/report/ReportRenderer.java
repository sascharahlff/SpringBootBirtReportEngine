package de.itemis.birt.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportRenderer {
	@Autowired
	ReportEngine reportEngine;
	
	public void render() {
		reportEngine.doStuff();
	}
}
