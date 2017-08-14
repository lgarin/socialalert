package com.bravson.socialalert.file.video;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.infrastructure.util.DateUtil.parseInstant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaUtil;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Global;
import io.humble.video.KeyValueBag;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class BaseVideoFileProcessor implements MediaFileProcessor {
	private static final DateTimeFormatter TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
			.parseStrict()
			.appendPattern("yyyy-MM-dd HH:mm:ss")
			.parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
			.toFormatter()
			.withZone(ZoneOffset.UTC);
	
	private static final Pattern LOCATION_PATTERN = Pattern.compile("([+-]\\d+.\\d+)([+-]\\d+.\\d+)([+-]\\d+.\\d+)/");
	
	protected MediaConfiguration config;
	
	protected BufferedImage watermarkImage;
	
	private SnapshotCache snapshotCache;
	
	protected BaseVideoFileProcessor(@NonNull MediaConfiguration config, @NonNull SnapshotCache snapshotCache) {
		this.config = config;
		this.snapshotCache = snapshotCache;
		System.setProperty("java.library.path", config.getVideoLibraryPath());
		watermarkImage = MediaUtil.readImage(config.getWatermarkFile());
	}
	
	private static MediaPicture buildPicture(Demuxer demuxer, DemuxerStream stream, long delay) throws InterruptedException, IOException {
		Decoder decoder = stream.getDecoder();
		decoder.open(null, null);
		MediaPicture picture = MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
		MediaPacket packet = MediaPacket.make();
		// TODO use delay
		while (demuxer.read(packet) >= 0) {
			if (packet.getStreamIndex() == stream.getIndex()) {
				if (VideoUtil.decodePicture(packet, decoder, picture)) {
					return picture;
				}
			}
		}
	
		if (VideoUtil.decodePicture(null, decoder, picture)) {
			return picture;
		}
	
		return null;
	}
	
	@SneakyThrows(IOException.class)
	private BufferedImage takeSnapshot(File sourceFile) {
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		Demuxer demuxer = Demuxer.make();
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			DemuxerStream stream = VideoUtil.getStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
			MediaPicture picture = buildPicture(demuxer, stream, config.getSnapshotDelay());
			MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture);
			return converter.toImage(null, picture);
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			VideoUtil.closeDemuxer(demuxer);
		}
	}
	
	protected BufferedImage getSnapshot(File sourceFile) {
		if (snapshotCache == null) {
			return takeSnapshot(sourceFile);
		}
		return snapshotCache.memoized(sourceFile, this::takeSnapshot);
	}
	
	@Override
	public MediaFileFormat createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(getSnapshot(sourceFile))
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getThumbnailWidth(), config.getThumbnailHeight())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
		return getThumbnailFormat();
	}
	
	@Override
	public MediaMetadata parseMetadata(@NonNull File sourceFile) throws IOException {
		MediaMetadata.MediaMetadataBuilder result = MediaMetadata.builder();
		Demuxer demuxer = Demuxer.make();
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			KeyValueBag metadata = demuxer.getMetaData();
		    result.cameraModel(metadata.getValue("model"));
		    result.cameraMaker(metadata.getValue("make"));
		    if (metadata.getValue("creation_time") != null) {
		    	result.timestamp(parseInstant(metadata.getValue("creation_time"), TIMESTAMP_FORMAT));
		    } else if (metadata.getValue("date") != null) {
		    	result.timestamp(parseInstant(metadata.getValue("date"), TIMESTAMP_FORMAT));
		    }
		    if (metadata.getValue("location") != null) {
		    	Matcher locationMatcher = LOCATION_PATTERN.matcher(metadata.getValue("location"));
			    if (locationMatcher.matches()) {
			    	result.latitude(Double.parseDouble((locationMatcher.group(1))));
			    	result.longitude(Double.parseDouble((locationMatcher.group(2))));
			    }
		    }
		    
		    result.duration(Duration.ofSeconds(demuxer.getDuration() / Global.DEFAULT_PTS_PER_SECOND));
		    
		    DemuxerStream stream = VideoUtil.getStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
		    Decoder decoder = stream.getDecoder();
		    result.height(decoder.getHeight());
	        result.width(decoder.getWidth());
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			VideoUtil.closeDemuxer(demuxer);
		}
		
		return result.build();
	}

}
