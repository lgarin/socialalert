package com.bravson.socialalert.file.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.annotation.ManagedBean;

import com.bravson.socialalert.infrastructure.cache.RequestScopeCache;

@ManagedBean
public class SnapshotCache extends RequestScopeCache<File, BufferedImage> {

}
