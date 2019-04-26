package com.bravson.socialalert.test.repository;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.drew.imaging.jpeg.JpegProcessingException;

public class PictureFileProcessorTest extends Assertions {

	private static MediaConfiguration config = MediaConfiguration
			.builder()
			.previewHeight(640)
			.previewWidth(960)
			.thumbnailHeight(320)
			.thumbnailWidth(480)
			.watermarkFile("C:\\Dev\\logo.jpg")
			.build();  
	
	private PictureFileProcessor processor = new PictureFileProcessor(config);
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_JPG);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	@Test
	public void extractJpgMetadata() throws IOException, JpegProcessingException {
		MediaMetadata metadata = processor.parseMetadata(new File("src/test/resources/media/IMG_0397.JPG"));
		assertThat(metadata.getTimestamp()).isEqualTo(OffsetDateTime.of(2013, 4, 14, 16, 28, 26, 0, ZoneOffset.UTC).toInstant());
		assertThat(metadata.getCameraMaker()).isEqualTo("Apple");
		assertThat(metadata.getCameraModel()).isEqualTo("iPhone 5");
		assertThat(metadata.getWidth()).isEqualTo(2448);
		assertThat(metadata.getHeight()).isEqualTo(3264);
		assertThat(metadata.getLatitude()).isEqualTo(46.68666666666667);
		assertThat(metadata.getLongitude()).isEqualTo(7.858833333333333);
		assertThat(metadata.getDuration()).isNull();
	}
	
	@Test
	public void createJpgThumbnail() throws IOException, JpegProcessingException {
		File file = File.createTempFile("jpg", "thumbnail.jpg");
		processor.createThumbnail(new File("src/test/resources/media/IMG_0397.JPG"), file);
		MediaMetadata metadata = processor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getThumbnailHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getThumbnailWidth());
	}
	
	@Test
	public void createJpgPreview() throws IOException, JpegProcessingException {
		File file = File.createTempFile("jpg", "preview.jpg");
		processor.createPreview(new File("src/test/resources/media/IMG_0397.JPG"), file);
		MediaMetadata metadata = processor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getPreviewHeight());
		//assertThat(metadata.getWidth()).isEqualTo(config.getPreviewWidth());
	}
}

