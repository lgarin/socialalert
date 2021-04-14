package com.bravson.socialalert.business.file.exchange;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public final class FileUploadParameter {

	@NonNull 
	private File inputFile;
	
	@NonNull 
	private String contentType;
}
