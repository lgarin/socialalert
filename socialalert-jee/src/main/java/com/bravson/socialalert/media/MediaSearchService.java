package com.bravson.socialalert.media;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserInfoSupplier;

import lombok.NonNull;

@ManagedBean
@Logged
public class MediaSearchService {
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoSupplier userRepository;

	public QueryResult<MediaInfo> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryResult<MediaInfo> result = mediaRepository.searchMedia(parameter, paging).map(MediaEntity::toMediaInfo);
		userRepository.fillUserInfo(result.getContent());
		return result;
	}
}
