package com.bravson.socialalert.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@ManagedBean
public class PictureFileProcessor {
	@Resource(name="pictureThumbnailPrefix")
	private String thumbnailPrefix;
	
	@Resource(name="pictureThumbnailHeight")
	private int thumbnailHeight;
	
	@Resource(name="pictureThumbnailWidth")
	private int thumbnailWidth;
	
	@Resource(name="picturePreviewPrefix")
	private String previewPrefix;
	
	@Resource(name="picturePreviewHeight")
	private int previewHeight;
	
	@Resource(name="picturePreviewWidth")
	private int previewWidth;
	
	@Resource(name="pictureWatermarkFile")
	private String watermarkFile;
	
	private BufferedImage watermarkImage;
	
	@PostConstruct
	protected void init() {
		try {
			watermarkImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(watermarkFile));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read watermark file", e);
		}
	}
	
	public PictureMetadata parseJpegMetadata(File sourceFile) throws JpegProcessingException, IOException {
		Metadata metadata = JpegMetadataReader.readMetadata(sourceFile);
		
		if (metadata.hasErrors()) {
			ArrayList<String> errorList = new ArrayList<>();
			for (Directory directory : metadata.getDirectories()) {
			   for (String error : directory.getErrors()) {
				   errorList.add(error);
			   }
	        }
			throw new JpegProcessingException(errorList.stream().collect(Collectors.joining("; ")));
		}
		
		PictureMetadata result = new PictureMetadata();
		
		ExifIFD0Directory exifTags = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (exifTags != null) {
			Date dateTime = exifTags.getDate(ExifIFD0Directory.TAG_DATETIME);
			if (dateTime != null) {
				result.setTimestamp(dateTime.toInstant());
			}
			result.setCameraMaker(exifTags.getString(ExifIFD0Directory.TAG_MAKE));
			result.setCameraModel(exifTags.getString(ExifIFD0Directory.TAG_MODEL));
			result.setHeight(exifTags.getInteger(ExifIFD0Directory.TAG_Y_RESOLUTION));
			result.setWidth(exifTags.getInteger(ExifIFD0Directory.TAG_X_RESOLUTION));
		}
		
		ExifSubIFDDirectory exifSubTags = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		if (exifSubTags != null) {
			Date dateTime = exifSubTags.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (dateTime != null) {
				result.setTimestamp(dateTime.toInstant());
			}
		}
		
		JpegDirectory jpegTags = metadata.getFirstDirectoryOfType(JpegDirectory.class);
		if (jpegTags != null) {
			result.setHeight(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT));
			result.setWidth(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_WIDTH));
		}
		
		GpsDirectory gpsTags = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsTags != null) {
			GeoLocation location = gpsTags.getGeoLocation();
			if (location != null) {
				result.setLatitude(location.getLatitude());
				result.setLongitude(location.getLongitude());
			}
		}
		
		return result;
	}
	
	public File createJpegThumbnail(File sourceFile) throws IOException {
		File thumbnailFile = new File(sourceFile.getParent(), thumbnailPrefix + sourceFile.getName());
		Thumbnails.of(sourceFile).watermark(Positions.CENTER, watermarkImage, 0.25f).size(thumbnailWidth, thumbnailHeight).crop(Positions.CENTER).outputFormat("jpg").toFile(thumbnailFile);
		return thumbnailFile;
	}
	
	public File createJpegPreview(File sourceFile) throws IOException {
		File thumbnailFile = new File(sourceFile.getParent(), previewPrefix + sourceFile.getName());
		Thumbnails.of(sourceFile).watermark(Positions.CENTER, watermarkImage, 0.25f).size(previewWidth, previewHeight).outputFormat("jpg").toFile(thumbnailFile);
		return thumbnailFile;
	}
}
