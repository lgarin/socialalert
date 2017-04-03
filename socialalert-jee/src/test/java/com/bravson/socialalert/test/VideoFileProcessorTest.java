package com.bravson.socialalert.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.video.VideoFileProcessor;

public class VideoFileProcessorTest extends Assertions {

	private static MediaConfiguration createConfig() {
		return MediaConfiguration.builder().previewHeight(640).previewWidth(960).watermarkFile("logo.jpg").videoLibraryPath("C:\\Dev").build();
	}
	
	VideoFileProcessor processor = new VideoFileProcessor(createConfig());
	
	@Test
	public void testContentType() {
		assertThat(processor.getPreviewContentType()).isEqualTo("video/mp4");
		assertThat(processor.getThumbnailContentType()).isEqualTo("image/jpeg");
	}
}
