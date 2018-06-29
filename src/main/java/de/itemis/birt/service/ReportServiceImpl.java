package de.itemis.birt.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
	private static final String REPORTS_PATH = "src/main/resources/reports/";
	private IReportEngine engine = null;

	@PostConstruct
	public void init() {
		System.out.println("startup");
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.setLogConfig("logs", Level.WARNING);

		try {
			Platform.startup(engineConfig);

			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
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

	@Override
	public String createReport(String report, String xml) {
		if (reportExists(report)) {
			IReportRunnable reportDesign = getReport(REPORTS_PATH + report);
			
			if (reportDesign != null) {
				String outputFile = UUID.randomUUID().toString() +".pdf";
				IRunAndRenderTask task = engine.createRunAndRenderTask(reportDesign);
				
				ByteArrayInputStream byteArray = new ByteArrayInputStream(xml.getBytes());
				Map<String, ByteArrayInputStream> appContext = task.getAppContext();
				appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", byteArray);
				
				RenderOption renderOption = new PDFRenderOption();
				renderOption.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
				renderOption.setOutputFileName("reports/"+ outputFile);

				task.setRenderOption(renderOption);
				
				try {
					task.run();
					return outputFile;
				} catch (EngineException e1) {
					System.err.println("Report" + report +" run failed.\n");
					System.err.println(e1.toString());
				}
			}
		}
		
		return "";
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
