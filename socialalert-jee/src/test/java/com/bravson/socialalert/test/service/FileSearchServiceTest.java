package com.bravson.socialalert.test.service;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileState;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileSearchServiceTest extends BaseServiceTest {

	@InjectMocks
	FileSearchService searchService;

	@Mock
	FileRepository mediaRepository;

	@Mock
	UserInfoService userService;

	@Test
	public void findNewFileByUserIdWithNoMatch() {
		String userId = "testUser";
		when(mediaRepository.findByUserIdAndState(userId, FileState.PROCESSED)).thenReturn(Collections.emptyList());

		List<FileInfo> result = searchService.findNewFilesByUserId(userId);
		assertThat(result).isEmpty();
	}
	
	@Test
	public void findNewFileByUserIdWithSingleMatch() {
		FileEntity entity = mock(FileEntity.class);
		FileInfo fileInfo = new FileInfo();
		String userId = "testUser";
		when(mediaRepository.findByUserIdAndState(userId, FileState.PROCESSED)).thenReturn(Collections.singletonList(entity));
		when(entity.toFileInfo()).thenReturn(fileInfo);
		when(userService.fillUserInfo(Collections.singletonList(fileInfo))).thenReturn(Collections.singletonList(fileInfo));
		
		List<FileInfo> result = searchService.findNewFilesByUserId(userId);
		assertThat(result).containsExactly(fileInfo);
	}
}
