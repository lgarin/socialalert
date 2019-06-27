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
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("sourceUserId")
	@IndexedEmbedded(includePaths= {"id"})
	private UserProfileEntity sourceUser;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("targetUserId")
	@IndexedEmbedded(includePaths= {"id"})
	private UserProfileEntity targetUser;
	
	public UserLinkEntity(@NonNull UserProfileEntity sourceUser, @NonNull UserProfileEntity targetUser) {
		this.id = new UserLinkKey(sourceUser.getId(), targetUser.getId());
		this.sourceUser = sourceUser;
		this.targetUser = targetUser;
		this.creation = Instant.now();
	}
}
