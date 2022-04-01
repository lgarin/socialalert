package com.bravson.socialalert.business.media;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.business.media.tag.MediaTagRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.UserLinkService;
import com.bravson.socialalert.domain.histogram.HistogramParameter;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.statistic.CreatorMediaCount;
import com.bravson.socialalert.domain.media.statistic.LocationMediaCount;
import com.bravson.socialalert.domain.media.statistic.MediaCount;
import com.bravson.socialalert.domain.media.statistic.MediaStatisticAggregation;
import com.bravson.socialalert.domain.media.statistic.PeriodicMediaCount;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional(TxType.REQUIRED)
public class MediaSearchService {
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	@Inject
	MediaTagRepository tagRepository;
	
	@Inject
	UserLinkService linkService;

	public QueryResult<MediaInfo> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryResult<MediaInfo> result = mediaRepository.searchMedia(parameter, paging).map(MediaEntity::toMediaInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
	
	public List<GeoStatistic> groupByGeoHash(@NonNull SearchMediaParameter parameter) {
		return mediaRepository.groupByGeoHash(parameter);
	}
	
	public List<CreatorMediaCount> groupByCreator(@NonNull String userId, @NonNull SearchMediaParameter parameter, int maxCreatorCount, int maxMediaCount) {
		List<MediaCount> groups = mediaRepository.groupByCreator(parameter, maxCreatorCount);
		if (maxMediaCount > 0) {
			PagingParameter paging = new PagingParameter(Instant.now(), 0, maxMediaCount);
			for (MediaCount group : groups) {
				parameter.setCreator(group.getKey());
				group.setTopMedia(searchMedia(parameter, paging).getContent());
			}
		}
		List<CreatorMediaCount> result = groups.stream().map(CreatorMediaCount::new).collect(Collectors.toList());
		return linkService.fillLinkedUserInfo(userId, userService.fillUserInfo(result));
	}
	
	public List<LocationMediaCount> groupByLocation(@NonNull SearchMediaParameter parameter, int maxLocationCount, int maxMediaCount) {
		List<LocationMediaCount> result = mediaRepository.groupByLocation(parameter, maxLocationCount);
		if (maxMediaCount > 0) {
			PagingParameter paging = new PagingParameter(Instant.now(), 0, maxMediaCount);
			for (LocationMediaCount group : result) {
				parameter.setCountry(group.getCountry());
				parameter.setLocality(group.getLocality());
				group.setTopMedia(searchMedia(parameter, paging).getContent());
			}
		}
		return result;
	}
	
	public List<PeriodicMediaCount> groupByPeriod(@NonNull SearchMediaParameter parameter, @NonNull HistogramParameter histogram, int maxMediaCount) {
		List<PeriodicMediaCount> result = mediaRepository.groupByPeriod(parameter, histogram.getInterval());
		if (maxMediaCount > 0) {
			TemporalAmount delta = histogram.getInterval().toTemporalAmount();
			Instant now = Instant.now();
			for (PeriodicMediaCount group : result) {
				PagingParameter paging = new PagingParameter(group.getPeriod().plus(delta), 0, maxMediaCount);
				parameter.setMaxAge(Duration.between(group.getPeriod(), now));
				group.setTopMedia(searchMedia(parameter, paging).getContent());
			}
		}
		return histogram.filter(result);
	}

	public List<String> suggestTags(@NonNull String searchTerm, int maxHitCount) {
		return tagRepository.suggestTags(searchTerm, maxHitCount);
	}
	
	public MediaStatisticAggregation aggregateStatistic(@NonNull SearchMediaParameter parameter) {
		return mediaRepository.aggregateStatistic(parameter);
	}
}
