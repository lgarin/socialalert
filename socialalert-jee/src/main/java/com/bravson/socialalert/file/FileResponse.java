package com.bravson.socialalert.file;

import java.io.File;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileResponse {

	@NonNull
	private File file;
	
	@NonNull
	private String contentType;
	
	@NonNull
	private String etag;
	
	private Integer maxAge;
}
