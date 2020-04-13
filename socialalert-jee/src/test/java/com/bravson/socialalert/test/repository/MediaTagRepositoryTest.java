package com.bravson.socialalert.test.repository;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.tag.MediaTagEntity;
import com.bravson.socialalert.business.media.tag.MediaTagRepository;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaTagRepositoryTest extends BaseRepositoryTest {
	
	@Inject
	MediaTagRepository repository;

	private void addTestData() {
		persistAndIndex(new MediaTagEntity("test"));
		persistAndIndex(new MediaTagEntity("other"));
	}
	
	@Test
	public void searchExactTag() {
		addTestData();
		List<String> result = repository.suggestTags("test", 10);
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithCapitalLetters() {
		addTestData();
		List<String> result = repository.suggestTags("TEST", 10);
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithAccentedLetters() {
		addTestData();
		List<String> result = repository.suggestTags("têst", 10);
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWith2Letters() {
		addTestData();
		List<String> result = repository.suggestTags("te", 10);
		assertThat(result).isEmpty();
	}
	
	@Test
	public void searchTagWith3Letters() {
		addTestData();
		List<String> result = repository.suggestTags("tes", 10);
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithBadStart() {
		addTestData();
		List<String> result = repository.suggestTags("pest", 10);
		assertThat(result).containsExactly("test");
	}
	
	@Test
	public void searchTagWithBadEnd() {
		addTestData();
		List<String> result = repository.suggestTags("tesp", 10);
		assertThat(result).containsExactly("test");
	}
}
