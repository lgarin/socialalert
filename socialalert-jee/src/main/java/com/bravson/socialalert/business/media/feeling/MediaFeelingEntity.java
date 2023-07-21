package com.bravson.socialalert.business.media.feeling;

import java.time.Instant;

import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.bravson.socialalert.business.media.entity.MediaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity(name="MediaFeeling")
@Indexed(index = "MediaFeeling")
@EqualsAndHashCode(of="id")
@ToString(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaFeelingEntity {

	@EmbeddedId
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type = MediaFeelingKey.Bridge.class))
	@Getter
	@NonNull
	private MediaFeelingKey id;

	@Getter
	@Setter
	@Column(name = "feeling", nullable = false)
	private Integer feeling;
	
	@Getter
	@NonNull
	@Column(name = "creation", nullable = false)
	private Instant creation;
	
	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "media_id", foreignKey = @ForeignKey(name = "FK_MediaFeeling_Media"))
	@MapsId("mediaUri")
	private MediaEntity media;

	public MediaFeelingEntity(@NonNull MediaEntity media, @NonNull String userId) {
		this.id = new MediaFeelingKey(media.getId(), userId);
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
