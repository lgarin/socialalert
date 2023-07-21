package com.bravson.socialalert.business.media.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

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
public class MediaStatistic {

	@Column(name = "hit_count", nullable = false)
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES)
	private int hitCount;
	
	@Column(name = "like_count", nullable = false)
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES)
	private int likeCount;
	
	@Column(name = "dislike_count", nullable = false)
	@GenericField(aggregable = Aggregable.YES)
	private int dislikeCount;
	
	@Column(name = "comment_count", nullable = false)
	@GenericField(aggregable = Aggregable.YES)
	private int commentCount;
	
	@Column(name = "boost_factor", nullable = false)
	@GenericField(sortable = Sortable.YES)
	private float boostFactor;

	private float computeBoost() {
		return (float) (Math.log(Math.max(2, hitCount)) * Math.sqrt(Math.max(2, likeCount - dislikeCount)));
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
