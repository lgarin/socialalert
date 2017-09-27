package com.bravson.socialalert.user.profile;

import java.time.Instant;
import java.time.LocalDate;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.infrastructure.entity.LocalDateAttributeConverter;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name="UserProfile")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class ProfileEntity extends VersionedEntity {

	@Getter
	@Setter
	@NonNull
	@Field(analyze=Analyze.NO)
	private String username;
	
	@Getter
	@Setter
	@NonNull
	@Field(analyze=Analyze.NO)
	private String email;
	
	@Getter
	@Setter
	@Convert(converter=LocalDateAttributeConverter.class)
	@Field
	private LocalDate birthdate;
	
	@Getter
	@Setter
	@Field
	private Gender gender;
	
	@Getter
	@Setter
	@Field(analyze=Analyze.NO)
	private String country;
	
	@Getter
	@Setter
	@Field(analyze=Analyze.NO)
	private String language;
	
	@Getter
	@Setter
	private String imageUri;
	
	@Getter
	@Setter
	@Field
	@Analyzer(definition="languageAnalyzer")
	private String biography;
	
	public ProfileEntity(@NonNull String username, @NonNull String email, @NonNull UserAccess userAccess) {
		this.id = userAccess.getUserId();
		this.username = username;
		this.email = email;
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
	}
	
	public ProfileEntity(@NonNull String id) {
		this.id = id;
	}
	
	public UserInfo toOnlineUserInfo() {
		return toUserInfo(true);
	}
	
	public UserInfo toOfflineUserInfo() {
		return toUserInfo(false);
	}
	
	private UserInfo toUserInfo(boolean online) {
		return new UserInfo(id, username, email, getCreation(), online);
	}
	
	public Instant getCreation() {
		return versionInfo.getCreation();
	}
}
