package com.bravson.socialalert.test.integration;

import java.io.File;
import java.util.Arrays;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.media.UpsertMediaParameter;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaViewTest extends BaseIntegrationTest {

	private static Entity<UpsertMediaParameter> getClaimMediaParameter() {
		UpsertMediaParameter param = new UpsertMediaParameter();
		param.setTitle("Test title");
		param.setTags(Arrays.asList("tag1", "tag2"));
		param.setCategory("cat1");
		param.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		return Entity.entity(param, MediaTypeConstants.JSON);
	}

	private static Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	private String uploadPicture(String token) {
		Response upload = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(upload.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		return getLocationPath(upload).replace("/file/download/", "");
	}
	
	@Test
	public void viewMediaDetail() throws InterruptedException {
		String token = requestLoginToken("test@test.com", "123");
		String uri = uploadPicture(token);
		awaitAsyncEvent(AsyncMediaProcessedEvent.class);
		Response claim = createAuthRequest("/media/claim/" + uri, MediaTypeConstants.JSON, token).post(getClaimMediaParameter());
		assertThat(claim.getStatus()).isEqualTo(Status.OK.getStatusCode());
		MediaDetail result = createAuthRequest("/media/view/" + uri, MediaTypeConstants.JSON, token).get(MediaDetail.class);
		assertThat(result).isNotNull();
		assertThat(result.getCreator().getUsername()).isEqualTo("test@test.com");
		assertThat(result.getCreator().isOnline()).isTrue();
		assertThat(result.getTitle()).isEqualTo("Test title");
		assertThat(result.getCountry()).isEqualTo("CH");
		assertThat(result.getLocality()).isEqualTo("Bern");
		assertThat(result.getLongitude()).isCloseTo(7.45, Offset.offset(0.001));
		assertThat(result.getLatitude()).isCloseTo(46.95, Offset.offset(0.001));
		assertThat(result.getCameraMaker()).isEqualTo("Apple");
		assertThat(result.getCameraModel()).isEqualTo("iPhone 5");
		assertThat(result.getHitCount()).isEqualTo(1);
		assertThat(result.getLikeCount()).isZero();
		assertThat(result.getCategory()).isEqualTo("cat1");
		assertThat(result.getTags()).containsExactly("tag1", "tag2");
	}
	
	@Test
	public void viewNonExistingMedia() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/view/uri1", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}

}
