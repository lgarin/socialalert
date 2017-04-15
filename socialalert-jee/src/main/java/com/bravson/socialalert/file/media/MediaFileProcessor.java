package com.bravson.socialalert.file.media;

import java.io.File;
import java.io.IOException;

public interface MediaFileProcessor {

	MediaMetadata parseMetadata(File inputFile) throws Exception;
	
	void createPreview(File inputFile, File outputFile) throws IOException;
	
	MediaFileFormat getPreviewFormat();
	
	void createThumbnail(File inputFile, File outputFile) throws IOException;
	
	default MediaFileFormat getThumbnailFormat() {
		return MediaFileFormat.THUMBNAIL_JPG;
	}
}
