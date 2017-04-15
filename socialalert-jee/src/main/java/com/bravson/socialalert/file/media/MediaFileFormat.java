package com.bravson.socialalert.file.media;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_MEDIA_TYPE;
import static com.bravson.socialalert.file.media.MediaFileConstants.MEDIA_VARIANT;
import static com.bravson.socialalert.file.media.MediaFileConstants.MOV_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MOV_MEDIA_TYPE;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_MEDIA_TYPE;
import static com.bravson.socialalert.file.media.MediaFileConstants.PREVIEW_VARIANT;
import static com.bravson.socialalert.file.media.MediaFileConstants.THUMBNAIL_VARIANT;

import java.util.EnumSet;
import java.util.Optional;

import com.bravson.socialalert.file.store.FileFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public enum MediaFileFormat implements FileFormat {

	MEDIA_MOV(MOV_MEDIA_TYPE, "." + MOV_EXTENSION, MEDIA_VARIANT),
	MEDIA_MP4(MP4_MEDIA_TYPE, "." + MP4_EXTENSION, MEDIA_VARIANT),
	MEDIA_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, MEDIA_VARIANT),
	PREVIEW_MP4(MP4_MEDIA_TYPE, "." + MP4_EXTENSION, PREVIEW_VARIANT),
	PREVIEW_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, PREVIEW_VARIANT),
	THUMBNAIL_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, THUMBNAIL_VARIANT);
	
	private static EnumSet<MediaFileFormat> MEDIA_SET = EnumSet.of(MEDIA_MOV, MEDIA_MP4, MEDIA_JPG);
	
	public static Optional<MediaFileFormat> fromMediaContentType(String contentType) {
		return MEDIA_SET.stream().filter(f -> f.getContentType().equals(contentType)).findAny();
	}
	
	@Getter
	@NonNull
	private final String contentType;
	
	@Getter
	@NonNull
	private final String extension;
	
	@Getter
	@NonNull
	private final String sizeVariant;
}
