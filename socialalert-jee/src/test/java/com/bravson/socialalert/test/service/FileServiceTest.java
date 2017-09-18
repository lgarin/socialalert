package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.file.FileResponse;
import com.bravson.socialalert.file.FileService;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.user.UserAccess;

public class FileServiceTest extends BaseServiceTest {

	@InjectMocks
	FileService fileService;
	
	@Mock
	FileRepository mediaRepository;

	@Mock
	FileStore fileStore;

	@Test
	public void downloadNonExistingFile() throws IOException {
		String fileUri = "abc";
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.empty());
		
		Optional<FileResponse> result = fileService.download(fileUri);
		assertThat(result).isEmpty();
		
		verifyZeroInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingFile() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.MEDIA_JPG;
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(1000L).fileFormat(format).build();
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of("test", "1.2.3.4"));
		
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.download(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
	
	@Test
	public void downloadMissingPreview() throws IOException {
		String fileUri = "abc";
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of("test", "1.2.3.4"));
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		
		Optional<FileResponse> result = fileService.preview(fileUri);
		assertThat(result).isEmpty();
		
		verifyZeroInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingThumbnail() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.THUMBNAIL_JPG;
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileMetadata thumbnailMetadata = FileMetadata.builder().md5("456").timestamp(Instant.MAX).contentLength(1000L).fileFormat(format).build();
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of("test", "1.2.3.4"));
		entity.addVariant(thumbnailMetadata);
		
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), thumbnailMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.thumbnail(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
}
