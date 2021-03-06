package com.bravson.socialalert.domain.media.format;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MediaSizeVariant {
	
	MEDIA(MediaFileConstants.MEDIA_VARIANT),
	PREVIEW(MediaFileConstants.PREVIEW_VARIANT),
	THUMBNAIL(MediaFileConstants.THUMBNAIL_VARIANT);
	
	@Getter
	private final String variantName;
}
