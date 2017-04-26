package com.bravson.socialalert.file.store;

import java.io.File;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ManagedBean
@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter(AccessLevel.NONE)
public class FileStoreConfiguration {

	@Resource(name="mediaBaseDirectory")
	private String baseDirectory;
	
	public File getBaseDirectory() {
		return new File(baseDirectory);
	}
}
