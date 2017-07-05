package com.bravson.socialalert.media;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class ClaimPictureParameter {
	@NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	private String title;
	
	@NotEmpty @Size(max=MediaConstants.MAX_DESCRIPTION_LENGTH)
	private String description;
	
	@NotNull @Size(max=MediaConstants.MAX_CATEGORY_COUNT)
	private List<String> categories;
	
	@NotNull @Size(max=MediaConstants.MAX_TAG_COUNT)
	private List<String> tags;
	
	private GeoAddress location;
}
