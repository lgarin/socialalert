package com.bravson.socialalert.test.repository;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.tag.MediaTagEntity;
import com.bravson.socialalert.business.media.tag.MediaTagRepository;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Transactional
public class MediaTagRepositoryTest extends BaseRepositoryTest {
	
	@Inject
	private MediaTagRepository repository;

	private void addTestData() {
		persistAndIndex(new MediaTagEntity("test"));
		persistAndIndex(new MediaTagEntity("other"));
	}
	
	@Test
	public void searchExactTag() {
		addTestData();
		List<String> result = repository.suggestTags("test");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithCapitalLetters() {
		addTestData();
		List<String> result = repository.suggestTags("TEST");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithAccentedLetters() {
		addTestData();
		List<String> result = repository.suggestTags("têst");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWith2Letters() {
		addTestData();
		List<String> result = repository.suggestTags("te");
		assertThat(result).isEmpty();
	}
	
	@Test
	public void searchTagWith3Letters() {
		addTestData();
		List<String> result = repository.suggestTags("tes");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithBadStart() {
		addTestData();
		List<String> result = repository.suggestTags("pest");
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithBadEnd() {
		addTestData();
		List<String> result = repository.suggestTags("tesp");
		assertThat(result).containsExactly("test");
	}
}
