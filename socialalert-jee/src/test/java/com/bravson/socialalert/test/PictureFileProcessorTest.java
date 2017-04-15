package com.bravson.socialalert.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.picture.PictureFileProcessor;

public class PictureFileProcessorTest extends Assertions {

	private static MediaConfiguration config = MediaConfiguration.builder().previewHeight(640).previewWidth(960).watermarkFile("logo.jpg").build(); 
	
	private PictureFileProcessor processor = new PictureFileProcessor(config);
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_JPG);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
}
