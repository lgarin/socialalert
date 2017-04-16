package com.bravson.socialalert.test.repository;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.video.VideoFileProcessor;
import com.drew.imaging.jpeg.JpegProcessingException;

import lombok.val;

public class VideoFileProcessorTest extends Assertions {

	private static MediaConfiguration config = MediaConfiguration
			.builder()
			.previewHeight(640)
			.previewWidth(960)
			.thumbnailHeight(320)
			.thumbnailWidth(480)
			.watermarkFile("logo.jpg")
			.videoLibraryPath("C:\\Dev")
			.build(); 
	
	private VideoFileProcessor processor = new VideoFileProcessor(config);
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_MP4);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	@Test
	public void extractMovMetadata() throws IOException {
		val metadata = processor.parseMetadata(new File("src/test/resources/media/IMG_0236.MOV"));
		assertThat(metadata.getTimestamp()).isEqualTo(LocalDateTime.of(2015, 1, 7, 21, 13, 32).atOffset(ZoneOffset.UTC).toInstant());
		assertThat(metadata.getCameraMaker()).isEqualTo("Apple");
		assertThat(metadata.getCameraModel()).isEqualTo("iPhone 6");
		assertThat(metadata.getHeight()).isEqualTo(320);
		assertThat(metadata.getWidth()).isEqualTo(568);
		assertThat(metadata.getDuration()).isEqualTo(Duration.ofSeconds(23));
		assertThat(metadata.getLatitude()).isEqualTo(43.3222);
		assertThat(metadata.getLongitude()).isEqualTo(-1.9949);
	}

	@Test
	public void createMovThumbnail() throws IOException, JpegProcessingException {
		val file = File.createTempFile("mov", "thumbnail.jpg");
		processor.createThumbnail(new File("src/test/resources/media/IMG_0236.MOV"), file);
		val metadata = new PictureFileProcessor(config).parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getThumbnailHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getThumbnailWidth());
	}
	
	@Test
	public void createMovPreview() throws IOException {
		val file = File.createTempFile("mov", "preview.mp4");
		processor.createPreview(new File("src/test/resources/media/IMG_0236.MOV"), file);
		val metadata = processor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getPreviewHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getPreviewWidth());
	}
}
