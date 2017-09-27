package com.bravson.socialalert.media;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserInfoService;

import lombok.NonNull;

@ManagedBean
@Logged
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
}
