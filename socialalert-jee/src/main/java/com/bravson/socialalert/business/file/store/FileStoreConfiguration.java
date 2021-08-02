package com.bravson.socialalert.business.file.store;

import java.io.File;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;

@ConfigMapping(prefix = "store", namingStrategy = NamingStrategy.VERBATIM)
public interface FileStoreConfiguration {

	public File baseDirectory();
}
