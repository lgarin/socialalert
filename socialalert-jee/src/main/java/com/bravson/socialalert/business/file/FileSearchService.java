package com.bravson.socialalert.business.file;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileSearchService {

	@Inject
	UserInfoService userService;
	
	@Inject
	FileRepository fileRepository;
	
	public List<FileInfo> findNewFilesByUserId(@NonNull String userId) {
		return userService.fillUserInfo(fileRepository.findByUserIdAndState(userId, FileState.UPLOADED).stream().map(FileEntity::toFileInfo).collect(Collectors.toList()));
	}
}
