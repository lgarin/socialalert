package com.bravson.socialalert.file;

import org.bson.Document;

import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

@NoArgsConstructor
@AllArgsConstructor
class MediaFileMetadata {

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private FileMetadata fileMetadata;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private MediaMetadata mediaMetadata;
	
	public Document toBson() {
		val result = fileMetadata.toBson();
		result.putAll(mediaMetadata.toBson());
		return result;
	}
}
