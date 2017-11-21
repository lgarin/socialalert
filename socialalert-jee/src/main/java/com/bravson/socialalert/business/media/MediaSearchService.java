package com.bravson.socialalert.business.media;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
public class MediaSearchService {
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;

	public QueryResult<MediaInfo> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryResult<MediaInfo> result = mediaRepository.searchMedia(parameter, paging).map(MediaEntity::toMediaInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
	
	public List<GeoStatistic> groupByGeoHash(@NonNull SearchMediaParameter parameter) {
		return mediaRepository.groupByGeoHash(parameter);
	}
}
