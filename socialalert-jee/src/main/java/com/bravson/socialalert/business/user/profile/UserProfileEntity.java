package com.bravson.socialalert.business.user.profile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.authentication.LoginToken;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserDetail;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;
import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity(name="UserProfile")
@Indexed(index = "UserProfile")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserProfileEntity extends VersionedEntity {

	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@NonNull
	@Column(name = "username", length = FieldLength.NAME, nullable = false)
	@KeywordField
	private String username;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@NonNull
	@Column(name = "email", length = FieldLength.NAME, nullable = false)
	@KeywordField
	private String email;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "firstname", length = FieldLength.NAME)
	@KeywordField
	private String firstname;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "lastname", length = FieldLength.NAME)
	@KeywordField
	private String lastname;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@GenericField
	private LocalDate birthdate;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@KeywordField
	private Gender gender;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "country", length = FieldLength.ISO_CODE)
	@KeywordField
	private String country;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "language", length = FieldLength.ISO_CODE)
	@KeywordField
	private String language;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "image_uri", length = FieldLength.ID + FieldLength.MD5)
	private String imageUri;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "biography", length = FieldLength.TEXT)
	@FullTextField(analyzer="languageAnalyzer")
	private String biography;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "last_login")
	@GenericField
	private Instant lastLogin;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Column(name = "last_activity")
	@GenericField
	private Instant lastActivity;
	
	@Getter
	@NonNull
	@Embedded
	private UserStatistic statistic;
	
	@Getter
	@NonNull
	@Embedded
	private UserPrivacy privacy;
	
	/*
	@OneToMany
	@CollectionTable(name = "UserFile", joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_User_File")), uniqueConstraints = @UniqueConstraint(name = "UK_UserFile_File", columnNames = "files_id"))
	private Set<FileEntity> files;
	
	@OneToMany
	@JoinColumn(name = "media_id", foreignKey = @ForeignKey(name = "FK_Media_User"))
	@CollectionTable(name = "UserMedia", joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_User_Media")), uniqueConstraints = @UniqueConstraint(name = "UK_UserMedia_Media", columnNames = "medias_id"))
	private Set<MediaEntity> medias;
	
	@OneToMany
	@CollectionTable(name = "UserComment", joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_User_Comment")), uniqueConstraints = @UniqueConstraint(name = "UK_UserComment_Comment", columnNames = "comments_id"))
	private Set<MediaCommentEntity> comments;
	*/
	@Getter
	@OneToMany(mappedBy="sourceUser")
	private Set<UserLinkEntity> followedUsers;
	
	public UserProfileEntity(@NonNull UserAccess userAccess) {
		this.id = userAccess.getUserId();
		this.username = userAccess.getUsername();
		this.email = userAccess.getEmail();
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.statistic = new UserStatistic();
		this.privacy = new UserPrivacy();
		this.followedUsers = new HashSet<>();
	}
	
	public UserProfileEntity(@NonNull String id) {
		this.id = id;
	}
	
	public UserDetail toOwnUserDetail() {
		UserDetail detail = fillUserInfo(new UserDetail(), true);
		detail.setPrivacy(privacy);
		return detail;
	}
	
	public UserInfo toOnlineUserInfo() {
		return applyPrivacy(fillUserInfo(new UserInfo(), true));
	}
	
	public UserInfo toOfflineUserInfo() {
		return applyPrivacy(fillUserInfo(new UserInfo(), false));
	}
	
	private UserInfo applyPrivacy(UserInfo info) {
		if (privacy.isNameMasked()) {
			info.setFirstname(null);
			info.setLastname(null);
		}
		if (privacy.isGenderMasked()) {
			info.setGender(null);
		}
		if (privacy.isBirthdateMasked()) {
			info.setBirthdate(null);
		}
		return info;
	}
	
	private <T extends UserInfo> T fillUserInfo(T info, boolean online) {
		info.setId(id);
		info.setUsername(username);
		info.setEmail(email);
		info.setCreatedTimestamp(versionInfo.getCreation());
		info.setOnline(online);
		info.setFirstname(firstname);
		info.setLastname(lastname);
		info.setBiography(biography);
		info.setBirthdate(birthdate);
		info.setGender(gender);
		info.setCountry(country);
		info.setLanguage(language);
		info.setImageUri(imageUri);
		info.setStatistic(statistic);
		info.setCreatorPrivacy(privacy);
		return info;
	}
	
	public void addFile() {
		statistic.incFileCount();
		setLastActivity(Instant.now());
	}
	
	public void addMedia(MediaEntity media) {
		if (media.getKind() == MediaKind.VIDEO) {
			statistic.incVideoCount();
		} else {
			statistic.incPictureCount();
		}
		setLastActivity(Instant.now());
	}
	
	public void removeMedia(MediaEntity media) {
		if (media.getKind() == MediaKind.VIDEO) {
			statistic.decVideoCount();
		} else {
			statistic.decPictureCount();
		}
		setLastActivity(Instant.now());
	}
	
	public void addComment() {
		statistic.incCommentCount();
		setLastActivity(Instant.now());
	}

	public void addMediaHit() {
		statistic.incHitCount();
		setLastActivity(Instant.now());
	}
	
	public void addMediaLike() {
		statistic.incLikeCount();
		setLastActivity(Instant.now());
	}
	
	public void addMediaDislike() {
		statistic.incDislikeCount();
		setLastActivity(Instant.now());
	}
	
	public void addFollower() {
		statistic.incFollowerCount();
		setLastActivity(Instant.now());
	}
	
	public void removeFollower() {
		statistic.decFollowerCount();
		setLastActivity(Instant.now());
	}

	public void login(AuthenticationInfo authInfo) {
		setEmail(authInfo.getEmail());
		setUsername(authInfo.getUsername());
		getStatistic().incLoginCount();
		setLastLogin(Instant.now());
		setLastActivity(getLastLogin());
	}
	
	public void markActive() {
		setLastActivity(Instant.now());
	}

	public LoginResponse toLoginResponse(LoginToken loginToken) {
		return LoginResponse.builder()
				.accessToken(loginToken.getAccessToken())
				.refreshToken(loginToken.getRefreshToken())
				.expiration(loginToken.getExpiration())
				.id(id)
				.username(username)
				.email(email)
				.createdTimestamp(versionInfo.getCreation()) // TODO extract from token
				.online(true)
				.firstname(firstname)
				.lastname(lastname)
				.biography(biography)
				.birthdate(birthdate)
				.gender(gender)
				.country(country)
				.language(language)
				.imageUri(imageUri)
				.statistic(statistic)
				.privacy(privacy)
				.build();
	}

	public void changeAvatar(String imageUri, @NonNull UserAccess userAccess) {
		setImageUri(imageUri);
		versionInfo.touch(userAccess.getUserId(), userAccess.getIpAddress());
		setLastActivity(versionInfo.getLastUpdate());
	}
	
	public boolean hasNameChange(String newFirstname, String newLastname) {
		return hasFirstnameChange(newFirstname) || hasLastnameChange(newLastname);
	}

	private boolean hasLastnameChange(String newLastname) {
		return newLastname != null && !newLastname.equals(lastname);
	}

	private boolean hasFirstnameChange(String newFirstname) {
		return newFirstname != null && !newFirstname.equals(firstname);
	}
	
	public void updateProfile(@NonNull UpdateProfileParameter param, @NonNull UserAccess userAccess) {
		if (param.getBirthdate() != null) {
			setBirthdate(param.getBirthdate());
		}
		if (param.getCountry() != null) {
			setCountry(param.getCountry());
		}
		if (param.getLanguage() != null) {
			setLanguage(param.getLanguage());
		}
		if (param.getGender() != null) {
			setGender(param.getGender());
		}
		if (param.getBiography() != null) {
			setBiography(param.getBiography());
		}
		if (param.getFirstname() != null) {
			setFirstname(param.getFirstname());
		}
		if (param.getLastname() != null) {
			setLastname(param.getLastname());
		}
		versionInfo.touch(userAccess.getUserId(), userAccess.getIpAddress());
	}

	public void updatePrivacySettings(@NonNull UserPrivacy settings, @NonNull UserAccess userAccess) {
		privacy = settings;
		versionInfo.touch(userAccess.getUserId(), userAccess.getIpAddress());
	}
}
