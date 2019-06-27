package com.bravson.socialalert.business.feed;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="FeedItem")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FeedItemEntity {

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
	@ManyToOne(fetch=FetchType.LAZY)
	@IndexedEmbedded(includePaths= {"id"})
	private MediaCommentEntity comment;

	@Getter
	@NonNull
	@Field
	private FeedActivity activity;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	public FeedItemEntity(@NonNull MediaEntity media, MediaCommentEntity comment, @NonNull FeedActivity activity, @NonNull UserAccess userAccess) {
		this.media = media;
		this.comment = comment;
		this.activity = activity;
		versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
	
	public FeedItemInfo toItemInfo() {
		FeedItemInfo info = new FeedItemInfo();
		info.setActivity(getActivity());
		info.setMedia(getMedia().toMediaInfo());
		if (getComment() != null) {
			info.setComment(getComment().toMediaCommentInfo());
		}
		info.setCreatorId(versionInfo.getUserId());
		info.setCreation(versionInfo.getCreation());
		return info;
	}
}
