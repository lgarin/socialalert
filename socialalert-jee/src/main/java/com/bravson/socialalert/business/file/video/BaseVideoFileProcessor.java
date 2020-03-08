package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.util.ProcessUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class BaseVideoFileProcessor implements MediaFileProcessor {

	protected MediaConfiguration config;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected BaseVideoFileProcessor(@NonNull MediaConfiguration config) {
		this.config = config;
	}
	
	protected File takeSnapshot(File sourceFile, File targetFile, int width, int height, boolean watermark) throws IOException {
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		
		String filter = String.format("[0] thumbnail,scale=(iw*sar)*max(%1$d/(iw*sar)\\,%2$d/ih):ih*max(%1$d/(iw*sar)\\,%2$d/ih),crop=%1$d:%2$d [thumbnail]; ", width, height);
		if (watermark) {
			filter += "[1] format=yuva420p,lutrgb='a=128' [watermark]; [thumbnail][watermark] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2'";
		} else {
			filter += "[thumbnail] null";
		}
		
		List<String> arguments = Arrays.asList(
				"-i", sourceFile.getAbsolutePath(), 
				"-i", config.getWatermarkFile(), 
				"-f", "image2", "-frames:v", "1", 
				"-filter_complex", filter,
				"-y", targetFile.getAbsolutePath());
		
		StringBuilder output = new StringBuilder(16000);
		int exitCode = ProcessUtil.execute(config.getEncodingProgram(), arguments, output);
		if (exitCode != 0) {
			logger.error(output.toString());
			throw new IOException("Cannot process file " + targetFile);
		}

		return targetFile;
	}

	
	@Override
	public MediaFileFormat createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		takeSnapshot(sourceFile, outputFile, config.getThumbnailWidth(), config.getThumbnailHeight(), false);
		return getThumbnailFormat();
	}
}
