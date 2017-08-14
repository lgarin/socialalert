package com.bravson.socialalert.file.media;

import java.io.File;
import java.io.IOException;

public interface MediaFileProcessor {

	MediaMetadata parseMetadata(File inputFile) throws Exception;
	
	MediaFileFormat createPreview(File inputFile, File outputFile) throws IOException;
	
	MediaFileFormat getPreviewFormat();
	
	MediaFileFormat createThumbnail(File inputFile, File outputFile) throws IOException;
	
	default MediaFileFormat getThumbnailFormat() {
		return MediaFileFormat.THUMBNAIL_JPG;
	}
	
	default MediaFileFormat getFormat(MediaSizeVariant sizeVariant) {
		switch (sizeVariant) {
		case PREVIEW:
			return getPreviewFormat();
		case THUMBNAIL:
			return getThumbnailFormat();
		case MEDIA:
		default:
			throw new IllegalArgumentException();
		}
	}
	
	default MediaFileFormat createVariant(File inputFile, File outputFile, MediaSizeVariant sizeVariant) throws IOException {
		switch (sizeVariant) {
		case PREVIEW:
			return createPreview(inputFile, outputFile);
		case THUMBNAIL:
			return createThumbnail(inputFile, outputFile);
		case MEDIA:
		default:
			throw new IllegalArgumentException();
		}
	}
}
