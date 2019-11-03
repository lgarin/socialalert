package com.bravson.socialalert.business.file.store;

import java.io.File;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "fileStore")
public interface FileStoreConfiguration {

	public File getBaseDirectory();
}
