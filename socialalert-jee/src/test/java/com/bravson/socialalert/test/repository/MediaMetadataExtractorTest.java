package com.bravson.socialalert.test.repository;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaMetadataExtractorTest extends Assertions {

	@Inject
	MediaMetadataExtractor extractor;
	
	
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


	@Test
	public void extractJpgMetadata() throws Exception {
		MediaMetadata metadata = extractor.parseMetadata(new File("src/test/resources/media/IMG_0397.JPG"));
		assertThat(metadata.getTimestamp()).isEqualTo(LocalDateTime.of(2013, 4, 14, 16, 28, 26, 0).toInstant(ZoneOffset.UTC));
		assertThat(metadata.getCameraMaker()).isEqualTo("Apple");
		assertThat(metadata.getCameraModel()).isEqualTo("iPhone 5");
		assertThat(metadata.getWidth()).isEqualTo(2448);
		assertThat(metadata.getHeight()).isEqualTo(3264);
		assertThat(metadata.getLatitude()).isEqualTo(46.6866666666667);
		assertThat(metadata.getLongitude()).isEqualTo(7.85883333333333);
		assertThat(metadata.getDuration()).isNull();
	}
}
