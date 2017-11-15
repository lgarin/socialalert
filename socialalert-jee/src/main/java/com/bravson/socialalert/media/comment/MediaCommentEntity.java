package com.bravson.socialalert.media.comment;

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

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.user.UserAccess;

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
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
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
	
	public MediaCommentInfo toMediaCommentInfo() {
		MediaCommentInfo result = new MediaCommentInfo();
		result.setId(id);
		result.setComment(comment);
		result.setCreatorId(versionInfo.getUserId());
		result.setCreation(versionInfo.getCreation());
		result.setLikeCount(likeCount);
		result.setDislikeCount(dislikeCount);
		return result;
	}
}
