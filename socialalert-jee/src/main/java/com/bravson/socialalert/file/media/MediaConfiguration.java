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
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class MediaConfiguration {

	@Resource(name="videoSnapshotDelay")
	private long snapshotDelay;
	
	@Resource(name="mediaThumbnailHeight")
	private int thumbnailHeight;
	
	@Resource(name="mediaThumbnailWidth")
	private int thumbnailWidth;
	
	@Resource(name="mediaPreviewHeight")
	private int previewHeight;
	
	@Resource(name="mediaPreviewWidth")
	private int previewWidth;
	
	@Resource(name="mediaWatermarkFile")
	private String watermarkFile;
	
	@Resource(name="videoLibraryPath")
	private String videoLibraryPath;
}
