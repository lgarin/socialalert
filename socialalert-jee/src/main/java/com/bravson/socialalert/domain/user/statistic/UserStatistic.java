package com.bravson.socialalert.domain.user.statistic;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class UserStatistic {
	
	@Column(name = "login_count", nullable = false)
	private int loginCount;

	@Column(name = "file_count", nullable = false)
	private int fileCount;
	
	@Column(name = "picture_count", nullable = false)
	private int pictureCount;
	
	@Column(name = "video_count", nullable = false)
	private int videoCount;
	
	@Column(name = "comment_count", nullable = false)
	private int commentCount;
	
	@Column(name = "hit_count", nullable = false)
	private int hitCount;
	
	@Column(name = "like_count", nullable = false)
	private int likeCount;
	
	@Column(name = "dislike_count", nullable = false)
	private int dislikeCount;
	
	@Column(name = "follower_count", nullable = false)
	private int followerCount;
	
	public void incLoginCount() {
		loginCount++;
	}
	
	public void incFileCount() {
		fileCount++;
	}
	
	public void incPictureCount() {
		pictureCount++;
	}
	
	public void incVideoCount() {
		videoCount++;
	}
	
	public void incCommentCount() {
		commentCount++;
	}
	
	public void incHitCount() {
		hitCount++;
	}
	
	public void incLikeCount() {
		likeCount++;
	}
	
	public void incDislikeCount() {
		dislikeCount++;
	}
	
	public void incFollowerCount() {
		followerCount++;
	}
	
	public void decFollowerCount() {
		followerCount--;
	}
}
