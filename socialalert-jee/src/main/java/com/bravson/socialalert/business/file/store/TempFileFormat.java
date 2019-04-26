package com.bravson.socialalert.business.file.store;

import com.bravson.socialalert.domain.media.format.FileFormat;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(of="extension")
public class TempFileFormat implements FileFormat {

	private final FileFormat sourceFormat;
	private final String extension;
	
	public TempFileFormat(FileFormat sourceFormat) {
		this.sourceFormat = sourceFormat;
		this.extension = "." + System.currentTimeMillis() + ".tmp" + sourceFormat.getExtension();
	}
	
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
