package com.bshg.plc.component.report.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.ReportUtils;

@Service
public class ResourceServiceImpl implements ResourceService {
	@Override
	public String createTempFolder() throws FileNotFoundException {
		String uuid = createTemporaryFolder();

		if (uuid == null) {
			throw new FileNotFoundException("Folder '" + uuid + "' could not be created");
		}

		return uuid;
	}

	@Override
	public List<ReportAsset> uploadMultipartFiles(final String uuid, final List<MultipartFile> files) throws Exception {
		List<ReportAsset> assetList = new ArrayList<ReportAsset>();

		if (files != null) {
			String filePath = Constants.REPORT_TEMP_UPLOAD_PATH + uuid + "/";

			for (MultipartFile file : files) {
				String fileUUID = UUID.randomUUID().toString();
				String fileName = fileUUID + "." + ReportUtils.getFileExtension(file.getOriginalFilename());
				
				try {
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
