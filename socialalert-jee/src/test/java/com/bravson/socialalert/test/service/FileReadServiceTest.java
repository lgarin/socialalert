package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileReadService;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class FileReadServiceTest extends BaseServiceTest {

	@InjectMocks
	FileReadService fileService;
	
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
		
		verifyNoInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingFile() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.MEDIA_JPG;
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(format).build();
		FileEntity entity = new FileEntity(fileMetadata, UserAccess.of("test", "1.2.3.4"));
		
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.download(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
	
	@Test
	public void downloadMissingPreview() throws IOException {
		String fileUri = "abc";
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity entity = new FileEntity(fileMetadata, UserAccess.of("test", "1.2.3.4"));
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		
		Optional<FileResponse> result = fileService.preview(fileUri);
		assertThat(result).isEmpty();
		
		verifyNoInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingThumbnail() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.THUMBNAIL_JPG;
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileMetadata thumbnailMetadata = FileMetadata.builder().md5("456").timestamp(Instant.MAX).contentSize(1000L).fileFormat(format).build();
		FileEntity entity = new FileEntity(fileMetadata, UserAccess.of("test", "1.2.3.4"));
		entity.addVariant(thumbnailMetadata);
		
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), thumbnailMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.thumbnail(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
}
