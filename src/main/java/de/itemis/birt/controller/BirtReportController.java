package de.itemis.birt.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.itemis.birt.service.ReportService;

@RestController
@RequestMapping("/report")
public class BirtReportController {
	public static final String COMPONENT_PATH = "/component";
	public static final String COMPONENT_UUID_PATH = "/component/{uuid}/resources";

	@Autowired
	ReportService reportService;

	@RequestMapping(value = COMPONENT_PATH, consumes = MediaType.ALL_VALUE, method = RequestMethod.POST)
	public ResponseEntity<Object> createTempFolder(HttpServletRequest request) throws FileNotFoundException {
		String uuid = UUID.randomUUID().toString();

		if (!createTempFolder(uuid)) {
			throw new FileNotFoundException("Folder '" + uuid + "' could not be created");
		}

		HttpHeaders responseHeader = getResponseHeader(uuid, request);
		return new ResponseEntity<>(responseHeader, HttpStatus.CREATED);
	}

	@PostMapping(value = COMPONENT_UUID_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadFile(@PathVariable String uuid, @RequestParam("files") List<MultipartFile> files) {
		for (MultipartFile mFile : files) {
			File file = new File("./reports/temp/" + uuid + "/" + mFile.getOriginalFilename());
			FileOutputStream fos = null;
			
			try {
				fos = new FileOutputStream(file);
				fos.write(mFile.getBytes());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private HttpHeaders getResponseHeader(final String uuid, final HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		URI location = URI.create(currentUrl + "/" + uuid + "/resources");
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.setLocation(location);

		return responseHeader;
	}

	private boolean createTempFolder(String uuid) {
		File folder = new File("reports/temp/" + uuid);

		try {
			if (!folder.exists()) {
				folder.mkdir();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}