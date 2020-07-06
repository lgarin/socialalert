package com.bravson.socialalert.test.integration;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.ChangePasswordParameter;
import com.bravson.socialalert.domain.user.CreateUserParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserCredential;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserFacadeTest extends BaseIntegrationTest {

	@Inject
	UserProfileRepository profileRepository;
	
	@Test
	public void loginWithExistingUser() throws Exception {
		createProfile("test@test.com");
		
		UserCredential param = new UserCredential("test@test.com", "123");
		Response response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		LoginResponse result = response.readEntity(LoginResponse.class); 
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("test@test.com");
		assertThat(result.getAccessToken()).startsWith("Bearer ").matches(s -> s.length() > 64);
	}

	private UserProfileEntity createProfile(String username) {
		AuthenticationInfo authInfo = AuthenticationInfo.builder()
				.id("8b99179c-2a6b-4e41-92d3-3edfe3df885b")
				.email(username)
				.username(username)
				.createdTimestamp(Instant.EPOCH)
				.build();
		return profileRepository.createProfile(authInfo, "1.2.3.4");
	}
	
	@Test
	public void loginWithEmptyPassword() throws Exception {
		UserCredential param = new UserCredential("test@test.com", "");
		Response response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithEmptyUserId() throws Exception {
		UserCredential param = new UserCredential("", "abc");
		Response response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithInvalidPassword() throws Exception {
		UserCredential param = new UserCredential("test@test.com", "abc");
		Response response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void loginWithUnknownUser() throws Exception {
		UserCredential param = new UserCredential("xyz@test.com", "abc");
		Response response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void renewLoginWithInvalidToken() throws Exception {
		Response response = createRequest("/user/renewLogin", MediaTypeConstants.JSON).post(Entity.text("1234"));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void renewLoginWithValidToken() throws Exception {
		UserCredential param = new UserCredential("test@test.com", "123");
		LoginResponse loginResponse = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param)).readEntity(LoginResponse.class);
		Response response = createRequest("/user/renewLogin", MediaTypeConstants.JSON).post(Entity.text(loginResponse.getRefreshToken()));
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void renewLoginTwiceWithSameToken() throws Exception {
		UserCredential param = new UserCredential("test@test.com", "123");
		LoginResponse loginResponse = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param)).readEntity(LoginResponse.class);
		Response response1 = createRequest("/user/renewLogin", MediaTypeConstants.JSON).post(Entity.text(loginResponse.getRefreshToken()));
		assertThat(response1.getStatus()).isEqualTo(Status.OK.getStatusCode());
		Response response2 = createRequest("/user/renewLogin", MediaTypeConstants.JSON).post(Entity.text(loginResponse.getRefreshToken()));
		assertThat(response2.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void logoutWithoutToken() throws Exception {
		Response response = createRequest("/user/logout", MediaTypeConstants.JSON).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void logoutWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/logout", MediaTypeConstants.TEXT_PLAIN, token).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	public void logoutWithInvalidToken() throws Exception {
		Response response = createAuthRequest("/user/logout", MediaTypeConstants.TEXT_PLAIN, "Bearer 12344334").post(null);
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
	
	@Test
	public void getCurrentUserWithToken() throws Exception {
		createProfile("test@test.com");
		
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/current", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		UserInfo user = response.readEntity(UserInfo.class);
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo("test@test.com");
	}
	
	@Test
	public void getCurrentUserWithoutToken() throws Exception {
		Response response = createRequest("/user/current", MediaTypeConstants.JSON).get();
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void updateExistingProfile() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("fr").build();
		UserInfo response = createAuthRequest("/user/profile", MediaTypeConstants.JSON, token).post(Entity.json(param), UserInfo.class);
		assertThat(response.getCountry()).isEqualTo("CH");
		assertThat(response.getLanguage()).isEqualTo("fr");
	}
	
	@Test
	public void updateProfileWithInvalidParameter() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("Switzerland").language("French").build();
		Response response = createAuthRequest("/user/profile", MediaTypeConstants.JSON, token).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void updateProfileWIthInvalidLanguage() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("FR").build();
		Response response = createAuthRequest("/user/profile", MediaTypeConstants.JSON, token).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void updateProfileWithoutToken() throws Exception {
		UpdateProfileParameter param = UpdateProfileParameter.builder().country("CH").language("FR").build();
		Response response = createRequest("/user/profile", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void listCountries() throws Exception {
		Map<String,String> response = createRequest("/user/countries", MediaTypeConstants.JSON).get(new GenericType<Map<String,String>>() {});
		assertThat(response).contains(Map.entry("CH", "Switzerland"));
	}
	
	@Test
	public void listLanguages() throws Exception {
		Map<String,String> response = createRequest("/user/languages", MediaTypeConstants.JSON).get(new GenericType<Map<String,String>>() {});
		assertThat(response).contains(Map.entry("fr", "French"));
	}
	
	@Test
	public void listCommentsForInvalidUser() {
		String token = requestLoginToken("test@test.com", "123");
		String userId = "xyz";
		Response response = createAuthRequest("/user/comments/" + userId, MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void listCommentsForExistingUser() {
		String token = requestLoginToken("test@test.com", "123");
		String userId = "8b99179c-2a6b-4e41-92d3-3edfe3df885b";
		Response response = createAuthRequest("/user/comments/" + userId, MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void changePassword() {
		ChangePasswordParameter param = new ChangePasswordParameter("test@test.com", "123", "123");
		Response response = createRequest("/user/changePassword", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	public void deleteUnknownUser() {
		UserCredential param = new UserCredential("xyz@test.com", "123");
		Response response = createRequest("/user/delete", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void deleteNewUser() {
		CreateUserParameter newUserParam = CreateUserParameter.builder().email("new@test.com").username("newUser").password("123").build();
		createRequest("/user/create", MediaTypeConstants.JSON).post(Entity.json(newUserParam));
		UserCredential param = new UserCredential("newUser", "123");
		Response response = createRequest("/user/delete", MediaTypeConstants.JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
				
	}
	
	@Test
	public void updatePrivacyWithoutMasking() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		UpdateProfileParameter param = UpdateProfileParameter.builder().lastname("TestLast").birthdate(LocalDate.EPOCH).build();
		createAuthRequest("/user/profile", MediaTypeConstants.JSON, token).post(Entity.json(param));
		UserPrivacy privacy = UserPrivacy.builder().birthdateMasked(false).nameMasked(false).build();
		UserInfo response = createAuthRequest("/user/privacy", MediaTypeConstants.JSON, token).post(Entity.json(privacy), UserInfo.class);
		assertThat(response.getBirthdate()).isNotNull();
		assertThat(response.getLastname()).isNotNull();
	}
	
	@Test
	public void updatePrivacyWithMasking() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		UpdateProfileParameter param = UpdateProfileParameter.builder().lastname("TestLast").birthdate(LocalDate.EPOCH).build();
		createAuthRequest("/user/profile", MediaTypeConstants.JSON, token).post(Entity.json(param));
		UserPrivacy privacy = UserPrivacy.builder().birthdateMasked(true).nameMasked(true).build();
		UserInfo response = createAuthRequest("/user/privacy", MediaTypeConstants.JSON, token).post(Entity.json(privacy), UserInfo.class);
		assertThat(response.getBirthdate()).isNotNull();
		assertThat(response.getLastname()).isNotNull();
	}
}