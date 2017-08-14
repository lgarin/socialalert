package com.bravson.socialalert.file.store;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TempFileFormat implements FileFormat {

	private final FileFormat sourceFormat;
	
	@Override
	public String getContentType() {
		return sourceFormat.getContentType();
	}
	
	@Override
	public String getExtension() {
		return "." + System.currentTimeMillis() + ".tmp"; 
	}
	
	@Override
	public String getSizeVariant() {
		return sourceFormat.getSizeVariant();
	}
}
