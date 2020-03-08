package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.layer.Service;

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
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		takeSnapshot(sourceFile, outputFile, config.getPreviewWidth(), config.getPreviewHeight(), true);
		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
