package com.bravson.socialalert.business.media.approval;

import java.time.Instant;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity(name="MediaApproval")
@Indexed
@EqualsAndHashCode(of="id")
@ToString(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaApprovalEntity {

	@EmbeddedId
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type = MediaApprovalKey.Bridge.class))
	@Getter
	@NonNull
	private MediaApprovalKey id;

	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@MapsId("mediaUri")
	private MediaEntity media;

	public MediaApprovalEntity(@NonNull MediaEntity media, @NonNull String userId) {
		this.id = new MediaApprovalKey(media.getId(), userId);
		this.creation = Instant.now();
		this.media = media;
	}
	
	public String getMediaUri() {
		return id.getMediaUri();
	}
	
	public String getUserId() {
		return id.getUserId();
	}
}
