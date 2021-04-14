package com.bravson.socialalert.business.file.media;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import lombok.NonNull;

public interface MediaUtil {
	
	public static BufferedImage readImage(@NonNull String filename) {
		try (FileInputStream stream = new FileInputStream(filename)) {
			return ImageIO.read(stream);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read image " + filename, e);
		}
	}
}
