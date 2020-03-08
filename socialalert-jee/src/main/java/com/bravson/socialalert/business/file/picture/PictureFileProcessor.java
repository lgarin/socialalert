package com.bravson.socialalert.business.file.picture;

import static com.bravson.socialalert.domain.media.format.MediaFileConstants.JPG_EXTENSION;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.business.file.media.MediaUtil;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class PictureFileProcessor implements MediaFileProcessor {
	
	private MediaConfiguration config;
	
	private BufferedImage watermarkImage;
	
	@Inject
	public PictureFileProcessor(@NonNull MediaConfiguration config) {
		this.config = config;
		watermarkImage = MediaUtil.readImage(config.getWatermarkFile());
	}

	@Override
	public MediaFileFormat createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.size(config.getThumbnailWidth(), config.getThumbnailHeight())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
		return getThumbnailFormat();
	}
	
	@Override
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getPreviewWidth(), config.getPreviewHeight())
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
