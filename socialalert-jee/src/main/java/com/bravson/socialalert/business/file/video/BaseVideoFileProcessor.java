package com.bravson.socialalert.business.file.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeVideoDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class BaseVideoFileProcessor implements MediaFileProcessor {
	/*
	private static final DateTimeFormatter TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
			.parseStrict()
			.appendPattern("yyyy-MM-dd HH:mm:ss")
			.parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
			.toFormatter()
			.withZone(ZoneOffset.UTC);
	*/
	private static final Pattern LOCATION_PATTERN = Pattern.compile("([+-]\\d+.\\d+)([+-]\\d+.\\d+)([+-]\\d+.\\d+)/");
	
	protected MediaConfiguration config;
	
	protected BaseVideoFileProcessor(@NonNull MediaConfiguration config) {
		this.config = config;
	}
	
	@SneakyThrows(InterruptedException.class)
	protected File takeSnapshot(File sourceFile, File targetFile, int width, int height) throws IOException {
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		
		//String filter = "thumbnail,scale=320:240:force_original_aspect_ratio=decrease,pad=320:240:(ow-iw)/2:(oh-ih)/2";
		String filter = String.format("[0] thumbnail,scale=(iw*sar)*max(%1$d/(iw*sar)\\,%2$d/ih):ih*max(%1$d/(iw*sar)\\,%2$d/ih),crop=%1$d:%2$d [thumbnail]; [1] format=yuva420p,lutrgb='a=128' [watermark]; [thumbnail][watermark] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2'",width, height);
		
		ProcessBuilder builder = new ProcessBuilder(config.getVideoEncodingProgram(), "-i", sourceFile.getAbsolutePath(), "-i", config.getWatermarkFile(), "-f", "image2", "-frames:v", "1", "-filter_complex", filter, "-y", targetFile.getAbsolutePath());
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		System.out.println(builder.command().stream().collect(Collectors.joining(" ")));
		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while((line=br.readLine())!=null){
           System.out.println(line);
        }
		 
		int result = process.waitFor();
		if (result != 0) {
			throw new IOException("Cannot process file " + targetFile);
		}

		return targetFile;
	}

	
	@Override
	public MediaFileFormat createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		takeSnapshot(sourceFile, outputFile, config.getThumbnailWidth(), config.getThumbnailHeight());
		return getThumbnailFormat();
	}
	
	private Instant fixInstant(Instant wrongTimezone) {
		ZoneOffset offset = wrongTimezone.atZone(ZoneId.systemDefault()).getOffset();;
		return wrongTimezone.truncatedTo(ChronoUnit.SECONDS).atOffset(offset).withOffsetSameLocal(ZoneOffset.UTC).toInstant();
	}
	
	@Override
	public MediaMetadata parseMetadata(@NonNull File sourceFile) throws ImageProcessingException, IOException, MetadataException {
		
		Metadata metadata = ImageMetadataReader.readMetadata(sourceFile);
		
		if (metadata.hasErrors()) {
			ArrayList<String> errorList = new ArrayList<>();
			for (Directory directory : metadata.getDirectories()) {
			   for (String error : directory.getErrors()) {
				   errorList.add(error);
			   }
	        }
			//throw new ImageProcessingException(errorList.stream().collect(Collectors.joining("; ")));
		}
		
		MediaMetadata.MediaMetadataBuilder builder = MediaMetadata.builder();
		/*
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				System.out.println(directory.getClass().getName() + " " + tag.getTagTypeHex() + "->" + tag.getTagType() + " : " + tag);
			}
		}
		 */
		for (Directory directory : metadata.getDirectoriesOfType(Mp4VideoDirectory.class)) {
			if (directory.containsTag(Mp4VideoDirectory.TAG_HEIGHT)) {
				builder.height(directory.getInteger(Mp4VideoDirectory.TAG_HEIGHT));
			}
			if (directory.containsTag(Mp4VideoDirectory.TAG_WIDTH)) {
				builder.width(directory.getInteger(Mp4VideoDirectory.TAG_WIDTH));
			}
		}
		
		for (Directory directory : metadata.getDirectoriesOfType(Mp4Directory.class)) {
			if (directory.containsTag(Mp4Directory.TAG_DURATION)) {
				builder.duration(Duration.ofSeconds(directory.getLong(Mp4Directory.TAG_DURATION)));
			}
			if (directory.containsTag(Mp4Directory.TAG_CREATION_TIME)) {
				builder.timestamp(fixInstant(directory.getDate(Mp4Directory.TAG_CREATION_TIME).toInstant()));
			}
		}

		for (Directory directory : metadata.getDirectoriesOfType(QuickTimeDirectory.class)) {
			if (directory.containsTag(QuickTimeDirectory.TAG_DURATION)) {
				builder.duration(Duration.ofSeconds(directory.getLong(QuickTimeDirectory.TAG_DURATION)));
			}
			if (directory.containsTag(QuickTimeDirectory.TAG_CREATION_TIME)) {
				builder.timestamp(fixInstant(directory.getDate(QuickTimeDirectory.TAG_CREATION_TIME).toInstant()));
			}
		}
		
		for (Directory directory : metadata.getDirectoriesOfType(QuickTimeVideoDirectory.class)) {
			if (directory.containsTag(QuickTimeVideoDirectory.TAG_HEIGHT)) {
				builder.height(directory.getInteger(QuickTimeVideoDirectory.TAG_HEIGHT));
			}
			if (directory.containsTag(QuickTimeVideoDirectory.TAG_WIDTH)) {
				builder.width(directory.getInteger(QuickTimeVideoDirectory.TAG_WIDTH));
			}
		}
		
		for (Directory directory : metadata.getDirectoriesOfType(QuickTimeMetadataDirectory.class)) {
			for (Tag tag : directory.getTags()) {
				if (tag.getTagName().equals("Make")) {
					builder.cameraMaker(directory.getString(tag.getTagType()));
				}
				if (tag.getTagName().equals("Model")) {
					builder.cameraModel(directory.getString(tag.getTagType()));
				}
				if (tag.getTagName().equals("ISO 6709")) {
			    	Matcher locationMatcher = LOCATION_PATTERN.matcher(directory.getString(tag.getTagType()));
				    if (locationMatcher.matches()) {
				    	builder.latitude(Double.parseDouble((locationMatcher.group(1))));
				    	builder.longitude(Double.parseDouble((locationMatcher.group(2))));
				    }
				}
			}
		}
		
		return builder.build();
	}

}
