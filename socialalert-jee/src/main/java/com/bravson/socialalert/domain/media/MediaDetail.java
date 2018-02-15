package com.bravson.socialalert.domain.media;

import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MediaDetail extends MediaInfo {

	private ApprovalModifier userApprovalModifier;
	
	public boolean isLikeAllowed() {
		System.out.println(userApprovalModifier);
		return userApprovalModifier != ApprovalModifier.LIKE;
	}
	
	public boolean isDislikeAllowed() {
		System.out.println(userApprovalModifier);
		return userApprovalModifier != ApprovalModifier.DISLIKE;
	}
}
