package com.bravson.socialalert.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.store.TempFileFormat;
import com.bravson.socialalert.business.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;

public class MediaFileStoreTest extends BaseServiceTest {

	@InjectMocks
	MediaFileStore mediaFileStore;
	
	@Mock
	FileStore fileStore;

	@Mock
	PictureFileProcessor pictureFileProcessor;
	
	@Mock
	SnapshotVideoFileProcessor videoFileProcessor;
	
	@Test
	public void buildFileMetadata() throws IOException {
		String md5 = "1234";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(fileStore.computeMd5Hex(inputFile)).thenReturn(md5);
		
		FileMetadata result = mediaFileStore.buildFileMetadata(inputFile, fileFormat);
		assertThat(result).isEqualTo(FileMetadata.builder().md5(md5).timestamp(result.getTimestamp()).fileFormat(fileFormat).contentSize(2_100_375L).build());
	}
	
	@Test
	public void buildFileMetadataPropagatesException() throws IOException {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(fileStore.computeMd5Hex(inputFile)).thenThrow(IOException.class);
		
		assertThatThrownBy(() -> mediaFileStore.buildFileMetadata(inputFile, fileFormat)).isInstanceOf(IOException.class);
	}
	
	@Test
	public void storeMedia() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		
		FileMetadata result = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		assertThat(result).isSameAs(fileMetadata);
		
		verify(fileStore).storeFile(inputFile, fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		verifyNoMoreInteractions(fileStore, videoFileProcessor, pictureFileProcessor);
	}
	
	@Test
	public void storeThumbnail() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		File tempFile = new File("src/test/resources/media/temp.jpg");
		File outputFile = new File("src/test/resources/media/out.jpg");
		
		when(fileStore.createEmptyFile(eq(fileMetadata.getMd5()), eq(fileMetadata.getTimestamp()), any(TempFileFormat.class))).thenReturn(tempFile);
		when(pictureFileProcessor.createVariant(inputFile, tempFile, MediaSizeVariant.THUMBNAIL)).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		when(pictureFileProcessor.getFormat(Mockito.any())).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		when(fileStore.changeFileFormat(eq(fileMetadata.getMd5()), eq(fileMetadata.getTimestamp()), any(TempFileFormat.class), eq(MediaFileFormat.THUMBNAIL_JPG))).thenReturn(outputFile);
		when(fileStore.computeMd5Hex(outputFile)).thenReturn("456");
		
		FileMetadata result = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		assertThat(result).isEqualTo(FileMetadata.builder().md5("456").fileFormat(MediaFileFormat.THUMBNAIL_JPG).contentSize(0L).timestamp(result.getTimestamp()).build());
	}
	
	@Test
	public void storePreview() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0236.MOV");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_MOV).build();
		File tempFile = new File("src/test/resources/media/temp.jpg");
		File outputFile = new File("src/test/resources/media/out.jpg");
		
		when(fileStore.createEmptyFile(eq(fileMetadata.getMd5()), eq(fileMetadata.getTimestamp()), any(TempFileFormat.class))).thenReturn(tempFile);
		when(videoFileProcessor.createVariant(inputFile, tempFile, MediaSizeVariant.PREVIEW)).thenReturn(MediaFileFormat.PREVIEW_JPG);
		when(videoFileProcessor.getFormat(Mockito.any())).thenReturn(MediaFileFormat.MEDIA_MP4);
		when(fileStore.changeFileFormat(eq(fileMetadata.getMd5()), eq(fileMetadata.getTimestamp()), any(TempFileFormat.class), eq(MediaFileFormat.PREVIEW_JPG))).thenReturn(outputFile);
		when(fileStore.computeMd5Hex(outputFile)).thenReturn("456");
		
		FileMetadata result = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		assertThat(result).isEqualTo(FileMetadata.builder().md5("456").fileFormat(MediaFileFormat.PREVIEW_JPG).contentSize(0L).timestamp(result.getTimestamp()).build());
	}
}
