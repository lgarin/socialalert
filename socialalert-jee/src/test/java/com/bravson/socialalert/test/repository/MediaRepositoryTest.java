package com.bravson.socialalert.test.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaKind;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.media.SearchMediaParameter;
import com.bravson.socialalert.media.UpsertMediaParameter;
import com.bravson.socialalert.user.UserAccess;
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
		UpsertMediaParameter claimParameter = new UpsertMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setDescription("Test desc");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategories(Arrays.asList("cat1", "cat2"));
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		return storeMedia(new MediaEntity("abc", MediaKind.PICTURE, claimParameter, UserAccess.of("test", "1.2.3.4")));
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
    	parameter.setArea(GeoBox.builder().minLon(7.4).maxLon(7.6).minLat(46).maxLat(48).build());
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
    	parameter.setArea(GeoBox.builder().minLon(7.4).maxLon(7.5).minLat(46.1).maxLat(46.2).build());
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

    @Test
    public void groupPicturesByGeoHash() {
    	MediaEntity media = storeDefaultMedia();

    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setArea(GeoBox.builder().minLon(7.4).maxLon(7.6).minLat(46).maxLat(48).build());
    	QueryResult<MediaEntity> r = repository.searchMedia(parameter, new PagingParameter(Instant.now(), 0, 10));
    	assertThat(r.getContent()).containsExactly(media);
    	List<GeoStatistic> result = repository.groupByGeoHash(parameter);
    	assertThat(result).hasSize(1);
    }
}
