package com.bravson.socialalert.media.comment;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.user.UserAccess;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="MediaComment")
@Indexed
@ToString(of="commentId")
@EqualsAndHashCode(of="commentId")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaCommentEntity {
	
	@Getter
	@Id
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@GeneratedValue(generator="system-uuid")
	private String commentId;
	
	@Getter
	@NonNull
	@Field(analyze=Analyze.NO)
	private String mediaUri;
	/*
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("mediaUri")
	private MediaEntity media;
	*/
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
		this.commentId = commentId;
	}
	
	public MediaCommentEntity(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		this.mediaUri = mediaUri;
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
	
	public MediaCommentInfo toMediaCommentInfo() {
		MediaCommentInfo result = new MediaCommentInfo();
		result.setCommentId(commentId);
		result.setComment(comment);
		result.setCreatorId(versionInfo.getUserId());
		result.setCreation(versionInfo.getCreation());
		result.setLikeCount(likeCount);
		result.setDislikeCount(dislikeCount);
		return result;
	}
}
