package com.bravson.socialalert.business.file.media;

import java.io.File;
import java.io.IOException;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;

public interface MediaFileProcessor {
	
	void createPreview(File inputFile, File outputFile) throws IOException;
	
	MediaFileFormat getPreviewFormat();
	
	void createThumbnail(File inputFile, File outputFile) throws IOException;
	
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
			createPreview(inputFile, outputFile);
			return getPreviewFormat();
		case THUMBNAIL:
			createThumbnail(inputFile, outputFile);
			return getThumbnailFormat();
		case MEDIA:
		default:
			throw new IllegalArgumentException();
		}
	}
}
