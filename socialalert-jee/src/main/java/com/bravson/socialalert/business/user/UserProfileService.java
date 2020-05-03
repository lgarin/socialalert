package com.bravson.socialalert.business.user;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.authentication.AuthenticationRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfileService {

	private static final int MAXIMUM_AGE = 200;

	private static final String ENGLISH_LANGUAGE_CODE = Locale.ENGLISH.getLanguage();
	
	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
	@Inject
	@NonNull
	AuthenticationRepository authenticationRepository;
	
	private static Set<String> getValidCountryCodes() {
		return Set.of(Locale.getISOCountries());
	}
	
	private static Set<String> getValidLanguageCodes() {
		return Set.of(Locale.getISOLanguages());
	}
	
	public UserInfo updateProfile(@NonNull UpdateProfileParameter param, @NonNull UserAccess userAccess) {
		
		if (param.getCountry() != null && !getValidCountryCodes().contains(param.getCountry())) {
			throw new BadRequestException("Unsupported country " + param.getCountry());
		}
		
		if (param.getLanguage() != null && !getValidLanguageCodes().contains(param.getLanguage())) {
			throw new BadRequestException("Unsupported language " + param.getLanguage());
		}
		
		if (param.getBirthdate() != null && param.getBirthdate().isAfter(LocalDate.now())) {
			throw new BadRequestException("Birthdate cannot be in the future");
		}
		if (param.getBirthdate() != null && param.getBirthdate().isBefore(LocalDate.now().plusYears(-MAXIMUM_AGE))) {
			throw new BadRequestException("Birthdate cannot be so far in the past");
		}
		
		UserProfileEntity entity = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
		
		if (entity.hasNameChange(param.getFirstname(), param.getLastname())) {
			entity.updateProfile(param, userAccess);
			authenticationRepository.updateUser(userAccess.getUserId(), entity.getFirstname(), entity.getLastname());
		} else {
			entity.updateProfile(param, userAccess);
		}
		
		return entity.toOnlineUserInfo();
	}
	
	public Map<String,String> getValidCountries() {
		return getValidCountryCodes().stream().collect(Collectors.toMap(k -> k, k -> new Locale(ENGLISH_LANGUAGE_CODE, k).getDisplayCountry(Locale.ENGLISH)));
	}
	
	public Map<String,String> getValidLanguages() {
		return getValidLanguageCodes().stream().collect(Collectors.toMap(k -> k, k -> new Locale(k).getDisplayLanguage(Locale.ENGLISH)));
	}
}
