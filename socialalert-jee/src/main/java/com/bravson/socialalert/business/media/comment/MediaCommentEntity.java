package com.bravson.socialalert.business.media.comment;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.bravson.socialalert.business.media.MediaConstants;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.media.comment.UserCommentDetail;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.DefaultStringIdentifierBridge;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="MediaComment")
@Indexed(index = "MediaComment")
@ToString(of="id")
@EqualsAndHashCode(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaCommentEntity {
	
	@Getter
	@Id
	@Column(name = "id", length = FieldLength.ID)
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type=DefaultStringIdentifierBridge.class))
	@GenericField
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@Getter
	@NonNull
	@ManyToOne(fetch=FetchType.EAGER, optional = false) // TODO should be lazy
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Comment_Media"))
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private MediaEntity media;
	
	@Getter
	@NonNull
	@Column(name = "comment", length = MediaConstants.MAX_COMMENT_LENGTH, nullable = false)
	@FullTextField(analyzer = "languageAnalyzer")
	private String comment;
	
	@Getter
	@Column(name = "like_count", nullable = false)
	@GenericField
	private int likeCount;
	
	@Getter
	@Column(name = "dislike_count", nullable = false)
	@GenericField
	private int dislikeCount;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	public MediaCommentEntity(@NonNull MediaEntity media, @NonNull String comment, @NonNull UserAccess userAccess) {
		this.media = media;
		this.comment = comment;
		versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
	}
	
	public void updateApprovalCount(ApprovalModifier oldModifier, ApprovalModifier newModifier) {
		likeCount += ApprovalModifier.computeLikeDelta(oldModifier, newModifier);
		dislikeCount += ApprovalModifier.computeDislikeDelta(oldModifier, newModifier);
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
	
	public String getMediaUri() {
		return getMedia().getId();
	}
	
	private <T extends MediaCommentInfo> T fillMediaInfo(T info) {
		info.setId(id);
		info.setComment(comment);
		info.setCreatorId(versionInfo.getUserId());
		info.setCreation(versionInfo.getCreation());
		info.setLikeCount(likeCount);
		info.setDislikeCount(dislikeCount);
		return info;
	}
	
	public MediaCommentInfo toMediaCommentInfo() {
		return fillMediaInfo(new MediaCommentInfo());
	}

	public MediaCommentDetail toMediaCommentDetail() {
		return fillMediaInfo(new MediaCommentDetail());
	}
	
	public UserCommentDetail toUserCommentDetail() {
		UserCommentDetail result = fillMediaInfo(new UserCommentDetail());
		result.setMedia(getMedia().toMediaInfo());
		result.getMedia().applyPrivacy();
		return result;
	}
}
