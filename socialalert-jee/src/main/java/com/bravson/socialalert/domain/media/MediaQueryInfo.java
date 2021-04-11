package com.bravson.socialalert.domain.media;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.location.GeoArea;

import lombok.Data;

@Schema(description="The information about live queries.")
@Data
public class MediaQueryInfo {
	
	private String id;

	private String label;
	
	private String userId;
	
	private GeoArea location;
	
	private String keywords;
	
	private String category;
	
	private int hitThreshold;
	
	private int lastHitCount;
	
	private Instant lastExecution;
}
