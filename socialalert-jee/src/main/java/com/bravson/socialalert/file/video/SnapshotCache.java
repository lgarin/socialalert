package com.bravson.socialalert.file.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;

import com.bravson.socialalert.infrastructure.cache.ScopedCache;

@ManagedBean
@RequestScoped
public class SnapshotCache extends ScopedCache<File, BufferedImage> {

}
