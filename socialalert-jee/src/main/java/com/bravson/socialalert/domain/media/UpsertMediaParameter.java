package com.bravson.socialalert.domain.media;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertMediaParameter {
	@NotBlank @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	@NonNull
	private String title;
	
	@Size(max=MediaConstants.MAX_TAG_LENGTH)
	private String category;

	@Min(-MediaConstants.MAX_ABS_FEELING)
	@Max(+MediaConstants.MAX_ABS_FEELING)
	private Integer feeling;
	
	@NotNull
	@Size(max=MediaConstants.MAX_TAG_COUNT)
	@NonNull
	@Schema(maxItems = MediaConstants.MAX_TAG_COUNT, description = "The list of tags")
	private List<@NotNull @Size(max=MediaConstants.MAX_TAG_LENGTH) String> tags;
	
	private GeoAddress location;
	
	@Size(max=FieldLength.NAME)
	private String cameraMaker;
	
	@Size(max=FieldLength.NAME)
	private String cameraModel;
}
