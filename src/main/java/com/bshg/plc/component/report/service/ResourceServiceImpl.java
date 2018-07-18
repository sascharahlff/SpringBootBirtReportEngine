package com.bshg.plc.component.report.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.ReportUtils;

@Service
public class ResourceServiceImpl implements ResourceService {
	@Override
	public String createTempFolder() throws FileNotFoundException {
		// Create temporary folder
		String uuid = createTemporaryFolder();

		if (uuid == null) {
			throw new FileNotFoundException("Folder '" + uuid + "' could not be created");
		}

		return uuid;
	}

	@Override
	public List<ReportAsset> uploadMultipartFiles(final String uuid, final List<MultipartFile> files) throws Exception {
		// Return list of uploaded files with unique file names
		List<ReportAsset> assetList = new ArrayList<ReportAsset>();

		if (files != null) {
			// Path to temporary folder
			String filePath = Constants.REPORT_TEMP_UPLOAD_PATH + uuid + "/";

			// Do multipart file upload
			for (MultipartFile file : files) {
				// Create unique file name
				String fileUUID = UUID.randomUUID().toString();
				String fileName = fileUUID + "." + ReportUtils.getFileExtension(file.getOriginalFilename());

				try {
					// Upload file in temporary folder
					file.transferTo(new File(filePath + fileName));
				} catch (Exception e) {
					throw new Exception("Error writing multipart file to directory.");
				}

				assetList.add(new ReportAsset(file.getOriginalFilename(), fileUUID));
			}
		}

		return assetList;
	}

	@Override
	public boolean uploadDataXml(final String uuid, final MultipartFile file) throws Exception {
		if (file != null) {
			try {
				// Upload xml file as "data.xml" in temporary folder
				String filePath = Constants.REPORT_TEMP_UPLOAD_PATH + uuid + "/" + Constants.XML_FILE_NAME;
				file.transferTo(new File(filePath));
			} catch (Exception e) {
				throw new Exception("Error writing multipart file to directory.");
			}
		}

		return true;
	}

	@Override
	public boolean removeTemporaryFolder(String uuid) {
		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

		// Remove temoporary folder and files
		if (folder.exists()) {
			return FileUtils.deleteQuietly(folder);
		}

		return false;
	}

	private String createTemporaryFolder() {
		String uuid = UUID.randomUUID().toString();
		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

		try {
			if (!folder.exists()) {
				boolean created = folder.mkdir();

				if (created) {
					return uuid;
				}
			} else {
				return uuid;
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}
}
