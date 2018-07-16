package com.bshg.plc.component.report.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.domain.ReportAsset;

public interface ResourceService {
	public String createTempFolder() throws FileNotFoundException;
	public List<ReportAsset> uploadMultipartFiles(List<MultipartFile> files, String uuid) throws Exception;
	public boolean uploadDataXml(MultipartFile file, String uuid) throws Exception;
	public boolean removeTemporaryFolder(String uuid);
}
