package com.bravson.socialalert.business.media.comment;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="MediaComment")
@Indexed
@ToString(of="id")
@EqualsAndHashCode(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaCommentEntity {
	
	@Getter
	@Id
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@IndexedEmbedded(includePaths= {"id"})
	private MediaEntity media;
	
	@Getter
	@NonNull
	@Field
	@Analyzer(definition="languageAnalyzer")
	private String comment;
	
	@Getter
	@Field
	private int likeCount;
	
	@Getter
	@Field
	private int dislikeCount;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	public MediaCommentEntity(@NonNull String commentId) {
		this.id = commentId;
	}
	
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
}
