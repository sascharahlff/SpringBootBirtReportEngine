package de.itemis.birt.report;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.itemis.birt.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public class BirtReportTest {
	@Test
	public void reportTest() {
		//ReportEngine engine = new ReportEngine();
	}
}
