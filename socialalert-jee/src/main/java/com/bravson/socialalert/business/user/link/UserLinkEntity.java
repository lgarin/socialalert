package com.bravson.socialalert.business.user.link;

import java.time.Instant;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.dirtiness.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

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
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type = UserLinkKey.Bridge.class))
	@Getter
	@NonNull
	private UserLinkKey id;

	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("sourceUserId")
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private UserProfileEntity sourceUser;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("targetUserId")
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private UserProfileEntity targetUser;
	
	public UserLinkEntity(@NonNull UserProfileEntity sourceUser, @NonNull UserProfileEntity targetUser) {
		this.id = new UserLinkKey(sourceUser.getId(), targetUser.getId());
		this.sourceUser = sourceUser;
		this.targetUser = targetUser;
		this.creation = Instant.now();
	}
}
