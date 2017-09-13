package com.bravson.socialalert.test.service;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.bravson.socialalert.user.session.UserSessionService;

public class UserSessionServiceTest extends BaseServiceTest {

	@InjectMocks
	UserSessionService sessionService;
	
	@Test
	public void addNewViewedMedia() {
		boolean result = sessionService.addViewedMedia("media1");
		assertThat(result).isTrue();
	}
	
	@Test
	public void addAlreadyViewedMedia() {
		sessionService.addViewedMedia("media1");
		boolean result = sessionService.addViewedMedia("media1");
		assertThat(result).isFalse();
	}
}
