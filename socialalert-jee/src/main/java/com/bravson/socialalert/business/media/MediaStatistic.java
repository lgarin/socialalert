package com.bravson.socialalert.business.media;

import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

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

	@GenericField
	private int hitCount;
	
	@GenericField
	private int likeCount;
	
	@GenericField
	private int dislikeCount;
	
	@GenericField
	private int commentCount;
	
	@GenericField
	private double boostFactor;

	private double computeBoost() {
		return Math.log(Math.max(2.0, hitCount)) * Math.sqrt(Math.max(2.0, likeCount - dislikeCount));
	}

	public void increaseHitCount() {
		hitCount++;
		boostFactor = computeBoost();
	}

	public void updateApprovalCount(ApprovalModifier oldModifier, ApprovalModifier newModifier) {
		likeCount += ApprovalModifier.computeLikeDelta(oldModifier, newModifier);
		dislikeCount += ApprovalModifier.computeDislikeDelta(oldModifier, newModifier);
		boostFactor = computeBoost();
	}
	
	public void increateCommentCount() {
		commentCount++;
	}
}
