package com.bravson.socialalert.test.repository;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;

public class MediaMetadataExtractorTest extends Assertions {

	private static MediaConfiguration config = MediaConfiguration
			.builder()
			.previewHeight(640)
			.previewWidth(960)
			.thumbnailHeight(320)
			.thumbnailWidth(480)
			.watermarkFile("C:\\Dev\\logo.jpg")
			.videoEncodingProgram("C:\\Dev\\ffmpeg.exe")
			.metadataExtractorProgram("C:\\Dev\\exiftool.exe")
			.build(); 
	
	private MediaMetadataExtractor extractor = new MediaMetadataExtractor(config);
	
	
	@Test
	public void extractMovMetadata() throws Exception {
		MediaMetadata metadata = extractor.parseMetadata(new File("src/test/resources/media/IMG_0236.MOV"));
		assertThat(metadata.getTimestamp()).isEqualTo(LocalDateTime.of(2014, 12, 28, 14, 21, 49).toInstant(ZoneOffset.UTC));
		assertThat(metadata.getCameraMaker()).isEqualTo("Apple");
		assertThat(metadata.getCameraModel()).isEqualTo("iPhone 6");
		assertThat(metadata.getHeight()).isEqualTo(320);
		assertThat(metadata.getWidth()).isEqualTo(568);
		assertThat(metadata.getDuration()).isEqualTo(Duration.ofSeconds(23, 428 * 1_000_000));
		assertThat(metadata.getLatitude()).isEqualTo(43.3222);
		assertThat(metadata.getLongitude()).isEqualTo(-1.9949);
	}


}
