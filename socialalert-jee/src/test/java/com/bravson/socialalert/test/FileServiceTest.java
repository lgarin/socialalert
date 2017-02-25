package com.bravson.socialalert.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class FileServiceTest extends BaseServiceTest {

	private Entity<String> getPlainText(String content) {
		return Entity.entity(content, MediaType.TEXT_PLAIN_TYPE);
	}
	
	@Test
	public void uploadWithoutPrincial() throws Exception {
		Response response = createRequest("/file/upload", MediaType.WILDCARD).header("filename", "test.txt").post(getPlainText("test"));
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	public void uploadWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload", MediaType.WILDCARD, token).header("filename", "test.txt").post(getPlainText("test"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		String path = response.getLocation().toString().replaceFirst("(.*/rest/)", "");
		String content = createAuthRequest(path, MediaType.WILDCARD, token).get(String.class);
		assertThat(content).isEqualTo("test");
	}
	
	@Test
	public void uploadWithoutFilename() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload", MediaType.WILDCARD, token).post(getPlainText("test"));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void uploadTooLarge() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload", MediaType.WILDCARD, token).header("filename", "test.txt").post(getPlainText("testtoolarge"));
		assertThat(response.getStatus()).isEqualTo(Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode());
	}

	@Test
	public void downloadNonExistingFile() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/download/f8b1befe5d29ba266c36ffad", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}
