package com.bravson.socialalert.media.approval;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.InstantAttributeConverter;
import com.bravson.socialalert.media.comment.MediaCommentEntity;
import com.bravson.socialalert.user.profile.ProfileEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name="CommentApproval")
@IdClass(CommentApprovalKey.class)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class CommentApprovalEntity {

	@Id
	@Getter
	@NonNull
	private String userId;
	
	@Id
	@Getter
	@NonNull
	private String commentId;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("userId")
	private ProfileEntity userProfile;
	
	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@Convert(converter=InstantAttributeConverter.class)
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("commentId")
	private MediaCommentEntity comment;

	public CommentApprovalEntity(@NonNull String commentId, @NonNull String userId) {
		this.commentId = commentId;
		this.userId = userId;
		this.creation = Instant.now();
	}
}
