package com.bravson.socialalert.business.file.exchange;

import java.io.File;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class FileDownloadResponse {

	@NonNull
	private File file;
	
	@NonNull
	private MediaFileFormat format;
}
