package com.bravson.socialalert.test.integration;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.domain.media.query.MediaQueryParameter;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
public class MediaQueryTest extends BaseIntegrationTest {

	@Test
	public void findNonExistingQuery() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/liveQuery", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void postInvalidQuery() {
		String token = requestLoginToken("test@test.com", "123");
		MediaQueryParameter param = new MediaQueryParameter();
		param.setLabel("Test");
		param.setHitThreshold(0);
		Response response = createAuthRequest("/media/liveQuery", MediaTypeConstants.JSON, token).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
}
