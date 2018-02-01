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

import lombok.Getter;
import lombok.Setter;

@Model
public class IndexView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;
	
	@Inject
	private MediaSearchService searchMediaService;

	@Getter
	private List<MediaInfo> mediaList;

	@Getter
	@Setter
	private String keyword;
	
	public List<String> completeKeyword(String query) {
		return searchMediaService.suggestTags(query);
	}
	
    public void loadMediaList() {
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords(keyword);
    	mediaList = searchMediaService.searchMedia(parameter, new PagingParameter(Instant.now(), 0, 50)).getContent();
    }
}    
