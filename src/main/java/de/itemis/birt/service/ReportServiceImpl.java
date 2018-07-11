package de.itemis.birt.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
	private static final String LOG_PATH = "logs/";

	@Value("${report.input}")
	private String REPORT_PATH;

	@Value("${report.output}")
	private String OUTPUT_PATH;

	private IReportEngine engine = null;

	@PostConstruct
	public void init() {
		System.out.println("startup");
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.setLogConfig(LOG_PATH, Level.WARNING);

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

	@Override
	@SuppressWarnings("unchecked")
	public String createReport(String report, String xml) throws FileNotFoundException {
		String reportPath = REPORT_PATH + report;
		String outputFile = "";

		// File file = new
		// File("/Users/sascha/repos/SpringBootBirtReportEngine/src/main/resources/assets/bsh.xml");

		// byte[] encoded = null;
		// try {
		// encoded =
		// Files.readAllBytes(Paths.get("/Users/sascha/repos/SpringBootBirtReportEngine/src/main/resources/assets/bsh.xml"));
		// String s = new String(encoded, "UTF-8");
		// System.out.println(s);
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// Get report design
		IReportRunnable reportDesign = getReportDesign(reportPath);

		if (reportDesign == null) {
			throw new FileNotFoundException("Report Design '" + report + "' does not exist");
		} else {
			// Unique pdf name
			// TODO String fileName = UUID.randomUUID().toString() + "." + RenderOption.OUTPUT_FORMAT_PDF;
			String fileName = "result." + RenderOption.OUTPUT_FORMAT_PDF;
			IRunAndRenderTask task = engine.createRunAndRenderTask(reportDesign);

			// Override XML structure in report
			ByteArrayInputStream byteArray = new ByteArrayInputStream(xml.getBytes());
			//ByteArrayInputStream byteArray = new ByteArrayInputStream(encoded);
			Map<String, ByteArrayInputStream> appContext = task.getAppContext();
			appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", byteArray);

			// PDF name and location
			RenderOption renderOption = new PDFRenderOption();
			renderOption.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
			renderOption.setOutputFileName(OUTPUT_PATH + fileName);
			task.setRenderOption(renderOption);

			try {
				task.run();
				outputFile = fileName;
			} catch (EngineException e) {
				System.err.println("Report" + report + " run failed.\n");
				System.err.println(e.toString());
			}
		}

		return outputFile;
	}

	@Override
	public ResponseEntity<InputStreamResource> getReport(String report) throws FileNotFoundException {
		File reportFile = new File(OUTPUT_PATH + report);

		if (!reportFile.exists()) {
			throw new FileNotFoundException("Report '" + report + "' does not exist");
		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("content-disposition", "attachment; filename=" + report);

			InputStream reportStream = new FileInputStream(reportFile);

			return ResponseEntity.ok().headers(headers).contentLength(reportFile.length())
					.contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
					.body(new InputStreamResource(reportStream));
		}
	}

	private IReportRunnable getReportDesign(String reportPath) {
		try {
			return engine.openReportDesign(reportPath);
		} catch (EngineException e) {
			return null;
		}
	}
}