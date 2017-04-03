package com.bravson.socialalert.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.picture.PictureFileProcessor;

public class PictureFileProcessorTest extends Assertions {

	private static MediaConfiguration createConfig() {
		return MediaConfiguration.builder().previewHeight(640).previewWidth(960).watermarkFile("logo.jpg").build();
	}
	
	PictureFileProcessor processor = new PictureFileProcessor(createConfig());
	
	@Test
	public void testContentType() {
		assertThat(processor.getPreviewContentType()).isEqualTo("image/jpeg");
		assertThat(processor.getThumbnailContentType()).isEqualTo("image/jpeg");
	}
}
