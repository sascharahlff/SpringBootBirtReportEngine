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
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
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
			URL url = classLoader.getResource("reports/new.rptdesign");
			URL xmlUrl = classLoader.getResource("assets/books2.xml");
			URL outputUrl = classLoader.getResource("reports/");
//			
//			ClassLoader cl = getClass().getClassLoader();
//			File file = new File(cl.getResource("assets/books2.xml").getFile());
			
			
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder;
//			try {
//				builder = factory.newDocumentBuilder();
//				Document document = builder.parse(file);
//				document.getDocumentElement().normalize();
//				System.out.println("test");
//			} catch (ParserConfigurationException | SAXException | IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}


			
//			File f = new File("/main/resources/assets/books2.xml");
//			
//			String xmlStr = "<message>HELLO!</message>";
//			DocumentBuilder db;
//			try {
//				db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//				Document doc = db.parse(new InputSource(new StringReader(xmlStr)));
//				System.out.println(doc.getFirstChild().getNodeValue());
//			} catch (ParserConfigurationException | SAXException | IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
			
			
			
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		    DocumentBuilder dBuilder;
//			try {
//				dBuilder = dbFactory.newDocumentBuilder();
//				Document doc = dBuilder.parse(file);
//				System.out.println(doc.toString());
//			} catch (ParserConfigurationException e1) {
//				e1.printStackTrace();
//			} catch (SAXException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		    
//			try {
//				StringBuilder result = new StringBuilder("");
//				File file = new File(cl.getResource("assets/books2.xml").getFile());
//				try (Scanner scanner = new Scanner(file)) {
//
//					while (scanner.hasNextLine()) {
//						String line = scanner.nextLine();
//						result.append(line).append("\n");
//					}
//
//					scanner.close();
//
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				System.out.println(result.toString());
//				
//			} catch (Exception e) {
//				System.out.println(e);
//			}

			// Render to pdf
			IReportRunnable design = engine.openReportDesign(url.getFile());
			RenderOption renderOption = new PDFRenderOption();
			renderOption.setOutputFormat("pdf");
			renderOption.setOutputFileName(outputUrl.getFile() + "blub.pdf");
			
			ReportDesignHandle report = (ReportDesignHandle)design.getDesignHandle();
			try {
				((OdaDataSourceHandle)report.getDataSources().get(0)).setProperty("FILELIST", xmlUrl.getFile());
			} catch (SemanticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			BufferedReader reader;
//
//			try {
//				reader = new BufferedReader(new FileReader(f.getPath()));
//				// Set parameters to prefill PDF
//				params.put("firstName", "Biff Tannen");
//				params.put("sourcexml", reader.toString());
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstName", "Biff Tannen");

			String xmlString = "<library><name>Biff Tannen</name><data><book category='FOO'><title lang='en'>BIRT ist kein Geschenk</title><author name='Sascha Rahlff' country='it'/><year>2005</year></book><book category='BAR'><title lang='en'>BIRT suxx ...</title><author name='Biff Tannen' country='uk' /><year>2005</year></book><book category='BLUB'><title lang='en'>BIRT kann XML Datasets</title><author name='James McGovern' country='us' /></book></data></library>";
			params.put("sourcexml", "<library></library>");
			// Render task
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
//			Map appContext = task.getAppContext();
//			try {
//				FileInputStream fileInputStream = new FileInputStream(xmlUrl.getFile());
//				appContext.put("org.eclipse.datatools.enablement.oda.xml.inputStream", fileInputStream);
//				appContext.put("org.eclipse.datatools.enablement.oda.xml.closeInputStream", "true");
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			task.setRenderOption(renderOption);
			//task.setParameterValues(params);
			//task.validateParameters();
			
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
