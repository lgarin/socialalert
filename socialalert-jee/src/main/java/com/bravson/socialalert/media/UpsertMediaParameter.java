package com.bravson.socialalert.media;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.domain.location.GeoAddress;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UpsertMediaParameter {
	@NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	@NonNull
	private String title;
	
	@NotEmpty @Size(max=MediaConstants.MAX_DESCRIPTION_LENGTH)
	@NonNull
	private String description;
	
	@NotNull @Size(max=MediaConstants.MAX_CATEGORY_COUNT)
	@NonNull
	private List<String> categories;
	
	@NotNull @Size(max=MediaConstants.MAX_TAG_COUNT)
	@NonNull
	private List<String> tags;
	
	private GeoAddress location;
}
