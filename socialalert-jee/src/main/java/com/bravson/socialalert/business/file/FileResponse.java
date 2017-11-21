package com.bravson.socialalert.business.file;

import java.io.File;

import com.bravson.socialalert.business.file.media.MediaFileFormat;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class FileResponse {

	@NonNull
	private File file;
	
	@NonNull
	private MediaFileFormat format;
	
	private boolean temporary;
}
