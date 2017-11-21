package com.bravson.socialalert.test.integration;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.business.file.media.MediaFileConstants;
import com.google.common.io.Files;

public class FileUploadTest extends BaseIntegrationTest {
	
	private static Entity<String> getPlainText(String content) {
		return Entity.entity(content, MediaType.TEXT_PLAIN_TYPE);
	}
	
	private static Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	private static Entity<File> getVideo(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.MOV_MEDIA_TYPE);
	}

	@Test
	@RunAsClient
	public void uploadPictureWithoutPrincial() throws Exception {
		Response response = createRequest("/file/upload/picture", MediaType.WILDCARD).post(getPicture("src/main/resources/logo.jpg"));
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
	
	private static Predicate<File> contentEquals(String sourceFile) {
		return f -> {
			try {
				return Files.equal(f, new File(sourceFile));
			} catch (IOException e) {
				return false;
			}
		};
	}

	@Test
	@RunAsClient
	public void uploadPictureWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		File content = createAuthRequest(getLocationPath(response), MediaType.WILDCARD, token).get(File.class);
		assertThat(content).isFile().matches(contentEquals("src/test/resources/media/IMG_0397.JPG"));
	}

	@Test
	@RunAsClient
	public void uploadVideoWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload/video", MediaType.WILDCARD, token).post(getVideo("src/test/resources/media/IMG_0236.MOV"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		File content = createAuthRequest(getLocationPath(response), MediaType.WILDCARD, token).get(File.class);
		assertThat(content).isFile().matches(contentEquals("src/test/resources/media/IMG_0236.MOV"));
	}

	@Test
	@RunAsClient
	public void uploadPictureTooLarge() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPicture("C:/Dev/jdk8/javafx-src.zip"));
		assertThat(response.getStatus()).isEqualTo(Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void uploadPictureWithWrongMediaType() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPlainText("test"));
		assertThat(response.getStatus()).isEqualTo(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
	}

}
