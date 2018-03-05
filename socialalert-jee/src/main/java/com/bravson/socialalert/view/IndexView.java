package com.bravson.socialalert.view;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.view.media.MediaSearchComponent;

import lombok.Getter;

@Model
public class IndexView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;
	
	@Inject
	private MediaSearchService searchMediaService;

	@Getter
	private List<MediaInfo> mediaList;
	
	@Inject
	private MediaSearchComponent searchComponent;
	
    public void loadMediaList() {
    	SearchMediaParameter parameter = searchComponent.buildSearchParameter();
    	mediaList = searchMediaService.searchMedia(parameter, new PagingParameter(Instant.now(), 0, 50)).getContent();
    }
}    
