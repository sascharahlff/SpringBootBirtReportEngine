package com.bshg.plc.component.report.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.constants.Constants;

@Service
public class UploadServiceImpl implements UploadService {
	@Override
	public String createUniqueFolder() throws FileNotFoundException {
		String uuid = createTempFolder();

		if (uuid == null) {
			throw new FileNotFoundException("Folder '" + uuid + "' could not be created");
		}
		
		return uuid;
	}

	@Override
	public Map<String, String> uploadFiles(final List<MultipartFile> files, final String uuid) throws Exception {
		Map<String, String> fileMap = new HashMap<String, String>();
		
		if (files != null) {
			String fileName = "";
					
			for (MultipartFile mFile : files) {
				fileName = getUniqueFileName(mFile);
				
				if (!fileName.isEmpty()) {
					File file = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid + "/" + fileName);
					FileOutputStream fos = null;
					
					try {
						fos = new FileOutputStream(file);
						fos.write(mFile.getBytes());
					}
					catch (Exception e) {
						throw new Exception("Error writing multipart file to directory.");
					}
					finally {
						try {
							fos.close();
							fileMap.put(mFile.getOriginalFilename(), fileName);
						} catch (IOException e) {
							throw new Exception("Error writing multipart file to directory.");
						}
					}
				}
				else {
					throw new Exception("Error extracting file extension from uploaded file.");
				}
			}
		}
		
		return fileMap;
	}

	private String createTempFolder() {
		String uuid = UUID.randomUUID().toString();
		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

		try {
			if (!folder.exists()) {
				boolean created = folder.mkdir();
				
				if (created) {
					return uuid;
				}
			}

		} catch (Exception e) {
		}

		return null;
	}
	
	private String getUniqueFileName(MultipartFile file) {
		String uuid = UUID.randomUUID().toString();
		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
		
		if (!fileExtension.isEmpty()) {
			return uuid +"."+ fileExtension;
		}
		
		return "";
	}
}
