package com.bravson.socialalert.business.user.profile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name="UserProfile")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserProfileEntity extends VersionedEntity {

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
	
	@Getter
	@Setter
	@Field
	private Instant lastLogin;
	
	@Getter
	@NonNull
	@Embedded
	private UserStatistic statistic;
	
	@OneToMany
	private Set<FileEntity> files;
	
	@OneToMany
	private Set<MediaEntity> medias;
	
	@OneToMany
	private Set<MediaCommentEntity> comments;
	
	@Getter
	@OneToMany(mappedBy="sourceUser")
	private Set<UserLinkEntity> followedUsers;
	
	public UserProfileEntity(@NonNull String username, @NonNull String email, @NonNull UserAccess userAccess) {
		this.id = userAccess.getUserId();
		this.username = username;
		this.email = email;
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.statistic = new UserStatistic();
	}
	
	public UserProfileEntity(@NonNull String id) {
		this.id = id;
	}
	
	public UserInfo toOnlineUserInfo() {
		return toUserInfo(true);
	}
	
	public UserInfo toOfflineUserInfo() {
		return toUserInfo(false);
	}
	
	public UserInfo toUserInfo(boolean online) {
		return UserInfo.builder()
				.id(id)
				.username(username)
				.email(email)
				.createdTimestamp(getCreation())
				.online(online)
				.biography(biography)
				.birthdate(birthdate)
				.country(country)
				.language(language)
				.imageUri(imageUri)
				.statistic(statistic)
				.build();
	}
	
	public Instant getCreation() {
		return versionInfo.getCreation();
	}
	
	public void addFile(FileEntity file) {
		if (files == null) {
			files = new HashSet<>();
		}
		files.add(file);
		statistic.incFileCount();
	}
	
	public void addMedia(MediaEntity media) {
		if (medias == null) {
			medias = new HashSet<>();
		}
		medias.add(media);
		if (media.getKind() == MediaKind.VIDEO) {
			statistic.incVideoCount();
		} else {
			statistic.incPictureCount();
		}
	}
	
	public void addComment(MediaCommentEntity comment) {
		if (comments == null) {
			comments = new HashSet<>();
		}
		comments.add(comment);
		statistic.incCommentCount();
	}

	public void addMediaHit() {
		statistic.incHitCount();
	}
	
	public void addMediaLike() {
		statistic.incLikeCount();
	}
	
	public void addMediaDislike() {
		statistic.incDislikeCount();
	}

	public void login(AuthenticationInfo authInfo) {
		setEmail(authInfo.getEmail());
		setUsername(authInfo.getUsername());
		getStatistic().incLoginCount();
		setLastLogin(Instant.now());
	}

	public LoginResponse toLoginResponse(String accessToken) {
		return LoginResponse.builder()
				.accessToken(accessToken)
				.id(id)
				.username(username)
				.email(email)
				.createdTimestamp(getCreation())
				.online(true)
				.biography(biography)
				.birthdate(birthdate)
				.country(country)
				.language(language)
				.imageUri(imageUri)
				.statistic(statistic)
				.build();
	}
	
}
