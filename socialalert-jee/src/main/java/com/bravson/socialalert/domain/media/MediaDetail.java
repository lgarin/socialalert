package com.bravson.socialalert.domain.media;

import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MediaDetail extends MediaInfo {

	private ApprovalModifier userApprovalModifier;
	
	public boolean isLikeAllowed() {
		return userApprovalModifier != ApprovalModifier.LIKE;
	}
	
	public boolean isDislikeAllowed() {
		return userApprovalModifier != ApprovalModifier.DISLIKE;
	}
}
