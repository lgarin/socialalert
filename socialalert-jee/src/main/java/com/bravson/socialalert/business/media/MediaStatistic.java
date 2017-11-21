package com.bravson.socialalert.business.media;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@Embeddable
@NoArgsConstructor()
@Setter(AccessLevel.NONE)
@Indexed
public class MediaStatistic {

	@Field
	private int hitCount;
	
	@Field
	private int likeCount;
	
	@Field
	private int dislikeCount;
	
	@Field
	private int commentCount;

	public double computeBoost() {
		return Math.log(Math.max(2.0, hitCount)) * Math.sqrt(Math.max(2.0, likeCount - dislikeCount));
	}

	public void increaseHitCount() {
		hitCount++;
	}

	public void updateApprovalCount(ApprovalModifier oldModifier, ApprovalModifier newModifier) {
		likeCount += ApprovalModifier.computeLikeDelta(oldModifier, newModifier);
		dislikeCount += ApprovalModifier.computeDislikeDelta(oldModifier, newModifier);
	}
	
	public void increateCommentCount() {
		commentCount++;
	}
}
