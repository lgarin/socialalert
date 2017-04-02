package com.bravson.socialalert.file;

import org.bson.Document;

import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.val;

@Data
@Builder
@AllArgsConstructor
class MediaFileMetadata {

	@Getter
	private final FileMetadata fileMetadata;
	
	@Getter
	private final MediaMetadata mediaMetadata;
	
	public Document toBson() {
		val result = fileMetadata.toBson();
		result.putAll(mediaMetadata.toBson());
		return result;
	}
}
