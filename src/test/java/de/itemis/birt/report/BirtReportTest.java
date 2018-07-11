package de.itemis.birt.report;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import de.itemis.birt.Application;
import de.itemis.birt.controller.BirtReportController;
import de.itemis.birt.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class,
		BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportTest {
	// private String sampleXml = "<?xml version=\"1.0\"
	// encoding=\"UTF-8\"?><library><name>Biff Tannen</name></library>";

	@Autowired
	ReportService reportService;

	@Autowired
	private MockMvc mockMvc;

	// @Test
	public void createTempFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(201)).andReturn();
		String location = result.getResponse().getHeader("Location");
		System.out.println("location: " + location);

		// http://localhost/report/component/6676d00e-7da8-4579-9d5e-aeed9bdea947/resources

		// .andExpect(status().is(201));

		// fe045882-8a3f-4aa8-937a-35d8570c93e7
	}

	@Test
	public void uploadFileToTempFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(201)).andReturn();
		String location = result.getResponse().getHeader("Location");

		File file = ResourceUtils.getFile("classpath:assets/images/hasi.png");
		assertEquals(true, file.exists());	
		//byte[] base64image = Base64.getEncoder().encode(loadFileAsBytesArray(file));
		MockMultipartFile firstFile = new MockMultipartFile("data", "hasi.png", "text/plain", loadFileAsBytesArray(file));

        mockMvc.perform(MockMvcRequestBuilders.multipart(location)
                        .file(firstFile))
                    .andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
    public static byte[] loadFileAsBytesArray(File file) throws Exception {
    	 
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
 
    }
}
