package com.bravson.socialalert.business.media.approval;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.dirtiness.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

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
@Indexed(index = "CommentApproval")
@EqualsAndHashCode(of="id")
@ToString(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class CommentApprovalEntity {

	@EmbeddedId
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type = CommentApprovalKey.Bridge.class))
	@Getter
	@NonNull
	private CommentApprovalKey id;
	
	@Column(name = "modifier", nullable = false)
	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Column(name = "creation", nullable = false)
	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_CommentApproval_Comment"))
	@MapsId("commentId")
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
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
