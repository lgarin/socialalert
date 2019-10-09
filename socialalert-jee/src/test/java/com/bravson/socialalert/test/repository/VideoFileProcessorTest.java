package com.bravson.socialalert.test.repository;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;
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
			.metadataExtractorProgram("C:\\Dev\\exiftool.exe")
			.build(); 
	
	private MediaMetadataExtractor extractor = new MediaMetadataExtractor(config);
	
	private VideoFileProcessor processor = new VideoFileProcessor(config);
	
	@Test
	public void testMediaFormat() {
		assertThat(processor.getPreviewFormat()).isEqualTo(MediaFileFormat.PREVIEW_MP4);
		assertThat(processor.getThumbnailFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	@Test
	public void createMovThumbnail() throws Exception {
		File file = File.createTempFile("mov", "thumbnail.jpg");
		processor.createThumbnail(new File("src/test/resources/media/IMG_0236.MOV"), file);
		MediaMetadata metadata = extractor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getThumbnailHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getThumbnailWidth());
	}
	
	@Test
	public void createMovPreview() throws Exception {
		File file = File.createTempFile("mov", "preview.mp4");
		processor.createPreview(new File("src/test/resources/media/IMG_0236.MOV"), file);
		MediaMetadata metadata = extractor.parseMetadata(file);
		assertThat(metadata.getHeight()).isEqualTo(config.getPreviewHeight());
		assertThat(metadata.getWidth()).isEqualTo(config.getPreviewWidth());
	}
}
