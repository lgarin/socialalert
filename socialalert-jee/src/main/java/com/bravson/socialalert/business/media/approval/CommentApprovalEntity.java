package com.bravson.socialalert.business.media.approval;

import java.time.Instant;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity(name="CommentApproval")
@Indexed
@EqualsAndHashCode(of="id")
@ToString(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class CommentApprovalEntity {

	@EmbeddedId
	@FieldBridge(impl=CommentApprovalKey.Bridge.class)
	@Getter
	@NonNull
	@IndexedEmbedded
	private CommentApprovalKey id;
	
	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("commentId")
	@IndexedEmbedded(includePaths= {"media.id"})
	private MediaCommentEntity comment;

	public CommentApprovalEntity(@NonNull MediaCommentEntity comment, @NonNull String userId) {
		this.id = new CommentApprovalKey(comment.getId(), userId);
		this.creation = Instant.now();
		this.comment = comment;
	}
	
	public String getCommentId() {
		return id.getCommentId();
	}
	
	public String getUserId() {
		return id.getUserId();
	}
}
