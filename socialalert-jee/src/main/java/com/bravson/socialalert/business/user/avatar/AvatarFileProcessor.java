package com.bravson.socialalert.business.user.avatar;

import static com.bravson.socialalert.domain.media.format.MediaFileConstants.JPG_EXTENSION;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class AvatarFileProcessor implements MediaFileProcessor {

	@Inject
	AvatarConfiguration config;
	
	@Override
	public void createPreview(File inputFile, File outputFile) throws IOException {
		Thumbnails
			.of(inputFile)
			.size(config.getLargeSize(), config.getLargeSize())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
	}

	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}

	@Override
	public void createThumbnail(File inputFile, File outputFile) throws IOException {
		Thumbnails
			.of(inputFile)
			.size(config.getSmallSize(), config.getSmallSize())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
	}

}
