package com.bravson.socialalert.business.media.approval;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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
@IdClass(MediaApprovalKey.class)
@ToString(of={"mediaUri", "userId"})
@EqualsAndHashCode(of={"mediaUri", "userId"})
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaApprovalEntity {

	@Id
	@Getter
	@NonNull
	private String userId;
	
	@Id
	@Getter
	@NonNull
	private String mediaUri;

	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@NonNull
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("mediaUri")
	private MediaEntity media;

	public MediaApprovalEntity(@NonNull MediaEntity media, @NonNull String userId) {
		this.mediaUri = media.getId();
		this.userId = userId;
		this.creation = Instant.now();
		this.media = media;
	}
}
