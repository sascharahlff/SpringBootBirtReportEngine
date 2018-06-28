package de.itemis.birt.report;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.springframework.stereotype.Component;

@Component
public class ReportEngine {
	@PostConstruct
	public void init() {
		System.out.println("startup");
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.setEngineHome("");
		engineConfig.setLogConfig("/repos/logs", Level.WARNING);

		try {
			Platform.startup(engineConfig);
		} catch (BirtException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void destroy() {
		System.out.println("destroy");
		Platform.shutdown();
	}
}
