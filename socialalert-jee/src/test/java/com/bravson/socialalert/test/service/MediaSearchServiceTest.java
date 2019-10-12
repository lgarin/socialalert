package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.business.media.tag.MediaTagRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

public class MediaSearchServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaSearchService searchService;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	MediaTagRepository tagRepository;
	
	@Mock
	UserInfoService userService;

	@Test
	public void searchMediaWithNoMatch() {
		SearchMediaParameter parameter = new SearchMediaParameter();
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		when(mediaRepository.searchMedia(parameter, paging)).thenReturn(new QueryResult<>(Collections.emptyList(), 0, paging));
		
		QueryResult<MediaInfo> result = searchService.searchMedia(parameter, paging);
		assertThat(result.getPageCount()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}
	
	@Test
	public void searchMediaWithOneMatch() {
		MediaEntity mediaEntity = mock(MediaEntity.class);
		SearchMediaParameter parameter = new SearchMediaParameter();
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		MediaInfo mediaInfo = new MediaInfo();
		when(mediaRepository.searchMedia(parameter, paging)).thenReturn(new QueryResult<>(Collections.singletonList(mediaEntity), 1, paging));
		when(mediaEntity.toMediaInfo()).thenReturn(mediaInfo);
		when(userService.fillUserInfo(Collections.singletonList(mediaInfo))).thenReturn(Collections.singletonList(mediaInfo));
		
		QueryResult<MediaInfo> result = searchService.searchMedia(parameter, paging);
		assertThat(result.getPageCount()).isEqualTo(1);
		assertThat(result.getContent()).containsExactly(mediaInfo);
	}
	
	@Test
	public void groupByGeoHash() {
		GeoStatistic geoStat = GeoStatistic.builder().count(10).build();
		SearchMediaParameter parameter = new SearchMediaParameter();
		when(mediaRepository.groupByGeoHash(parameter)).thenReturn(Collections.singletonList(geoStat));
		
		List<GeoStatistic> result = searchService.groupByGeoHash(parameter);
		assertThat(result).containsExactly(geoStat);
	}
	
	@Test
	public void suggestTags() {
		String searchTerm = "test";
		when(tagRepository.suggestTags(searchTerm)).thenReturn(Arrays.asList("test", "testimonial"));
		
		List<String> result = searchService.suggestTags(searchTerm);
		assertThat(result).containsExactly("test", "testimonial");
	}
}
