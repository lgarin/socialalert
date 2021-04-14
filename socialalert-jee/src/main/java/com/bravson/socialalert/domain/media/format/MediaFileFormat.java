package com.bravson.socialalert.domain.media.format;

import static com.bravson.socialalert.domain.media.format.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.domain.media.format.MediaFileConstants.JPG_MEDIA_TYPE;
import static com.bravson.socialalert.domain.media.format.MediaFileConstants.MOV_EXTENSION;
import static com.bravson.socialalert.domain.media.format.MediaFileConstants.MOV_MEDIA_TYPE;
import static com.bravson.socialalert.domain.media.format.MediaFileConstants.MP4_EXTENSION;
import static com.bravson.socialalert.domain.media.format.MediaFileConstants.MP4_MEDIA_TYPE;

import java.util.Optional;
import java.util.Set;

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
	
	private static final Set<MediaFileFormat> MEDIA_SET = Set.of(MEDIA_MOV, MEDIA_MP4, MEDIA_JPG);
	
	public static final Set<MediaFileFormat> VIDEO_SET = Set.of(MEDIA_MOV, MEDIA_MP4, PREVIEW_MP4);
	
	public static final Set<MediaFileFormat> PICTURE_SET = Set.of(MEDIA_JPG, PREVIEW_JPG, THUMBNAIL_JPG);
	
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
