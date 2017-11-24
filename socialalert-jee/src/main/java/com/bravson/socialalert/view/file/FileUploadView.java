package com.bravson.socialalert.view.file;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.media.MediaFileConstants;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;

import lombok.Getter;

@Model
public class FileUploadView implements Serializable, MediaFileConstants {
	
	private static final long serialVersionUID = 1L;
	
	private static final String FILE_PATTERN ="/\\.(" + Stream.of(JPG_EXTENSION, JPG_ALT_EXTENSIONS, MOV_EXTENSION, MP4_EXTENSION).collect(Collectors.joining("|")) + ")$/i";
		
	@Resource(name="maxUploadSize")
	@Getter
	long maxUploadSize;

	@Inject
	UserAccess userAccess;
	
	@Inject
	FileUploadService uploadService;
	
	@Inject
	FileSearchService searchService;
	
	@Getter
	List<FileInfo> newFiles;
	
	@PostConstruct
	void initNewFiles() {
		newFiles = searchService.findNewFilesByUserId(userAccess.getUserId());
		newFiles.sort(Comparator.comparing(FileInfo::getTimestamp));
	}
	
	public String getFilePattern() {
		return FILE_PATTERN;
	}
	
	public void handleFileUpload(FileUploadEvent event) throws IOException {
		UploadedFile upload = event.getFile();
		File tempFile = File.createTempFile("upload-", "tmp");
		try {
			Files.copy(upload.getInputstream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			FileInfo newFile = uploadService.uploadMedia(new FileUploadParameter(tempFile, upload.getContentType()), userAccess);
			newFiles.add(newFile);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not upload file", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		} finally {
			tempFile.delete();
		}
    }
}
