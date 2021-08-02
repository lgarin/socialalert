package com.bravson.socialalert.business.file.media;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;

@ConfigMapping(prefix = "media", namingStrategy = NamingStrategy.VERBATIM)
public interface MediaConfiguration {

	long snapshotDelay();
	
	int thumbnailHeight();
	
	int thumbnailWidth();
	
	int previewHeight();
	
	int previewWidth();
	
	String watermarkFile();
	
	String encodingProgram();
	
	String metadataProgram();
}
