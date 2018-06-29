package de.itemis.birt;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.ReportEngine;


public class RunReportParam {
	public static void main(String[] args) {
		String templateFilename = "src/main/resources/reports/new.rptdesign";
		EngineConfig config = new EngineConfig();
		ReportEngine engine = new ReportEngine(config);
		IReportRunnable report = null;

		try {
			report = engine.openReportDesign(templateFilename);
		} catch (Exception e) {
			System.err.println("Report " + templateFilename + " not found!\n");
			engine.destroy();
			return;
		}

		IRunAndRenderTask task = engine.createRunAndRenderTask(report);
		
		// use xmlstring 
		String xmlstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		//xmlstring += "<data>ttteeessstttt</data>";
		xmlstring += "<library>"
				+ "<name>Biff Tannen</name><data>"
				+ "<book category='FOO'><title lang='en'>BIRT ist kein Geschenk</title><author name='Sascha Rahlff' country='it'/><year>2015</year></book>"
				+ "<book category='BAR'><title lang='en'>BIRT suxx ...</title><author name='Biff Tannen' country='uk' /><year>2016</year></book>"
				+ "<book category='BLUB'><title lang='en'>BIRT kann XML Datasets</title><author name='James McGovern' country='us' /><year>2017</year></book>"
				+ "</data></library>";
		//Map<String, String> params = new HashMap<String, String>();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(xmlstring.getBytes());
		Map<String, ByteArrayInputStream> appContext = task.getAppContext();
		appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", bais);
		
		//params.put("xmlContent", "books2.xml");
		//params.put("xmlContent", xmlstring);
		
//		ByteArrayInputStream bais = new ByteArrayInputStream(xmlstring.getBytes());
//		task.getAppContext().put("org.eclipse.datatools.enablement.oda.xml.inputStream", bais);

		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
		String output = "foo.pdf"; //templateFilename.replaceFirst(".rptdesign", "." + HTMLRenderOption.OUTPUT_FORMAT_PDF);
		options.setOutputFileName(output);
		task.setRenderOption(options);
		//task.setParameterValues(params);

		// Run the report.
		try {
			task.run();
		} catch (EngineException e1) {
			System.err.println("Report " + templateFilename + " run failed.\n");
			System.err.println(e1.toString());
		}

		engine.destroy();
		Platform.shutdown();
	}
}
