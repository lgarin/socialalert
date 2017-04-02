package com.bravson.socialalert.file.media;

import java.io.File;
import java.io.IOException;

public interface MediaFileProcessor {

	MediaMetadata parseMetadata(File inputFile) throws Exception;
	
	File createPreview(File inputFile) throws IOException;
	
	String getPreviewContentType();
	
	File createThumbnail(File inputFile) throws IOException;
	
	default String getThumbnailContentType() {
		return MediaFileConstants.JPG_MEDIA_TYPE;
	}
}
