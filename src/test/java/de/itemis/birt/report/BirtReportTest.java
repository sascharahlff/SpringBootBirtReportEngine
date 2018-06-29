package de.itemis.birt.report;

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
	@Autowired
	ReportService reportService;
	
	@Test
	public void reportTest() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml += "<library>"
				+ "<name>Biff Tannen</name><data>"
				+ "<book category='FOO'><title lang='en'>BIRT ist kein Geschenk</title><author name='Sascha Rahlff' country='it'/><year>2015</year></book>"
				+ "<book category='BAR'><title lang='en'>BIRT suxx ...</title><author name='Biff Tannen' country='uk' /><year>2016</year></book>"
				+ "<book category='BLUB'><title lang='en'>BIRT kann XML Datasets</title><author name='James McGovern' country='us' /><year>2017</year></book>"
				+ "</data></library>";
		String pdf = reportService.createReport("new.rptdesign", xml);
		
		assertNotEquals(pdf, "");
	}
}
