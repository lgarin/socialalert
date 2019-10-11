package com.bravson.socialalert.business.media;

import javax.persistence.Column;
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

	@Column(name = "hit_count", nullable = false)
	@GenericField
	private int hitCount;
	
	@Column(name = "like_count", nullable = false)
	@GenericField
	private int likeCount;
	
	@Column(name = "dislike_count", nullable = false)
	@GenericField
	private int dislikeCount;
	
	@Column(name = "comment_count", nullable = false)
	@GenericField
	private int commentCount;
	
	@Column(name = "boost_factor", nullable = false)
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
