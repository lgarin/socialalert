package com.bravson.socialalert.test.repository;

import java.io.File;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PictureFileProcessorTest extends Assertions {

	@Inject
	MediaConfiguration config;
	
	@Inject
	MediaMetadataExtractor extractor;
	
	@Inject
	PictureFileProcessor processor;
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_JPG);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	
	@Test
	public void createJpgThumbnail() throws Exception {
		File file = File.createTempFile("jpg", "thumbnail.jpg");
		processor.createThumbnail(new File("src/test/resources/media/IMG_0397.JPG"), file);
		MediaMetadata metadata = extractor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getThumbnailHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getThumbnailWidth());
	}
	
	@Test
	public void createJpgPreview() throws Exception {
		File file = File.createTempFile("jpg", "preview.jpg");
		processor.createPreview(new File("src/test/resources/media/IMG_0397.JPG"), file);
		MediaMetadata metadata = extractor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getPreviewHeight());
		//assertThat(metadata.getWidth()).isEqualTo(config.getPreviewWidth());
	}
}

