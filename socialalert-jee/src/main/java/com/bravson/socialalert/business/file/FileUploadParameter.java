package com.bravson.socialalert.business.file;

import java.io.File;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public final class FileUploadParameter {

	@NonNull 
	private File inputFile;
	
	@NonNull 
	private String contentType;
}
