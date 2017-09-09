package com.bravson.socialalert.test.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.media.ClaimMediaParameter;
import com.bravson.socialalert.media.GeoAddress;
import com.bravson.socialalert.media.GeoArea;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaKind;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.media.PagingParameter;
import com.bravson.socialalert.media.QueryResult;
import com.bravson.socialalert.media.SearchMediaParameter;

public class MediaRepositoryTest extends BaseRepositoryTest {
    
    private MediaRepository repository = new MediaRepository(getEntityManager());

    @Test
    public void findMediaWithInvalidUri() {
    	Optional<MediaEntity> result = repository.findMedia("xyz");
    	assertThat(result).isEmpty();
    }
    
    private MediaEntity storeMedia(ClaimMediaParameter claimParameter) throws InterruptedException {
    	FileMetadata fileMetadata = FileMetadata.builder().fileFormat(MediaFileFormat.MEDIA_JPG).contentLength(1000L).md5("abc").timestamp(Instant.EPOCH).userId("test").ipAddress("1.2.3.4").build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1000).height(800).build();
    	FileEntity file = FileEntity.of(fileMetadata, mediaMetadata);
    	persistAndIndex(file);
    	MediaEntity media = MediaEntity.of(file, claimParameter, VersionInfo.of(file.getUserId(), file.getFileMetadata().getIpAddress()));
    	persistAndIndex(media);
		return media;
	}
    
    private MediaEntity storeDefaultMedia() throws InterruptedException {
		ClaimMediaParameter claimParameter = new ClaimMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setDescription("Test desc");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategories(Arrays.asList("cat1", "cat2"));
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
    	MediaEntity media = storeMedia(claimParameter);
		return media;
	}
    
    @Test
    public void findNewlyCreatedMedia() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	Optional<MediaEntity> result = repository.findMedia(media.getId());
    	assertThat(result).isNotEmpty();
    }

    @Test
    public void searchAllMediaWithEmptyIndex() {
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    public void searchAllMediaWithNonEmptyIndex() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithMatchingKeyword() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords("Test any");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithMatchingTag() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords("tag1");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithFuzzyMatchingKeyword() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords("Tesa any");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithoutMatchingKeyword() throws InterruptedException {
    	storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords("Other any");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(0);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    public void searchMediaInsideArea() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setArea(GeoArea.builder().longitude(7.5).latitude(47).radius(10).build());
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaOutsideArea() throws InterruptedException {
    	storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setArea(GeoArea.builder().longitude(7.5).latitude(47).radius(6.7).build());
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(0);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    public void searchMediaWithAllCategories() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setCategory("cat1 cat2");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithTooManyCategories() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setCategory("cat1 cat2 cat4");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
    
    @Test
    public void searchMediaWithWrongCategories() throws InterruptedException {
    	storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setCategory("cat3 cat4");
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(0);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    public void searchPictureWithinAgeLimit() throws InterruptedException {
    	MediaEntity media = storeDefaultMedia();
    	
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setMaxAge(Duration.ofMinutes(1));
    	parameter.setMediaKind(MediaKind.PICTURE);
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
    }
}
