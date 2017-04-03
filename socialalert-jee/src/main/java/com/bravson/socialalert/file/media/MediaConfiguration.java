package com.bravson.socialalert.file.media;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ManagedBean
@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter(AccessLevel.NONE)
public class MediaConfiguration {

	@Resource(name="videoSnapshotDelay")
	private long snapshotDelay;
	
	@Resource(name="pictureThumbnailPrefix")
	private String thumbnailPrefix;
	
	@Resource(name="pictureThumbnailHeight")
	private int thumbnailHeight;
	
	@Resource(name="pictureThumbnailWidth")
	private int thumbnailWidth;
	
	@Resource(name="picturePreviewPrefix")
	private String previewPrefix;
	
	@Resource(name="picturePreviewHeight")
	private int previewHeight;
	
	@Resource(name="picturePreviewWidth")
	private int previewWidth;
	
	@Resource(name="pictureWatermarkFile")
	private String watermarkFile;
	
	@Resource(name="videoLibraryPath")
	private String videoLibraryPath;
}
