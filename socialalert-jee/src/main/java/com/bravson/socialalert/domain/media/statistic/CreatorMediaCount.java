package com.bravson.socialalert.domain.media.statistic;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Schema(description="The number of matching media from a specific creator.")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreatorMediaCount extends MediaCount implements UserContent {
	
	private UserInfo creator;
	
	public CreatorMediaCount(@NonNull MediaCount mediaCount) {
		super(mediaCount.getKey(), mediaCount.getCount());
	}

	@Override
	public String getCreatorId() {
		return getKey();
	}

	@Override
	public void applyPrivacy() {
		// nothing to do
	}
}
