package com.bravson.socialalert.file.picture;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_MEDIA_TYPE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaUtil;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import lombok.val;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@ManagedBean
public class PictureFileProcessor implements MediaFileProcessor {
	
	private MediaConfiguration config;
	
	private BufferedImage watermarkImage;
	
	@Inject
	public PictureFileProcessor(MediaConfiguration config) {
		this.config = config;
		watermarkImage = MediaUtil.readImage(config.getWatermarkFile());
	}

	@Override
	public MediaMetadata parseMetadata(File sourceFile) throws JpegProcessingException, IOException {
		val metadata = JpegMetadataReader.readMetadata(sourceFile);
		
		if (metadata.hasErrors()) {
			val errorList = new ArrayList<String>();
			for (val directory : metadata.getDirectories()) {
			   for (val error : directory.getErrors()) {
				   errorList.add(error);
			   }
	        }
			throw new JpegProcessingException(errorList.stream().collect(Collectors.joining("; ")));
		}
		
		val builder = MediaMetadata.builder();
		
		val exifTags = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (exifTags != null) {
			val dateTime = exifTags.getDate(ExifIFD0Directory.TAG_DATETIME);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
			builder.cameraMaker(exifTags.getString(ExifIFD0Directory.TAG_MAKE));
			builder.cameraModel(exifTags.getString(ExifIFD0Directory.TAG_MODEL));
			builder.height(exifTags.getInteger(ExifIFD0Directory.TAG_Y_RESOLUTION));
			builder.width(exifTags.getInteger(ExifIFD0Directory.TAG_X_RESOLUTION));
		}
		
		val exifSubTags = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		if (exifSubTags != null) {
			val dateTime = exifSubTags.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
		}
		
		val jpegTags = metadata.getFirstDirectoryOfType(JpegDirectory.class);
		if (jpegTags != null) {
			builder.height(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT));
			builder.width(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_WIDTH));
		}
		
		val gpsTags = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsTags != null) {
			val location = gpsTags.getGeoLocation();
			if (location != null) {
				builder.latitude(location.getLatitude());
				builder.longitude(location.getLongitude());
			}
		}
		
		return builder.build();
	}
	
	@Override
	public File createThumbnail(File sourceFile) throws IOException {
		val thumbnailFile = new File(sourceFile.getParent(), config.getThumbnailPrefix() + sourceFile.getName() + "." + JPG_EXTENSION);
		Thumbnails.of(sourceFile).watermark(Positions.CENTER, watermarkImage, 0.25f).size(config.getThumbnailWidth(), config.getThumbnailHeight()).crop(Positions.CENTER).outputFormat(JPG_EXTENSION).toFile(thumbnailFile);
		return thumbnailFile;
	}
	
	@Override
	public File createPreview(File sourceFile) throws IOException {
		val previewFile = new File(sourceFile.getParent(), config.getPreviewPrefix() + sourceFile.getName() + "." + JPG_EXTENSION);
		Thumbnails.of(sourceFile).watermark(Positions.CENTER, watermarkImage, 0.25f).size(config.getPreviewWidth(), config.getPreviewHeight()).outputFormat(JPG_EXTENSION).toFile(previewFile);
		return previewFile;
	}
	
	@Override
	public String getPreviewContentType() {
		return JPG_MEDIA_TYPE;
	}
}
