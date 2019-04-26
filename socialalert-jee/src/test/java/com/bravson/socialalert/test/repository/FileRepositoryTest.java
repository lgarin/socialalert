package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileState;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class FileRepositoryTest extends BaseRepositoryTest {
    
    private FileRepository repository = new FileRepository(getPersistenceManager(), new DummyEvent<>());
    
    @Test
    public void findNonExistingFile() {
    	Optional<FileEntity> result = repository.findFile("xyz");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void persistValidFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity result = repository.storeMedia(fileMetadata, mediaMetadata, userAccess);
    	assertThat(result.getId()).isEqualTo("19700101/xyz");
    }
    
    @Test
    public void findExistingFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	repository.storeMedia(fileMetadata, mediaMetadata, userAccess);
    	Optional<FileEntity> result = repository.findFile("19700101/xyz");
    	assertThat(result).isNotEmpty();
    }
    
    @Test
    public void queryEmptyRepositoryByIpAddressPattern() {
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1.*");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void queryByIpAddressPattern() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, userAccess);
    	persistAndIndex(entity);
    	
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1.*");
    	assertThat(result).containsOnly(entity);
    }
    
    @Test
    public void queryByUserIdAndState() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, userAccess);
    	persistAndIndex(entity);
    	
    	List<FileEntity> result = repository.findByUserIdAndState("test", FileState.UPLOADED);
    	assertThat(result).containsOnly(entity);
    }
}
