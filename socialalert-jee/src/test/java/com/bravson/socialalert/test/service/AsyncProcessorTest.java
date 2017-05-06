package com.bravson.socialalert.test.service;

import javax.inject.Inject;

import org.junit.Test;

import com.bravson.socialalert.file.video.AsyncViewPreviewEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

public class AsyncProcessorTest extends BaseServiceTest {
	
	@Inject
	private AsyncRepository repository;

	@Test
	public void fireAsyncEvent() throws InterruptedException {
		Thread.sleep(10000);
		repository.fireAsync(new AsyncViewPreviewEvent());
		Thread.sleep(10000);
	}

}
