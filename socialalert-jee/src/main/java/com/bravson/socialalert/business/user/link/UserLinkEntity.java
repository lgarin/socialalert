package com.bravson.socialalert.business.user.link;

import java.time.Instant;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="UserLink")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserLinkEntity {

	@EmbeddedId
	@FieldBridge(impl=UserLinkKey.Bridge.class)
	@Getter
	@NonNull
	@IndexedEmbedded
	private UserLinkKey id;

	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("sourceUserId")
	private UserProfileEntity sourceUser;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("targetUserId")
	private UserProfileEntity targetUser;
	
	public UserLinkEntity(@NonNull String sourceUserId, @NonNull String targetUserId) {
		this.id = new UserLinkKey(sourceUserId, targetUserId);
		this.creation = Instant.now();
	}
}
