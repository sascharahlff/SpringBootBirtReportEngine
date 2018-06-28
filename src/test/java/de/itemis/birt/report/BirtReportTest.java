package de.itemis.birt.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.itemis.birt.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class BirtReportTest {
	@Autowired
	ReportRenderer reportRenderer;
	
	@Test
	public void reportTest() {
		reportRenderer.render();
//		ReportEngine engine = new ReportEngine();
//		engine.doStuff();
	}
}
