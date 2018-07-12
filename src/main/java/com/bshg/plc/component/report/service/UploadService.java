package com.bshg.plc.component.report.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
	public String createUniqueFolder() throws FileNotFoundException;
	public Map<String, String> uploadFiles(final List<MultipartFile> files, final String uuid) throws Exception;
}
