package com.bravson.socialalert.test.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.media.MediaInfo;
import com.bravson.socialalert.media.UserContent;
import com.bravson.socialalert.user.UserInfoSupplier;
import com.bravson.socialalert.user.activity.SessionRepository;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

import info.solidsoft.mockito.java8.api.WithMockito;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoSupplierTest extends Assertions implements WithMockito {

	@Mock
	ProfileRepository profileRepository;
	
	@Mock
	SessionRepository sessionRepository;
	
	@InjectMocks
	UserInfoSupplier userInfoSupplier;
	
	@Test
	public void test() {
		ProfileEntity profile = new ProfileEntity("test", "test@test.com", new VersionInfo("test", "1.2.3.4"));
		when(profileRepository.findByUserId(profile.getId())).thenReturn(Optional.of(profile));
		when(sessionRepository.isUserActive(profile.getId())).thenReturn(true);
		MediaInfo content = new MediaInfo();
		content.setCreatorId(profile.getId());
		UserContent result = userInfoSupplier.fillUserInfo(content);
		assertThat(result).isSameAs(content);
		assertThat(result).extracting(UserContent::getCreator).containsExactly(profile.toOnlineUserInfo());
	}
	
}
