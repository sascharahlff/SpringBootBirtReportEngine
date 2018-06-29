package de.itemis.birt.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.itemis.birt.Application;
import de.itemis.birt.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class BirtReportTest {
	private String sampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><library><name>Biff Tannen</name></library>";

	@Autowired
	ReportService reportService;

	@Test
	public void reportNotFoundTest() {
		String pdf = reportService.createReport("test.rptdesign", sampleXml);
		assertEquals("", pdf);
	}

	@Test
	public void reportTest() {
		String pdf = reportService.createReport("new.rptdesign", sampleXml);
		assertNotEquals(pdf, "");
	}
}
