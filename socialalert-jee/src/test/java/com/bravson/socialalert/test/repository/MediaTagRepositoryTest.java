package com.bravson.socialalert.test.repository;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bravson.socialalert.business.media.tag.MediaTagEntity;
import com.bravson.socialalert.business.media.tag.MediaTagRepository;

public class MediaTagRepositoryTest extends BaseRepositoryTest {
	
	private MediaTagRepository repository = new MediaTagRepository(getEntityManager());

	@Before
	public void addTestData() {
		persistAndIndex(new MediaTagEntity("test"));
		persistAndIndex(new MediaTagEntity("other"));
	}
	
	@Test
	public void searchExactTag() {
		List<String> result = repository.suggestTags("test");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWith2Letters() {
		List<String> result = repository.suggestTags("te");
		assertThat(result).isEmpty();;
	}
	
	@Test
	public void searchTagWith3Letters() {
		List<String> result = repository.suggestTags("tes");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithBadSpelling() {
		List<String> result = repository.suggestTags("pest");
		assertThat(result).containsExactly("test");
	}
}
