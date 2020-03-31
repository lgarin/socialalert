package com.bravson.socialalert.test.integration;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserLinkTest extends BaseIntegrationTest {

	@Test
	public void followUnknowUser() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/follow/xyz", MediaTypeConstants.JSON, token).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void readNoFollowedUsers() {
		String token = requestLoginToken("test@test.com", "123");
		@SuppressWarnings("unchecked")
		List<UserInfo> response = createAuthRequest("/user/followed", MediaTypeConstants.JSON, token).get(List.class);
		assertThat(response).isEmpty();
	}
}
