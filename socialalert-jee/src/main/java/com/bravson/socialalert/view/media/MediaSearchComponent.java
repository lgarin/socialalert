package com.bravson.socialalert.view.media;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.MediaInfo;

import lombok.Getter;
import lombok.Setter;

@ViewScoped
@Named
public class MediaSearchComponent implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;
	
	@Inject
	private MediaSearchService searchMediaService;

	@Getter
	private List<MediaInfo> mediaList;

	@Getter
	@Setter
	private String keyword;
	
	@Getter
	@Setter
	private List<String> categories;
	
	public List<String> completeKeyword(String query) {
		return searchMediaService.suggestTags(query);
	}

	public SearchMediaParameter buildSearchParameter() {
		SearchMediaParameter parameter = new SearchMediaParameter();
    	parameter.setKeywords(keyword);
    	if (categories != null && !categories.isEmpty()) {
    		parameter.setCategory(categories.stream().collect(Collectors.joining(" ")));
    	}
    	return parameter;
	}
}
