package com.bravson.socialalert.business.file.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;

@ManagedBean
@RequestScoped
public class SnapshotCache {

	private final HashMap<File, BufferedImage> cache = new HashMap<>();
	
	public BufferedImage memoized(File key, Function<? super File, ? extends BufferedImage> mappingFunction) {
		return cache.computeIfAbsent(key, mappingFunction);
	}
}
