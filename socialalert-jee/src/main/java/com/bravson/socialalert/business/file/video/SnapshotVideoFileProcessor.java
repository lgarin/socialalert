package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.layer.Service;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class SnapshotVideoFileProcessor extends BaseVideoFileProcessor {

	@Inject
	public SnapshotVideoFileProcessor(@NonNull MediaConfiguration config) {
		super(config);
	}
	
	@Override
	public void createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		takeSnapshot(sourceFile, outputFile, config.previewWidth(), config.previewHeight(), false);
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
