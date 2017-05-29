package com.bravson.socialalert.file.media;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_MEDIA_TYPE;
import static com.bravson.socialalert.file.media.MediaFileConstants.MOV_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MOV_MEDIA_TYPE;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_MEDIA_TYPE;

import java.util.EnumSet;
import java.util.Optional;

import com.bravson.socialalert.file.store.FileFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public enum MediaFileFormat implements FileFormat, Comparable<MediaFileFormat> {

	// values are sorted by worst to best quality
	THUMBNAIL_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, MediaSizeVariant.THUMBNAIL),
	PREVIEW_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, MediaSizeVariant.PREVIEW),
	PREVIEW_MP4(MP4_MEDIA_TYPE, "." + MP4_EXTENSION, MediaSizeVariant.PREVIEW),
	MEDIA_JPG(JPG_MEDIA_TYPE, "." + JPG_EXTENSION, MediaSizeVariant.MEDIA),
	MEDIA_MOV(MOV_MEDIA_TYPE, "." + MOV_EXTENSION, MediaSizeVariant.MEDIA),
	MEDIA_MP4(MP4_MEDIA_TYPE, "." + MP4_EXTENSION, MediaSizeVariant.MEDIA);
	
	private static EnumSet<MediaFileFormat> MEDIA_SET = EnumSet.of(MEDIA_MOV, MEDIA_MP4, MEDIA_JPG);
	
	public static EnumSet<MediaFileFormat> VIDEO_SET = EnumSet.of(MEDIA_MOV, MEDIA_MP4, PREVIEW_MP4);
	
	public static Optional<MediaFileFormat> fromMediaContentType(@NonNull String contentType) {
		return MEDIA_SET.stream().filter(f -> f.getContentType().equals(contentType)).findAny();
	}
	
	@NonNull
	private final String contentType;
	
	@NonNull
	private final String extension;
	
	@NonNull
	private final MediaSizeVariant mediaSizeVariant;
	
	@Override
	public String getSizeVariant() {
		return mediaSizeVariant.getVariantName();
	}
}
