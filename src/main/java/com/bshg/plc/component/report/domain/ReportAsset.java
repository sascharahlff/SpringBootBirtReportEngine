package com.bshg.plc.component.report.domain;

// JSON response item
public class ReportAsset {
	String origin = "";
	String uuid = "";

	// Jackson object mapper needs empty constructor
	public ReportAsset() {
		super();
	}
	
	public ReportAsset(final String origin, final String uuid) {
		this.origin = origin;
		this.uuid = uuid;
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
