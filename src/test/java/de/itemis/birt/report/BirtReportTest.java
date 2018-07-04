package de.itemis.birt.report;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import de.itemis.birt.Application;
import de.itemis.birt.controller.BirtReportController;
import de.itemis.birt.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class, BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportTest {
	private String sampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><library><name>Biff Tannen</name></library>";

	@Autowired
	ReportService reportService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void reportNotFoundTest() throws FileNotFoundException {
		String pdf = reportService.createReport("test.rptdesign", sampleXml);
		assertEquals("", pdf);
	}

	@Test
	public void reportTest() throws FileNotFoundException {
		String pdf = reportService.createReport("new.rptdesign", sampleXml);
		assertNotEquals(pdf, "");
	}

	@Test
	public void reportRestServiceTest() throws Exception {
		mockMvc.perform(post("/report" + BirtReportController.CREATE_REPORT).param("report", "new.rptdesign").param("xml", sampleXml))
				.andExpect(status().isOk()).andExpect(content().string(containsString(".pdf")));
	}
}
