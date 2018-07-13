package com.bshg.plc.component.report.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.domain.ReportAsset;

public interface UploadService {
	public String createTempFolder() throws FileNotFoundException;
	public List<ReportAsset> uploadMultipartFiles(final List<MultipartFile> files, final String uuid) throws Exception;
	public boolean removeTemporaryFolder(final String uuid);
}
