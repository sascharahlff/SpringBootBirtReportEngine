package com.bshg.plc.component.report.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bshg.plc.component.report.constants.Constants;

@Service
public class ReportServiceImpl implements ReportService {
	// Value from application.yml
	@Value("${report.input}")
	private String REPORT_PATH;
	
	private IReportEngine engine = null;

	@PostConstruct
	public void init() throws BirtException {
		// Start BIRT report engine
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.setLogConfig(Constants.BIRT_LOG_PATH, Level.WARNING);

		Platform.startup(engineConfig);
		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		engine = factory.createReportEngine(engineConfig);
		engine.changeLogLevel(Level.WARNING);
	}

	@PreDestroy
	public void destroy() {
		// Stop and destroy BIRT report engine
		engine.destroy();
		Platform.shutdown();
	}

	@Override
	@SuppressWarnings("unchecked")
	public byte[] createReport(String uuid) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String reportPath = REPORT_PATH + "bsh.rptdesign";

		// Get report design
		IReportRunnable reportDesign = getReportDesign(reportPath);

		if (reportDesign == null) {
			throw new FileNotFoundException("Report Design does not exist");
		}
		else {
			// Create task to render the report
			IRunAndRenderTask task = engine.createRunAndRenderTask(reportDesign);
			String path = Constants.REPORT_TEMP_UPLOAD_PATH + uuid + "/" + Constants.XML_FILE_NAME;

			// Read data.xml from temporary folder
			byte[] encoded = Files.readAllBytes(Paths.get(path));

			// Override XML structure in report
			ByteArrayInputStream byteArray = new ByteArrayInputStream(encoded);
			Map<String, ByteArrayInputStream> appContext = task.getAppContext();
			// Set report data source via byte array
			appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", byteArray);

			// Set PDF file format and output format
			PDFRenderOption renderOption = new PDFRenderOption();
			renderOption.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
			renderOption.setOutputStream(outputStream);

			// Run task with render options
			task.setRenderOption(renderOption);
			task.run();
			task.close();
		}

		return outputStream.toByteArray();
	}

	private IReportRunnable getReportDesign(String reportPath) {
		try {
			return engine.openReportDesign(reportPath);
		} catch (EngineException e) {
			return null;
		}
	}
}