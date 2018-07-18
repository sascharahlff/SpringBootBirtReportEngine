package com.bshg.plc.component.report.utils;

import org.apache.commons.io.FilenameUtils;

public class ReportUtils {
	public static String getFileExtension(String fileName) {
		// Get file extension from origin
		String fileExtension = FilenameUtils.getExtension(fileName);

		if (!fileExtension.isEmpty()) {
			return fileExtension;
		}

		return "";
	}
}
