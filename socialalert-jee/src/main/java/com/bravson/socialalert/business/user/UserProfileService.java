package com.bravson.socialalert.business.user;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

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

	private static final String ENGLISH_LANGUAGE_CODE = Locale.ENGLISH.getLanguage();
	
	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
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
		
		UserProfileEntity entity = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
		entity.updateProfile(param, userAccess);
		return entity.toOnlineUserInfo();
	}
	
	public Map<String,String> getValidCountries() {
		return getValidCountryCodes().stream().collect(Collectors.toMap(k -> k, k -> new Locale(ENGLISH_LANGUAGE_CODE, k).getDisplayCountry(Locale.ENGLISH)));
	}
	
	public Map<String,String> getValidLanguages() {
		return getValidLanguageCodes().stream().collect(Collectors.toMap(k -> k, k -> new Locale(k).getDisplayLanguage(Locale.ENGLISH)));
	}
}
