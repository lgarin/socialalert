package com.bravson.socialalert.media;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.user.UserInfoService;

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
}
