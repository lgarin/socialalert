package com.bravson.socialalert.test.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class FileServiceTest extends BaseServiceTest {

	@Test
	public void downloadNonExistingFile() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/download/f8b1befe5d29ba266c36ffad", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void downloadExistingFile() throws Exception  {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/download/58b28c6b28011a1ad4180419", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.getMediaType()).isEqualTo(MediaType.TEXT_PLAIN_TYPE);
		assertThat(response.readEntity(String.class)).isEqualTo("test");
	}
}
