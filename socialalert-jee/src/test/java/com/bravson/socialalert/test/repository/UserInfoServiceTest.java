package com.bravson.socialalert.test.repository;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bravson.socialalert.media.MediaInfo;
import com.bravson.socialalert.media.UserContent;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;
import com.bravson.socialalert.user.activity.OnlineUserRepository;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoServiceTest extends Assertions {

	@Mock
	ProfileRepository profileRepository;
	
	@Mock
	OnlineUserRepository onlineUserRepository;
	
	@InjectMocks
	UserInfoService userService;
	
	@Test
	public void fillOnlineUser() {
		ProfileEntity profile = new ProfileEntity("test", "test@test.com", UserAccess.of("test", "1.2.3.4"));
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(onlineUserRepository.isUserActive(profile.getId())).thenReturn(true);
		MediaInfo content = new MediaInfo();
		content.setCreatorId(profile.getId());
		UserContent result = userService.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result.getCreator()).isEqualTo(profile.toOnlineUserInfo());
	}
	
	@Test
	public void fillOfflineUser() {
		ProfileEntity profile = new ProfileEntity("test", "test@test.com", UserAccess.of("test", "1.2.3.4"));
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(onlineUserRepository.isUserActive(profile.getId())).thenReturn(false);
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
		ProfileEntity profile1 = new ProfileEntity("test1", "test1@test.com", UserAccess.of("test1", "1.2.3.4"));
		ProfileEntity profile2 = new ProfileEntity("test2", "test2@test.com", UserAccess.of("test2", "1.2.3.4"));
		
		MediaInfo content1 = new MediaInfo();
		content1.setCreatorId(profile1.getId());
		MediaInfo content2 = new MediaInfo();
		content2.setCreatorId(profile2.getId());
		MediaInfo content3 = new MediaInfo();
		content3.setCreatorId("xxx");
		List<MediaInfo> contentList = Arrays.asList(content1, content2, content3);
		
		when(profileRepository.findByUserId(profile1.getId())).thenReturn(Optional.of(profile1));
		when(onlineUserRepository.isUserActive(profile1.getId())).thenReturn(false);
		when(profileRepository.findByUserId(profile2.getId())).thenReturn(Optional.of(profile2));
		when(onlineUserRepository.isUserActive(profile2.getId())).thenReturn(true);
		when(profileRepository.findByUserId(content3.getCreatorId())).thenReturn(Optional.empty());
		
		List<MediaInfo> result = userService.fillUserInfo(contentList);
		
		assertThat(result).isSameAs(contentList);
		assertThat(result.get(0).getCreator()).isEqualTo(profile1.toOfflineUserInfo());
		assertThat(result.get(1).getCreator()).isEqualTo(profile2.toOnlineUserInfo());
		assertThat(result.get(2).getCreator()).isNull();
	}
}

