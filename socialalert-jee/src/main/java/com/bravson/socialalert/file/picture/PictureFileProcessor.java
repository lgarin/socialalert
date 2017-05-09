package com.bravson.socialalert.file.picture;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaUtil;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@ManagedBean
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class PictureFileProcessor implements MediaFileProcessor {
	
	private MediaConfiguration config;
	
	private BufferedImage watermarkImage;
	
	@Inject
	public PictureFileProcessor(@NonNull MediaConfiguration config) {
		this.config = config;
		watermarkImage = MediaUtil.readImage(config.getWatermarkFile());
	}

	@Override
	public MediaMetadata parseMetadata(@NonNull File sourceFile) throws JpegProcessingException, IOException {
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
		
		MediaMetadata.MediaMetadataBuilder builder = MediaMetadata.builder();
		
		ExifIFD0Directory exifTags = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (exifTags != null) {
			Date dateTime = exifTags.getDate(ExifIFD0Directory.TAG_DATETIME);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
			builder.cameraMaker(exifTags.getString(ExifIFD0Directory.TAG_MAKE));
			builder.cameraModel(exifTags.getString(ExifIFD0Directory.TAG_MODEL));
			builder.height(exifTags.getInteger(ExifIFD0Directory.TAG_Y_RESOLUTION));
			builder.width(exifTags.getInteger(ExifIFD0Directory.TAG_X_RESOLUTION));
		}
		
		ExifSubIFDDirectory exifSubTags = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		if (exifSubTags != null) {
			Date dateTime = exifSubTags.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
		}
		
		JpegDirectory jpegTags = metadata.getFirstDirectoryOfType(JpegDirectory.class);
		if (jpegTags != null) {
			builder.height(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT));
			builder.width(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_WIDTH));
		}
		
		GpsDirectory gpsTags = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsTags != null) {
			GeoLocation location = gpsTags.getGeoLocation();
			if (location != null) {
				builder.latitude(location.getLatitude());
				builder.longitude(location.getLongitude());
			}
		}
		
		return builder.build();
	}
	
	@Override
	public void createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getThumbnailWidth(), config.getThumbnailHeight())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
	}
	
	@Override
	public void createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getPreviewWidth(), config.getPreviewHeight())
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
