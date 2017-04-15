package com.bravson.socialalert.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.video.VideoFileProcessor;

public class VideoFileProcessorTest extends Assertions {

	private static MediaConfiguration createConfig() {
		return MediaConfiguration.builder().previewHeight(640).previewWidth(960).watermarkFile("logo.jpg").videoLibraryPath("C:\\Dev").build();
	}
	
	VideoFileProcessor processor = new VideoFileProcessor(createConfig());
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_MP4);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
}
