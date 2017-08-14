package com.bravson.socialalert.test.integration;

import java.io.File;
import java.util.Arrays;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.data.Offset;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.media.ClaimPictureParameter;
import com.bravson.socialalert.media.GeoAddress;
import com.bravson.socialalert.media.MediaInfo;

public class MediaClaimTest extends BaseIntegrationTest {

	private static Entity<ClaimPictureParameter> getClaimPictureParameter() {
		ClaimPictureParameter param = new ClaimPictureParameter();
		param.setTitle("Test title");
		param.setDescription("Test desc");
		param.setTags(Arrays.asList("tag1", "tag2"));
		param.setCategories(Arrays.asList("cat1"));
		param.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		return Entity.entity(param, MediaType.APPLICATION_JSON_TYPE);
	}
	
	@Test
	@RunAsClient
	public void claimNonExistingPicture() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/claim/picture/20170407/58b28c6b28011a1ad4180419", MediaType.APPLICATION_JSON, token).post(getClaimPictureParameter());
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	private static Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	private String uploadPicture(String token) {
		Response upload = createAuthRequest("/upload/picture", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(upload.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		return getLocationPath(upload).replace("/file/download/", "");
	}
	
	@Test
	@RunAsClient
	public void claimExistingPicture() {
		String token = requestLoginToken("test@test.com", "123");
		String uri = uploadPicture(token);
		Response response = createAuthRequest("/claim/picture/" + uri, MediaType.APPLICATION_JSON, token).post(getClaimPictureParameter());
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		MediaInfo result = response.readEntity(MediaInfo.class); 
		assertThat(result).isNotNull();
		assertThat(result.getCreator().getUsername()).isEqualTo("test@test.com");
		assertThat(result.getCreator().isOnline()).isTrue();
		assertThat(result.getTitle()).isEqualTo("Test title");
		assertThat(result.getDescription()).isEqualTo("Test desc");
		assertThat(result.getCountry()).isEqualTo("CH");
		assertThat(result.getLocality()).isEqualTo("Bern");
		assertThat(result.getLongitude()).isCloseTo(7.45, Offset.offset(0.001));
		assertThat(result.getLatitude()).isCloseTo(46.95, Offset.offset(0.001));
		assertThat(result.getCameraMaker()).isEqualTo("Apple");
		assertThat(result.getCameraModel()).isEqualTo("iPhone 5");
		assertThat(result.getHitCount()).isEqualTo(0);
		assertThat(result.getLikeCount()).isEqualTo(0);
		assertThat(result.getCategories()).containsExactly("cat1");
		assertThat(result.getTags()).containsExactly("tag1", "tag2");
		assertThat(result.getUserApprovalModifier()).isNull();
	}
}
