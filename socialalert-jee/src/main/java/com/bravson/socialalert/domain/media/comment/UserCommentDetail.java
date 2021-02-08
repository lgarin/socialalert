package com.bravson.socialalert.domain.media.comment;

import com.bravson.socialalert.domain.media.MediaInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserCommentDetail extends MediaCommentInfo {

	private MediaInfo media;
	
	@Override
	public void applyPrivacy() {
		if (media != null) {
			media.applyPrivacy();
		}
	}
}
