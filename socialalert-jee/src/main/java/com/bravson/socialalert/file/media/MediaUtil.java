package com.bravson.socialalert.file.media;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public interface MediaUtil {
	
	public static BufferedImage readImage(String filename) {
		try {
			return ImageIO.read(MediaUtil.class.getClassLoader().getResourceAsStream(filename));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read image " + filename, e);
		}
	}
}
