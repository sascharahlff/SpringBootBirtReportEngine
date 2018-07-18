package com.bshg.plc.component.report.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.domain.ReportAsset;

public interface ResourceService {
	public String createTempFolder() throws FileNotFoundException;
	public List<ReportAsset> uploadMultipartFiles(String uuid, List<MultipartFile> files) throws Exception;
	public boolean uploadDataXml(String uuid, MultipartFile file) throws Exception;
	public boolean removeTemporaryFolder(String uuid);
}
