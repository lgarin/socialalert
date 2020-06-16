package com.bravson.socialalert.test.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserProfileService;
import com.bravson.socialalert.business.user.authentication.AuthenticationRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserProfileServiceTest extends BaseServiceTest {

	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	AuthenticationRepository authenticationRepository;
	
	@InjectMocks
	UserProfileService profileService;
	
	@Test
	public void updateExistingProfile() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UserProfileEntity profileEntity = new UserProfileEntity("test", "test@test.com", userAccess);
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("fr").gender(Gender.MALE).build();
		
		UserInfo result = profileService.updateProfile(param, userAccess);
		assertThat(result.getBiography()).isNull();
		assertThat(result.getBirthdate()).isNull();
		assertThat(result.getCountry()).isEqualTo("CH");
		assertThat(result.getLanguage()).isEqualTo("fr");
		assertThat(result.getGender()).isEqualTo(Gender.MALE);
	}
	
	@Test
	public void updateProfileName() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UserProfileEntity profileEntity = new UserProfileEntity("test", "test@test.com", userAccess);
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		
		UpdateProfileParameter param = UpdateProfileParameter.builder().firstname("Firstname").lastname("Lastname").country("CH").language("fr").gender(Gender.MALE).build();
		
		UserInfo result = profileService.updateProfile(param, userAccess);
		assertThat(result.getFirstname()).isEqualTo("Firstname");
		assertThat(result.getLastname()).isEqualTo("Lastname");
		
		verify(authenticationRepository).updateUser(userAccess.getUserId(), "Firstname", "Lastname");
	}
	
	@Test
	public void updateProfileWithInvalidLanguage() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("FR").gender(Gender.MALE).build();
		assertThrows(BadRequestException.class, () -> profileService.updateProfile(param, userAccess));
	}
	
	@Test
	public void listCountries() {
		Map<String,String> result = profileService.getValidCountries();
		assertThat(result).contains(Map.entry("CH", "Switzerland"), Map.entry("FR", "France"), 
				Map.entry("DE", "Germany"), Map.entry("IT", "Italy"), Map.entry("AT", "Austria"));
	}
	
	@Test
	public void listLanguages() {
		Map<String,String> result = profileService.getValidLanguages();
		assertThat(result).contains(Map.entry("fr", "French"), Map.entry("de", "German"), 
				Map.entry("it", "Italian"), Map.entry("en", "English"));
	}
	
	@Test
	public void updatePrivacy() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UserProfileEntity profileEntity = new UserProfileEntity("test", "test@test.com", userAccess);
		UpdateProfileParameter profileParam = UpdateProfileParameter.builder().firstname("first").lastname("last").birthdate(LocalDate.EPOCH).build();
		profileEntity.updateProfile(profileParam, userAccess);
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		
		UserPrivacy privacyParam = UserPrivacy.builder().birthdateMasked(false).nameMasked(true).build();
		UserInfo result = profileService.updatePrivacy(privacyParam, userAccess);
		
		assertThat(result.getFirstname()).isNull();
		assertThat(result.getLastname()).isNull();
		assertThat(result.getBirthdate()).isNotNull();
	}
	
	@Test
	public void updatePrivacyWithInvalidUser() {
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.empty());
		UserPrivacy param = new UserPrivacy();
		assertThrows(NotFoundException.class, () -> profileService.updatePrivacy(param, userAccess));
	}
}
