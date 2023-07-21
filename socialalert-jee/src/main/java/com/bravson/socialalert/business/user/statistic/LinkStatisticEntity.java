package com.bravson.socialalert.business.user.statistic;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.infrastructure.entity.DefaultStringIdentifierBridge;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="LinkStatistic")
@Indexed(index = "LinkStatistic")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
public class LinkStatisticEntity {

	@Getter
	@Id
	@Column(name = "id", length = FieldLength.ID)
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type=DefaultStringIdentifierBridge.class))
	@GenericField
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@NonNull
	@Getter
	@Column(name = "source_user_id", length = FieldLength.ID, nullable = false)
	@KeywordField
	private String sourceUserId;
	
	@NonNull
	@Getter
	@Column(name = "target_user_id", length = FieldLength.ID, nullable = false)
	@KeywordField
	private String targetUserId;
	
	@Column(name = "activity", nullable = false)
	@Getter
	@NonNull
	@KeywordField
	private LinkActivity activity;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	@Getter
	@Column(name = "country", length = FieldLength.ISO_CODE)
	@KeywordField
	private String country;
	
	public LinkStatisticEntity(UserLinkEntity link, LinkActivity activity, UserAccess userAccess) {
		sourceUserId = link.getSourceUser().getId();
		targetUserId = link.getTargetUser().getId();
		this.activity = activity;
		versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		country = link.getTargetUser().getCountry();
	}
}
