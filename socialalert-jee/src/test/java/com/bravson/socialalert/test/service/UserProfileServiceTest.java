package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserProfileService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;

public class UserProfileServiceTest extends BaseServiceTest {

	@Mock
	UserProfileRepository profileRepository;
	
	@InjectMocks
	UserProfileService profileService;
	
	@Test
	public void updateExistingProfile() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UserProfileEntity profileEntity = new UserProfileEntity("test", "test@test.com", userAccess);
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("FR").gender(Gender.MALE).build();
		
		UserInfo result = profileService.updateProfile(param, userAccess);
		assertThat(result.getBiography()).isNull();
		assertThat(result.getBirthdate()).isNull();
		assertThat(result.getCountry()).isEqualTo("CH");
		assertThat(result.getLanguage()).isEqualTo("FR");
		assertThat(result.getGender()).isEqualTo(Gender.MALE);
	}
}
