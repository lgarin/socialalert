package com.bravson.socialalert.domain.user.statistic;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class UserStatistic {
	
	private int loginCount;

	private int fileCount;
	
	private int pictureCount;
	
	private int videoCount;
	
	private int commentCount;
	
	private int hitCount;
	
	private int likeCount;
	
	private int dislikeCount;
	
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
}
