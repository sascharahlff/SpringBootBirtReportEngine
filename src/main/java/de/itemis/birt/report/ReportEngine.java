package de.itemis.birt.report;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ReportEngine {
	@PostConstruct
	public void init() {
		System.out.println("startup");
	}
}
