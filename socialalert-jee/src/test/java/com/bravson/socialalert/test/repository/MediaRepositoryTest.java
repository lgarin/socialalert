package com.bravson.socialalert.test.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.file.FileEntity;
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
import com.bravson.socialalert.user.profile.ProfileEntity;

public class MediaRepositoryTest extends BaseRepositoryTest {
    
    private MediaRepository repository = new MediaRepository(getEntityManager());

    @Test
    public void findMediaWithInvalidUri() {
    	Optional<MediaEntity> result = repository.findMedia("xyz");
    	assertThat(result).isEmpty();
    }
    
    private MediaEntity storeMedia(MediaEntity mediaEntity) {
		ProfileEntity profileEntity = new ProfileEntity(mediaEntity.getUserId());
		FileEntity fileEntity = new FileEntity(mediaEntity.getId());
		fileEntity.setUserProfile(persistAndIndex(profileEntity));
		mediaEntity.setFile(persistAndIndex(fileEntity));
		mediaEntity.setUserProfile(profileEntity);
		return persistAndIndex(mediaEntity);
	}
    
    private MediaEntity storeDefaultMedia() {
		ClaimMediaParameter claimParameter = new ClaimMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setDescription("Test desc");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategories(Arrays.asList("cat1", "cat2"));
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		return storeMedia(new MediaEntity("abc", MediaKind.PICTURE, claimParameter, VersionInfo.of("test", "1.2.3.4")));
	}
    
    @Test
    public void findNewlyCreatedMedia() {
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
    public void searchAllMediaWithNonEmptyIndex() {
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
    public void searchMediaWithMatchingKeyword() {
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
    public void searchMediaWithMatchingTag() {
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
    public void searchMediaWithFuzzyMatchingKeyword() {
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
    public void searchMediaWithoutMatchingKeyword() {
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
    public void searchMediaInsideArea() {
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
    public void searchMediaOutsideArea() {
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
    public void searchMediaWithAllCategories() {
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
    public void searchMediaWithTooManyCategories() {
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
    public void searchMediaWithWrongCategories() {
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
    public void searchPictureWithinAgeLimit() {
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
    
    @Test
    public void incrementHitCount() {
    	MediaEntity media = storeDefaultMedia();
    	
    	repository.increaseHitCountAtomicaly(media.getId());
    	
    	media = repository.findMedia(media.getId()).get();
    	assertThat(media.getStatistic().getHitCount()).isEqualTo(1);
    }
}
