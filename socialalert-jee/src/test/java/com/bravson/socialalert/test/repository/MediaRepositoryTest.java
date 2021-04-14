package com.bravson.socialalert.test.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.media.SearchMediaParameter;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    MediaRepository repository;

    @Test
    public void findMediaWithInvalidUri() {
    	Optional<MediaEntity> result = repository.findMedia("xyz");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void findNewlyCreatedMedia() {
    	MediaEntity media = storeDefaultMedia();
    	
    	Optional<MediaEntity> result = repository.findMedia(media.getId());
    	assertThat(result).contains(media);
    }

    @Test
    public void searchAllMediaWithEmptyIndex() {
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
    	QueryResult<MediaEntity> result = repository.searchMedia(parameter, paging);
    	assertThat(result).isNotNull();
    	assertThat(result.getPageCount()).isEqualTo(1);
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageCount()).isZero();
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageCount()).isZero();
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageCount()).isZero();
    	assertThat(result.getPageNumber()).isZero();
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
    	assertThat(result.getPageNumber()).isZero();
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).containsExactly(media);
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
    
    @Test
    public void listByUserId() {
    	MediaEntity media = storeDefaultMedia();
    	
    	List<MediaEntity> result = repository.listByUserId(media.getUserId());
    	assertThat(result).containsOnly(media);
    }
    
    @Test
    public void listByInvalidUserId() {
    	MediaEntity media = storeDefaultMedia();
    	
    	List<MediaEntity> result = repository.listByUserId(media.getUserId() + "99");
    	assertThat(result).isEmpty();;
    }
}
