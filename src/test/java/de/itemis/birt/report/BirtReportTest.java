package de.itemis.birt.report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.itemis.birt.Application;
import de.itemis.birt.controller.BirtReportController;
import de.itemis.birt.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class,
		BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportTest {
	//private String sampleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><library><name>Biff Tannen</name></library>";

	@Autowired
	ReportService reportService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void createTempFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(200)).andReturn();
		String location = result.getResponse().getHeader("Location");
		System.out.println("location: " + location);
		
		//http://localhost/report/component/6676d00e-7da8-4579-9d5e-aeed9bdea947/resources
		
			//.andExpect(status().is(201));

		// fe045882-8a3f-4aa8-937a-35d8570c93e7
	}

	@Test
	public void uploadFileToTempFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(200)).andReturn();
		String location = result.getResponse().getHeader("Location");
		
//		 File file = new File();
//		 
//         System.out.println(f.isFile()+"  "+f.getName()+f.exists());
//         FileInputStream fi1 = new FileInputStream(f);
//         FileInputStream fi2 = new FileInputStream(new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Tulips.jpg"));
//         MockMultipartFile fstmp = new MockMultipartFile("upload", f.getName(), "multipart/form-data",fi1);
//         MockMultipartFile secmp = new MockMultipartFile("upload", "Tulips.jpg","multipart/form-data",fi2); 
//         MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//         mockMvc.perform(MockMvcRequestBuilders.fileUpload("/AddContacts")                
//                 .file(fstmp)
//                 .file(secmp)
//                 .param("name","abc").param("email","abc@gmail.com").param("phone", "1234567890"))               
//                 .andExpect(status().isOk());
	}
	
	

	// @Test(expected = FileNotFoundException.class)
	// public void reportNotFoundTest() throws FileNotFoundException {
	// String pdf = reportService.createReport("test.rptdesign", sampleXml);
	// assertEquals(pdf, "");
	// }
	//
	// @Test
	// public void reportTest() throws FileNotFoundException {
	// String pdf = reportService.createReport("new.rptdesign", sampleXml);
	// assertNotEquals(pdf, "");
	// }
	//
	// @Test
	// public void reportRestServiceTest() throws Exception {
	// mockMvc.perform(post("/report" +
	// BirtReportController.CREATE_REPORT).param("report",
	// "new.rptdesign").param("xml", sampleXml))
	// .andExpect(status().isOk()).andExpect(content().string(containsString(".pdf")));
	// }
}
