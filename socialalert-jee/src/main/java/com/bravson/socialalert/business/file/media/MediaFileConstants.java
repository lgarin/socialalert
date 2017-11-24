package com.bravson.socialalert.business.file.media;

import java.util.Optional;

import lombok.NonNull;

public interface MediaFileConstants {

	String PREVIEW_VARIANT = "preview";
	String THUMBNAIL_VARIANT = "thumbnail";
	String MEDIA_VARIANT = "media";
	
	String MOV_MEDIA_TYPE = "video/quicktime";
	String MP4_MEDIA_TYPE = "video/mp4";
	String JPG_MEDIA_TYPE = "image/jpeg";
	
	String JPG_EXTENSION = "jpg";
	String JPG_ALT_EXTENSIONS = "jpeg";
	String MP4_EXTENSION = "mp4";
	String MOV_EXTENSION = "mov";
	
	public static Optional<String> guessMediaType(@NonNull String filename) {
		filename = filename.toLowerCase();
		if (filename.endsWith(MOV_EXTENSION)) {
			return Optional.of(MOV_MEDIA_TYPE);
		} else if (filename.endsWith(MP4_EXTENSION)) {
			return Optional.of(MP4_MEDIA_TYPE);
		} else if (filename.endsWith(JPG_EXTENSION) || filename.endsWith(JPG_ALT_EXTENSIONS)) {
			return Optional.of(JPG_MEDIA_TYPE);
		}
		return Optional.empty();
	}
}
