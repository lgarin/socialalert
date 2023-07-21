package com.bravson.socialalert.test.integration;

import java.util.List;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.domain.paging.QueryResult;
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
		Response response = createAuthRequest("/user/followed", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		List<UserInfo> result = response.readEntity(new GenericType<List<UserInfo>>() {});
		assertThat(result).isEmpty();
	}
	
	@Test
	public void readNoFollowers() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/followers", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		QueryResult<UserInfo> result = response.readEntity(new GenericType<QueryResult<UserInfo>>() {});
		assertThat(result.getContent()).isEmpty();
	}
}
