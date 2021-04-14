package com.bravson.socialalert.test.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.business.user.session.UserSessionCache;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.privacy.LocationPrivacy;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;

import static org.mockito.Mockito.when;

public class UserInfoServiceTest extends BaseServiceTest {

	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	UserSessionCache userSessionCache;
	
	@InjectMocks
	UserInfoService userService;
	
	@Test
	public void fillOnlineUser() {
		UserProfileEntity profile = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(userSessionCache.isUserActive(profile.getId())).thenReturn(true);
		MediaInfo content = new MediaInfo();
		content.setCreatorId(profile.getId());
		UserContent result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.getCreator()).isEqualTo(profile.toOnlineUserInfo());
	}
	
	@Test
	public void fillUserWithLocationMasking() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UserProfileEntity profile = new UserProfileEntity(userAccess);
		profile.updatePrivacySettings(UserPrivacy.builder().location(LocationPrivacy.MASK).build(), userAccess);
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(userSessionCache.isUserActive(profile.getId())).thenReturn(true);
		MediaInfo content = new MediaInfo();
		content.setLongitude(12.0);
		content.setLatitude(47.0);
		content.setCreatorId(profile.getId());
		MediaInfo result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.hasLocation()).isFalse();
	}
	
	@Test
	public void fillUserWithLocationBluring() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UserProfileEntity profile = new UserProfileEntity(userAccess);
		profile.updatePrivacySettings(UserPrivacy.builder().location(LocationPrivacy.BLUR).build(), userAccess);
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(userSessionCache.isUserActive(profile.getId())).thenReturn(true);
		MediaInfo content = new MediaInfo();
		content.setLongitude(7.0135416);
		content.setLatitude(46.9975249);
		content.setCreatorId(profile.getId());
		MediaInfo result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.hasLocation()).isTrue();
		assertThat(result.getLongitude()).isCloseTo(7.0092, offset(0.0001));
		assertThat(result.getLatitude()).isCloseTo(46.9995, offset(0.0001));
	}
	
	@Test
	public void fillOfflineUser() {
		UserProfileEntity profile = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(userSessionCache.isUserActive(profile.getId())).thenReturn(false);
		MediaInfo content = new MediaInfo();
		content.setCreatorId(profile.getId());
		UserContent result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.getCreator()).isEqualTo(profile.toOfflineUserInfo());
	}
	
	@Test
	public void fillMissingUser() {
		String profileId = "invalidUserId";
		when(profileRepository.findByUserId(profileId)).thenReturn(Optional.empty());
		MediaInfo content = new MediaInfo();
		content.setCreatorId(profileId);
		UserContent result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.getCreator()).isNull();
	}
	
	@Test
	public void fillUserCollection() {
		UserProfileEntity profile1 = new UserProfileEntity(createUserAccess("test1", "1.2.3.4"));
		UserProfileEntity profile2 = new UserProfileEntity(createUserAccess("test2", "1.2.3.4"));
		
		MediaInfo content1 = new MediaInfo();
		content1.setCreatorId(profile1.getId());
		MediaInfo content2 = new MediaInfo();
		content2.setCreatorId(profile2.getId());
		MediaInfo content3 = new MediaInfo();
		content3.setCreatorId("xxx");
		List<MediaInfo> contentList = Arrays.asList(content1, content2, content3);
		
		when(profileRepository.findByUserId(profile1.getId())).thenReturn(Optional.of(profile1));
		when(userSessionCache.isUserActive(profile1.getId())).thenReturn(false);
		when(profileRepository.findByUserId(profile2.getId())).thenReturn(Optional.of(profile2));
		when(userSessionCache.isUserActive(profile2.getId())).thenReturn(true);
		when(profileRepository.findByUserId(content3.getCreatorId())).thenReturn(Optional.empty());
		
		List<MediaInfo> result = userService.fillUserInfo(contentList);
		
		assertThat(result).isSameAs(contentList);
		assertThat(result.get(0).getCreator()).isEqualTo(profile1.toOfflineUserInfo());
		assertThat(result.get(1).getCreator()).isEqualTo(profile2.toOnlineUserInfo());
		assertThat(result.get(2).getCreator()).isNull();
	}
}

