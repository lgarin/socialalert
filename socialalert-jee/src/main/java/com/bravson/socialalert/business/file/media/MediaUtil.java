package com.bravson.socialalert.business.file.media;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.NonNull;

public interface MediaUtil {
	
	public static BufferedImage readImage(@NonNull String filename) {
		try {
			return ImageIO.read(MediaUtil.class.getClassLoader().getResourceAsStream(filename));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read image " + filename, e);
		}
	}
}