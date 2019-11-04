package com.bravson.socialalert.business.file.media;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "media")
public interface MediaConfiguration {

	long getSnapshotDelay();
	
	int getThumbnailHeight();
	
	int getThumbnailWidth();
	
	int getPreviewHeight();
	
	int getPreviewWidth();
	
	String getWatermarkFile();
	
	String getEncodingProgram();
	
	String getMetadataProgram();
}
