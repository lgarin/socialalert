package com.bravson.socialalert.business.user.profile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.UserInfo;
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
	
	@OneToMany
	private Set<FileEntity> files;
	
	@OneToMany
	private Set<MediaEntity> medias;
	
	@OneToMany
	private Set<MediaCommentEntity> comments;
	
	public UserProfileEntity(@NonNull String username, @NonNull String email, @NonNull UserAccess userAccess) {
		this.id = userAccess.getUserId();
		this.username = username;
		this.email = email;
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
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
	
	private UserInfo toUserInfo(boolean online) {
		return new UserInfo(id, username, email, getCreation(), online);
	}
	
	public Instant getCreation() {
		return versionInfo.getCreation();
	}
	
	public void addFile(FileEntity file) {
		if (files == null) {
			files = new HashSet<>();
		}
		files.add(file);
	}
	
	public void addMedia(MediaEntity media) {
		if (medias == null) {
			medias = new HashSet<>();
		}
		medias.add(media);
	}
	
	public void addComment(MediaCommentEntity comment) {
		if (comments == null) {
			comments = new HashSet<>();
		}
		comments.add(comment);
	}
	
}
