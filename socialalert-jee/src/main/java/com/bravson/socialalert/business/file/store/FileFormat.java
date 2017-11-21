package com.bravson.socialalert.business.file.store;

public interface FileFormat {

	String getContentType();
	String getSizeVariant();
	String getExtension();
}
