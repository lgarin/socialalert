package com.bravson.socialalert.file.video;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;

import java.io.File;
import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@ManagedBean
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class SnapshotVideoFileProcessor extends BaseVideoFileProcessor {

	@Inject
	public SnapshotVideoFileProcessor(@NonNull MediaConfiguration config, @NonNull SnapshotCache snapshotCache) {
		super(config, snapshotCache);
	}
	
	@Override
	public void createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(getSnapshot(sourceFile))
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getPreviewWidth(), config.getPreviewHeight())
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}}
