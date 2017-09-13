package com.bravson.socialalert.media.approval;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.bravson.socialalert.infrastructure.entity.InstantAttributeConverter;
import com.bravson.socialalert.media.ApprovalModifier;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.user.profile.ProfileEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name="MediaApproval")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@IdClass(MediaApprovalKey.class)
public class MediaApprovalEntity {
	/*
	@EmbeddedId
	@Getter
	@NonNull
	private MediaApprovalKey key;
	*/
	@Id
	@Getter
	@NonNull
	private String mediaUri;
	
	@Id
	@Getter
	@NonNull
	private String userId;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("userId")
	private ProfileEntity userProfile;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("mediaUri")
	private MediaEntity media;
	
	@Getter
	@Setter
	private ApprovalModifier modifier;
	
	@Getter
	@Convert(converter=InstantAttributeConverter.class)
	private Instant creation;
	
	public MediaApprovalEntity(String mediaUri, String userId) {
		//key = new MediaApprovalKey(mediaUri, userId);
		this.mediaUri = mediaUri;
		this.userId = userId;
		creation = Instant.now();
	}
}
