package com.bravson.socialalert.test.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.bravson.socialalert.file.media.MediaFileConstants;

public class UploadServiceTest extends BaseServiceTest {

	private Entity<String> getPlainText(String content) {
		return Entity.entity(content, MediaType.TEXT_PLAIN_TYPE);
	}
	
	private Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	private Entity<File> getVideo(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.MOV_MEDIA_TYPE);
	}

	@Test
	public void uploadPictureWithoutPrincial() throws Exception {
		Response response = createRequest("/file/uploadPicture", MediaType.WILDCARD).post(getPicture("src/main/resources/logo.jpg"));
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	public void uploadPictureWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/uploadPicture", MediaType.WILDCARD, token).post(getPicture("src/main/resources/logo.jpg"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		String path = response.getLocation().toString().replaceFirst("^(http://.*/rest/)", "");
		File content = createAuthRequest(path, MediaType.WILDCARD, token).get(File.class);
		assertThat(content).isFile().hasBinaryContent(Files.readAllBytes(Paths.get("src/main/resources/logo.jpg")));
	}
	
	@Test
	public void uploadVideoWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/uploadVideo", MediaType.WILDCARD, token).post(getVideo("C:/Dev/IMG_0236.MOV"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		String path = response.getLocation().toString().replaceFirst("^(http://.*/rest/)", "");
		File content = createAuthRequest(path, MediaType.WILDCARD, token).get(File.class);
		assertThat(content).isFile().hasBinaryContent(Files.readAllBytes(Paths.get("C:/Dev/IMG_0236.MOV")));
	}

	@Test
	public void uploadPictureTooLarge() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/uploadPicture", MediaType.WILDCARD, token).post(getPicture("C:/Dev/jdk8/javafx-src.zip"));
		assertThat(response.getStatus()).isEqualTo(Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode());
	}
	
	@Test
	public void uploadPictureWithWrongMediaType() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/uploadPicture", MediaType.WILDCARD, token).post(getPlainText("test"));
		assertThat(response.getStatus()).isEqualTo(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
	}

}
