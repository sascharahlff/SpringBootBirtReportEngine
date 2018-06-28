package de.itemis.birt.report;

import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
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
		engineConfig.setLogConfig("usr/repos/logs", Level.WARNING);

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
			URL url = classLoader.getResource("reports/test.rptdesign");
			URL outputUrl = classLoader.getResource("reports/");

			// Render to pdf
			IReportRunnable design = engine.openReportDesign(url.getFile());
			RenderOption renderOption = new PDFRenderOption();
			renderOption.setOutputFormat("pdf");
			renderOption.setOutputFileName(outputUrl.getFile() + "foo.pdf");

			// Render task
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
			task.setRenderOption(renderOption);

			// Set parameter values and validate
			// task.setParameterValue("Top Percentage", (new Integer(3)));
			// task.setParameterValue("Top Count", (new Integer(5)));
			// task.validateParameters();

			// Setup rendering to HTML
			// HTMLRenderOption options = new HTMLRenderOption();
			// options.setOutputFileName("output/resample/TopNPercent.html");
			// options.setOutputFormat("html");

			// Setting this to true removes html and body tags
			//renderOption.setEmbeddable(false);
			task.setRenderOption(renderOption);
			// run and render report
			task.run();
			task.close();
		} catch (EngineException e) {
			e.printStackTrace();
		}
	}

//	public void test() {
//		try {
//			RenderOption renderOption = getFormatOption(reportId, request.getFormat());
//			IReportRunnable design = reportEngine.openReportDesign(reportText);
//			IRunAndRenderTask task = reportEngine.createRunAndRenderTask(design);
//			task.setRenderOption(renderOption);
//
//			if (request.getParams() != null) {
//				task.setParameterValues(request.getParams());
//			}
//
//			task.run();
//			task.close();
//			logger.info("Finished report with id {}", reportId);
//		} catch (Exception e) {
//			logger.error("Report rendering error", e);
//			throw new Exception("Report rendering error");
//		}
//
//		reportText.close();
//
//		return getReportUrl(reportId, request.getFormat());
//	}

	@PreDestroy
	public void destroy() {
		System.out.println("destroy");
		engine.destroy();
		Platform.shutdown();
	}
}
