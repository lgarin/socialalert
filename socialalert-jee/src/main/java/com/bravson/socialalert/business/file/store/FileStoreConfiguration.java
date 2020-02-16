package com.bravson.socialalert.business.file.store;

import java.io.File;

import io.quarkus.arc.config.ConfigProperties;
import io.quarkus.arc.config.ConfigProperties.NamingStrategy;

@ConfigProperties(prefix = "file", namingStrategy = NamingStrategy.VERBATIM)
public interface FileStoreConfiguration {

	public File getBaseDirectory();
}
