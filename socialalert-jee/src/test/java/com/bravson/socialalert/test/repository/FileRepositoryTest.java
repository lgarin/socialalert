package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;

public class FileRepositoryTest extends BaseRepositoryTest {
    
    private FileRepository repository = new FileRepository(getEntityManager());
    
    @Test
    public void findNonExistingFile() {
    	Optional<FileEntity> result = repository.findFile("xyz");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void persistValidFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentLength(0L).fileFormat(MediaFileFormat.MEDIA_JPG).userId("test").ipAddress("1.1.1.1").build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	FileEntity result = repository.storeMedia(fileMetadata, mediaMetadata);
    	assertThat(result.getId()).isEqualTo("19700101/xyz");
    }
    
    @Test
    public void findExistingFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentLength(0L).fileFormat(MediaFileFormat.MEDIA_JPG).userId("test").ipAddress("1.1.1.1").build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	repository.storeMedia(fileMetadata, mediaMetadata);
    	Optional<FileEntity> result = repository.findFile("19700101/xyz");
    	assertThat(result).isNotEmpty();
    }
    
    @Test
    public void queryEmptyRepositoryByIpAddressPattern() {
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void queryByIpAddressPattern() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentLength(0L).fileFormat(MediaFileFormat.MEDIA_JPG).userId("test").ipAddress("1.1.1.1").build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	FileEntity entity = repository.storeMedia(fileMetadata, mediaMetadata);
    	commitTransaction();
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1.*");
    	assertThat(result).containsOnly(entity);
    }
}
