package de.itemis.birt.report;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.springframework.stereotype.Component;

@Component
public class ReportEngine {
	private IReportEngine engine = null;

	@PostConstruct
	public void init() {
		System.out.println("startup");
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.setLogConfig("logs", Level.WARNING);

		try {
			Platform.startup(engineConfig);

			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(engineConfig);
			engine.changeLogLevel(Level.WARNING);
		} catch (BirtException e) {
			e.printStackTrace();
		}
	}

	public void doStuff() {
		System.out.println("do report stuff");
		try {
			// Load design
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("reports/blub.rptdesign");
			URL outputUrl = classLoader.getResource("reports/");

			// Render to pdf
			IReportRunnable design = engine.openReportDesign(url.getFile());
			RenderOption renderOption = new PDFRenderOption();
			renderOption.setOutputFormat("pdf");
			renderOption.setOutputFileName(outputUrl.getFile() + "blub.pdf");

			// Set parameters to prefill PDF
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstName", "blub foo bar");
			params.put("myLabel", "foo.bar");
			
			// Render task
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
			task.setRenderOption(renderOption);
			task.setParameterValues(params);
			task.validateParameters();
			
			task.run();
			task.close();
		} catch (EngineException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void destroy() {
		System.out.println("destroy");
		engine.destroy();
		Platform.shutdown();
	}
}
