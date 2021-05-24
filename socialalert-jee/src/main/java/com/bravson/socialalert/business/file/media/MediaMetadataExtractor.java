package com.bravson.socialalert.business.file.media;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.util.ProcessUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaMetadataExtractor {

	private static final DateTimeFormatter timestampFormat = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral(':')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendOffset("+HH:MM", "")
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();
	
	@Inject
	MediaConfiguration config;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	public MediaMetadataExtractor(@NonNull MediaConfiguration config) {
		this.config = config;
	}
	
	public MediaMetadata parseMetadata(File inputFile) throws IOException {
		
		if (!inputFile.canRead()) {
			throw new IOException("Cannot read file " + inputFile);
		}
		
		List<String> arguments = Arrays.asList("-j", "-n", inputFile.getAbsolutePath());
		// -d "%Y-%m-%dT%H:%M:%S%z" -c "%.6f"
		
		StringBuilder output = new StringBuilder(16000);
		int exitCode = ProcessUtil.execute(config.getMetadataProgram(), arguments, output);
		
		if (exitCode != 0) {
			logger.error(output.toString());
			throw new IOException("Cannot process file " + inputFile);
		}
		
		try (JsonReader reader = Json.createReader(new StringReader(output.toString()))) {
			JsonArray array = reader.readArray();
			if (array == null) {
				logger.error(output.toString());
				throw new IOException("Cannot process file " + inputFile);
			}
	
			JsonObject item = array.get(0).asJsonObject();
			return parseMediaMetadata(item);
		}
	}

	private MediaMetadata parseMediaMetadata(JsonObject item) {
		return MediaMetadata.builder()
			.width(readInt(item, "ImageWidth"))
			.height(readInt(item, "ImageHeight"))
			.latitude(readDouble(item, "GPSLatitude"))
			.longitude(readDouble(item, "GPSLongitude"))
			.duration(readDuration(item, "MediaDuration"))
			.timestamp(readTimestamp(item, "ContentCreateDate", "CreationDate", "CreateDate"))
			.cameraMaker(readString(item, "Make"))
			.cameraModel(readString(item, "Model"))
			.build();
	}
	
	private static JsonValue findFirstDefinedValue(JsonObject item, String... names) {
		for (String name : names) {
			JsonValue value = item.get(name);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
	
	private static Double readDouble(JsonObject item, String... names) {
		JsonValue value = findFirstDefinedValue(item, names);
		if (value instanceof JsonNumber) {
			return ((JsonNumber) value).doubleValue();
		}
		return null;
	}
	
	private static int readInt(JsonObject item, String... names) {
		JsonValue value = findFirstDefinedValue(item, names);
		if (value instanceof JsonNumber) {
			return ((JsonNumber) value).intValue();
		}
		return 0;
	}
	
	private static Duration readDuration(JsonObject item, String... names) {
		Double seconds = readDouble(item, names);
		if (seconds != null) {
			int intSeconds = seconds.intValue();
			int intMilliseconds = (int) ((seconds.doubleValue() - intSeconds) * 1000.0);
			return Duration.ofSeconds(intSeconds, intMilliseconds * 1_000_000L);
		}
		return null;
	}
	
	private static String readString(JsonObject item, String... names) {
		JsonValue value = findFirstDefinedValue(item, names);
		if (value instanceof JsonString) {
			return ((JsonString) value).getString();
		}
		return null;
	}
	
	private static Instant readTimestamp(JsonObject item, String... names) {
		String value = readString(item, names);
		if (value != null) {
			return Instant.from(timestampFormat.parse(value));
		}
		return null;
	}
}
