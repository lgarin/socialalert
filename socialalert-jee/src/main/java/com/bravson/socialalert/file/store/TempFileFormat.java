package com.bravson.socialalert.file.store;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(of="extension")
public class TempFileFormat implements FileFormat {

	private final FileFormat sourceFormat;
	private final String extension = "." + System.currentTimeMillis() + ".tmp"; 
	
	@Override
	public String getContentType() {
		return sourceFormat.getContentType();
	}
	
	@Override
	public String getExtension() {
		return extension; 
	}
	
	@Override
	public String getSizeVariant() {
		return sourceFormat.getSizeVariant();
	}
}
