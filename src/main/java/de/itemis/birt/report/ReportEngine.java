package de.itemis.birt.report;

import java.io.ByteArrayInputStream;
import java.io.File;
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
	private static final String REPORTS_PATH = "src/main/resources/reports/";
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

	@PreDestroy
	public void destroy() {
		System.out.println("destroy");
		engine.destroy();
		Platform.shutdown();
	}
	
	public boolean createReport(final String reportFile, final String xml) {
		if (reportExists(reportFile)) {
			IReportRunnable report = getReport(REPORTS_PATH + reportFile);
			
			if (report != null) {
				IRunAndRenderTask task = engine.createRunAndRenderTask(report);
				
				ByteArrayInputStream byteArray = new ByteArrayInputStream(xml.getBytes());
				Map<String, ByteArrayInputStream> appContext = task.getAppContext();
				appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", byteArray);
				
				RenderOption renderOption = new PDFRenderOption();
				renderOption.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
				renderOption.setOutputFileName("blub.pdf");

				task.setRenderOption(renderOption);
				
				try {
					task.run();
				} catch (EngineException e1) {
					System.err.println("Report" + reportFile +" run failed.\n");
					System.err.println(e1.toString());
				}
			}
		}
		
		return false;
	}

	private IReportRunnable getReport(String reportPath) {
		try {
			return engine.openReportDesign(reportPath);
		} catch (EngineException e) {
			return null;
		}
	}

	private boolean reportExists(final String report) {
		boolean exists = true;
		
		if (report == null || report.isEmpty()) {
			exists = false;
		}
		else {
			File reportFile = new File(REPORTS_PATH + report);
			
			if (!reportFile.exists()) {
				exists = false;
			}
		}
		
		return exists;
	}
}
