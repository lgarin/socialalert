package com.bravson.socialalert.business.user.link;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="UserLink")
@Indexed(index = "UserLink")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
public class UserLinkEntity {

	@EmbeddedId
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type = UserLinkKey.Bridge.class))
	@Getter
	@NonNull
	private UserLinkKey id;

	@Getter
	@NonNull
	@Column(name = "creation", nullable = false)
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "source_user_id", foreignKey = @ForeignKey(name = "FK_UserLink_SourceUser"))
	@MapsId("sourceUserId")
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private UserProfileEntity sourceUser;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("targetUserId")
	@JoinColumn(name = "target_user_id", foreignKey = @ForeignKey(name = "FK_UserLink_TargetUser"))
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
