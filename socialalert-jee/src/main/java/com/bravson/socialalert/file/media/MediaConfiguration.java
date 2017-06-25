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
	long snapshotDelay;
	
	@Resource(name="mediaThumbnailHeight")
	int thumbnailHeight;
	
	@Resource(name="mediaThumbnailWidth")
	int thumbnailWidth;
	
	@Resource(name="mediaPreviewHeight")
	int previewHeight;
	
	@Resource(name="mediaPreviewWidth")
	int previewWidth;
	
	@Resource(name="mediaWatermarkFile")
	String watermarkFile;
	
	@Resource(name="videoLibraryPath")
	String videoLibraryPath;
}
