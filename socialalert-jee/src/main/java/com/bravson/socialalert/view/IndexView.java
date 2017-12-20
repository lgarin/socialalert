package com.bravson.socialalert.view;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class IndexView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;
	
	@Inject
	private MediaSearchService searchMediaService;
	
	@Getter
	@Setter
	private MediaInfo selectedMedia;

	@Getter
	private List<MediaInfo> mediaList;

    public void loadMediaList() {
    	SearchMediaParameter parameter = new SearchMediaParameter();
    	mediaList = searchMediaService.searchMedia(parameter, new PagingParameter(Instant.now(), 0, 50)).getContent();
    }
}    
