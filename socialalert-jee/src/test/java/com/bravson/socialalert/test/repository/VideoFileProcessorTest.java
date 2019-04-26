package com.bravson.socialalert.test.repository;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.business.file.video.VideoFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class VideoFileProcessorTest extends Assertions {

	private static MediaConfiguration config = MediaConfiguration
			.builder()
			.previewHeight(640)
			.previewWidth(960)
			.thumbnailHeight(320)
			.thumbnailWidth(480)
			.watermarkFile("C:\\Dev\\logo.jpg")
			.videoEncodingProgram("C:\\Dev\\ffmpeg.exe")
			.build(); 
	
	private VideoFileProcessor processor = new VideoFileProcessor(config);
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_MP4);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	@Test
	public void extractMovMetadata() throws Exception {
		MediaMetadata metadata = processor.parseMetadata(new File("src/test/resources/media/IMG_0236.MOV"));
		assertThat(metadata.getTimestamp()).isEqualTo(LocalDateTime.of(2015, 1, 7, 21, 13, 32).toInstant(ZoneOffset.UTC));
		assertThat(metadata.getCameraMaker()).isEqualTo("Apple");
		assertThat(metadata.getCameraModel()).isEqualTo("iPhone 6");
		assertThat(metadata.getHeight()).isEqualTo(320);
		assertThat(metadata.getWidth()).isEqualTo(568);
		assertThat(metadata.getDuration()).isEqualTo(Duration.ofSeconds(23));
		assertThat(metadata.getLatitude()).isEqualTo(43.3222);
		assertThat(metadata.getLongitude()).isEqualTo(-1.9949);
	}

	@Test
	public void createMovThumbnail() throws Exception {
		File file = File.createTempFile("mov", "thumbnail.jpg");
		processor.createThumbnail(new File("src/test/resources/media/IMG_0236.MOV"), file);
		MediaMetadata metadata = new PictureFileProcessor(config).parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getThumbnailHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getThumbnailWidth());
	}
	
	@Test
	public void createMovPreview() throws Exception {
		File file = File.createTempFile("mov", "preview.mp4");
		processor.createPreview(new File("src/test/resources/media/IMG_0236.MOV"), file);
		MediaMetadata metadata = processor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getPreviewHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getPreviewWidth());
	}
}
