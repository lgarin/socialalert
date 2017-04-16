package com.bravson.socialalert.test.repository;

import java.io.IOException;
import java.time.Instant;

import org.junit.Test;

import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.val;

public class FileRepositoryTest extends BaseRepositoryTest {
    
    private FileRepository repository = new FileRepository(createEntityManager());
    
    @Test
    public void findNonExistingFile() {
    	val result = repository.findFile("xyz");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void persistValidFile() throws IOException {
    	val fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).fileFormat(MediaFileFormat.MEDIA_JPG).userId("test").ipAddress("1.1.1.1").build();
    	val mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	val result = repository.storeMedia(fileMetadata, mediaMetadata);
    	assertThat(result.getFileUri()).isEqualTo("19700101/xyz");
    }
    
    @Test
    public void findExistingFile() throws IOException {
    	val fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).fileFormat(MediaFileFormat.MEDIA_JPG).userId("test").ipAddress("1.1.1.1").build();
    	val mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	repository.storeMedia(fileMetadata, mediaMetadata);
    	val result = repository.findFile("19700101/xyz");
    	assertThat(result).isNotEmpty();
    }
}
