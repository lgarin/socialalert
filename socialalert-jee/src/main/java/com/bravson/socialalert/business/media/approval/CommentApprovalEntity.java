package com.bravson.socialalert.business.media.approval;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

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
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("commentId")
	private MediaCommentEntity comment;

	public CommentApprovalEntity(@NonNull MediaCommentEntity comment, @NonNull String userId) {
		this.commentId = comment.getId();
		this.userId = userId;
		this.creation = Instant.now();
		this.comment = comment;
	}
}
