package com.bravson.socialalert.file.video;

import static com.bravson.socialalert.file.media.MediaFileConstants.JPG_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_EXTENSION;
import static com.bravson.socialalert.file.media.MediaFileConstants.MP4_MEDIA_TYPE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.bravson.socialalert.file.media.MediaFileProcessor;

import io.humble.video.AudioChannel;
import io.humble.video.AudioFormat;
import io.humble.video.Codec;
import io.humble.video.Coder.Flag;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Encoder;
import io.humble.video.FilterGraph;
import io.humble.video.Global;
import io.humble.video.KeyValueBag;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverterFactory;
import lombok.val;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@ManagedBean
public class VideoFileProcessor implements MediaFileProcessor {

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final Pattern LOCATION_PATTERN = Pattern.compile("([+-]\\d+.\\d+)([+-]\\d+.\\d+)([+-]\\d+.\\d+)/");
	
	@Resource(name="videoSnapshotDelay")
	private long snapshotDelay;
	
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
	
	private MediaPicture buildPicture(Demuxer demuxer, DemuxerStream stream) throws InterruptedException, IOException {
		val decoder = stream.getDecoder();
		decoder.open(null, null);
		val picture = MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
		val packet = MediaPacket.make();
		while (demuxer.read(packet) >= 0) {
			if (packet.getStreamIndex() == stream.getIndex()) {
				if (decodePicture(packet, decoder, picture)) {
					return picture;
				}
			}
		}

		if (decodePicture(null, decoder, picture)) {
			return picture;
		}

		return null;
	}

	private BufferedImage createSnapshot(File sourceFile) throws IOException {
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		val demuxer = Demuxer.make();
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			val stream = findStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
			val picture = buildPicture(demuxer, stream);
			val converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture);
			return converter.toImage(null, picture);
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			closeDemuxer(demuxer);
		}
	}
	
	@Override
	public File createPreview(File sourceFile) throws IOException {
		val outputFile = new File(sourceFile.getParent(), previewPrefix + sourceFile.getName() + "." + MP4_EXTENSION);
		watermark(sourceFile, outputFile);
		return outputFile;
	}
	
	@Override
	public String getPreviewContentType() {
		return MP4_MEDIA_TYPE;
	}

	@Override
	public File createThumbnail(File sourceFile) throws IOException {
		val thumbnailFile = new File(sourceFile.getParent(), thumbnailPrefix + sourceFile.getName() + "." + JPG_EXTENSION);
		Thumbnails.of(createSnapshot(sourceFile)).watermark(Positions.CENTER, watermarkImage, 0.25f).size(thumbnailWidth, thumbnailHeight).crop(Positions.CENTER).outputFormat(JPG_EXTENSION).toFile(thumbnailFile);
		return thumbnailFile;
	}
	
	private DemuxerStream findStream(Demuxer demuxer, MediaDescriptor.Type type) throws IOException {
		try {
			int ns = demuxer.getNumStreams();
			for (int i = 0; i < ns; i++) {
				DemuxerStream stream = demuxer.getStream(i);
				Decoder decoder = stream.getDecoder();
				if (decoder != null && decoder.getCodecType() == type) {
					return stream;
				}
			}
			return null;
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public VideoMetadata parseMetadata(File sourceFile) throws IOException {
		val result = new VideoMetadata();
		val demuxer = Demuxer.make();
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			val metadata = demuxer.getMetaData();
		    result.setCameraModel(metadata.getValue("model"));
		    result.setCameraMaker(metadata.getValue("make"));
		    if (metadata.getValue("creation_time") != null) {
		    	result.setTimestamp(TIMESTAMP_FORMAT.parse(metadata.getValue("creation_time")));
		    } else if (metadata.getValue("date") != null) {
		    	result.setTimestamp(TIMESTAMP_FORMAT.parse(metadata.getValue("date")));
		    }
		    if (metadata.getValue("location") != null) {
		    	val locationMatcher = LOCATION_PATTERN.matcher(metadata.getValue("location"));
			    if (locationMatcher.matches()) {
			    	result.setDefaultLatitude(Double.parseDouble((locationMatcher.group(1))));
			    	result.setDefaultLongitude(Double.parseDouble((locationMatcher.group(2))));
			    }
		    }
		    
		    result.setDuration(Duration.ofSeconds(demuxer.getDuration() / Global.DEFAULT_PTS_PER_SECOND));
		    
		    val stream = findStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
		    if (stream != null) {
		    	val decoder = stream.getDecoder();
		    	result.setHeight(decoder.getHeight());
	        	result.setWidth(decoder.getWidth());
		    }
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			closeDemuxer(demuxer);
		}
		
		return result;
	}
	
	private void watermark(File sourceFile, File outputFile) throws IOException {
		
		val demuxer = Demuxer.make();
		val format = MuxerFormat.guessFormat(null, outputFile.getName(), null);
		val muxer = Muxer.make(outputFile.toString(), format, null);
		
		val inputPacket = MediaPacket.make();
		val audioPacket = MediaPacket.make();
		val videoPacket = MediaPacket.make();
		
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			val videoStream = findStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
			val videoDecoder = videoStream.getDecoder();
			videoDecoder.open(null, null);
			
			val audioStream = findStream(demuxer, MediaDescriptor.Type.MEDIA_AUDIO);
			val audioDecoder = audioStream.getDecoder();
			audioDecoder.open(null, null);
			
			val videoEncoder = createVideoEncoder(format, videoDecoder.getTimeBase());
			val audioEncoder = createAudioEncoder(format);
			
			muxer.addNewStream(videoEncoder);
			muxer.addNewStream(audioEncoder);
			val muxerOptions = KeyValueBag.make();
			//muxerOptions.setValue("movflags", "faststart");
			muxerOptions.setValue("moov_size", "100000");
			muxer.open(muxerOptions, null);
			
			val watermarkPicture = createWatermarkPicture();
			
			val metadata = videoStream.getMetaData();
			val rotation = metadata.getValue("rotate");
			int rotationAngle = 0;
			if (rotation != null) {
				rotationAngle = Integer.parseInt(rotation);
			}
			
			val videoGraph = FilterGraph.make();
			val videoSource = videoGraph.addPictureSource("input", videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat(), videoDecoder.getTimeBase(), null);
			val watermark = videoGraph.addPictureSource("watermark", watermarkPicture.getWidth(), watermarkPicture.getHeight(), watermarkPicture.getFormat(), videoDecoder.getTimeBase(), null);
			val videoSink = videoGraph.addPictureSink("output", videoEncoder.getPixelFormat());
			videoGraph.open("[watermark] lutrgb='a=128' [over];[input] scale='h=-1:w=" + previewWidth + ":force_original_aspect_ratio=decrease', rotate='oh=" + previewHeight + ":ow=" + previewWidth + ":a=" + rotationAngle + "*PI/180' [vid]; [vid][over] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2' [output]");
			
			val audioGraph = FilterGraph.make();
			val audioSource = audioGraph.addAudioSource("input", audioDecoder.getSampleRate(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat(), audioDecoder.getTimeBase());
			val audioSink = audioGraph.addAudioSink("output", audioEncoder.getSampleRate(), audioEncoder.getChannelLayout(), audioEncoder.getSampleFormat());
			audioGraph.open("[input] aformat='sample_fmts=s16:sample_rates=44100:channel_layouts=mono' [output]");
			
			val sourceAudio = MediaAudio.make(audioDecoder.getFrameSize(), audioDecoder.getSampleRate(), audioDecoder.getChannels(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat());
			val targetAudio = MediaAudio.make(audioEncoder.getFrameSize(), audioEncoder.getSampleRate(), audioEncoder.getChannels(), audioEncoder.getChannelLayout(), audioEncoder.getSampleFormat());
			val targetPicture = MediaPicture.make(videoEncoder.getWidth(), videoEncoder.getHeight(), videoEncoder.getPixelFormat());
			val sourcePicture = MediaPicture.make(videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat());

			while (demuxer.read(inputPacket) >= 0) {
				if (inputPacket.isComplete()) {
					if (audioStream.getIndex() == inputPacket.getStreamIndex()) {
						if (decodeAudio(inputPacket, audioDecoder, sourceAudio)) {
							audioSource.addAudio(sourceAudio);
							if (audioSink.getAudio(targetAudio) >= 0) {
								encodeAudio(muxer, audioPacket, audioEncoder, targetAudio);
							}
						}
					} else if (videoStream.getIndex() == inputPacket.getStreamIndex()) {
						if (decodePicture(inputPacket, videoDecoder, sourcePicture)) {
							watermarkPicture.setTimeStamp(sourcePicture.getTimeStamp());
							watermark.addPicture(watermarkPicture);
							videoSource.addPicture(sourcePicture);
							if (videoSink.getPicture(targetPicture) >= 0) {
								encodePicture(muxer, videoPacket, videoEncoder, targetPicture);
							}
						}
					}

				}
			}
			
			encodePicture(muxer, videoPacket, videoEncoder, null);
			encodeAudio(muxer, audioPacket, audioEncoder, null);
			
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			closeMuxer(muxer);
			closeDemuxer(demuxer);
		}
	}

	private void closeDemuxer(Demuxer demuxer) throws IOException {
		try {
			if (demuxer.getState() == Demuxer.State.STATE_OPENED) {
				demuxer.close();
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	private void closeMuxer(Muxer muxer) {
		if (muxer.getState() == Muxer.State.STATE_OPENED) {
			muxer.close();
		}
	}

	private Encoder createVideoEncoder(MuxerFormat format, Rational timeBase) {
		val videoEncoder = Encoder.make(Codec.findEncodingCodecByName("libx264"));
		videoEncoder.setWidth(previewWidth);
		videoEncoder.setHeight(previewHeight);
		videoEncoder.setPixelFormat(PixelFormat.Type.PIX_FMT_YUV420P);
		videoEncoder.setTimeBase(Rational.make(timeBase.getNumerator() * 2, timeBase.getDenominator()));
		if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
			videoEncoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
		}
		videoEncoder.setFlag(Flag.FLAG_4MV, true);
		videoEncoder.setFlag(Flag.FLAG_LOOP_FILTER, true);
		//videoEncoder.setProperty("crf", 24L);
		videoEncoder.setProperty("preset", "fast");
		videoEncoder.setProperty("profile", "baseline");
		videoEncoder.setProperty("refs", "5");
		videoEncoder.setProperty("level", "3.0");
		//videoEncoder.setProperty("tune", "zerolatency");
		videoEncoder.open(null, null);
		return videoEncoder;
	}

	private Encoder createAudioEncoder(MuxerFormat format) {
		val audioEncoder = Encoder.make(Codec.findEncodingCodecByName("libvo_aacenc"));
		audioEncoder.setSampleRate(44100);
		audioEncoder.setChannels(1);
		audioEncoder.setChannelLayout(AudioChannel.Layout.CH_LAYOUT_MONO);
		audioEncoder.setSampleFormat(AudioFormat.Type.SAMPLE_FMT_S16);
		if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
			audioEncoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
		}
		audioEncoder.setProperty("b", 64L);
		audioEncoder.open(null, null);
		return audioEncoder;
	}

	private void encodeAudio(Muxer muxer, MediaPacket audioPacket, Encoder audioEncoder, MediaAudio targetAudio) {
		//do {
			audioEncoder.encodeAudio(audioPacket, targetAudio);
		    if (audioPacket.isComplete()) {
		      muxer.write(audioPacket, false);
		    }
		//} while (audioPacket.isComplete());
	}

	private void encodePicture(Muxer muxer, MediaPacket videoPacket, Encoder videoEncoder, MediaPicture targetPicture) {
		do {
			videoEncoder.encodeVideo(videoPacket, targetPicture);
		    if (videoPacket.isComplete()) {
		      muxer.write(videoPacket, false);
		    }
		} while (videoPacket.isComplete());
	}

	private MediaPicture createWatermarkPicture() {
		val watermarkPicture = MediaPicture.make(watermarkImage.getWidth(), watermarkImage.getHeight(), PixelFormat.Type.PIX_FMT_RGBA);
		val watermarkConverter = MediaPictureConverterFactory.createConverter(watermarkImage, watermarkPicture);
		watermarkConverter.toPicture(watermarkPicture, watermarkImage, 0);
		return watermarkPicture;
	}

	private boolean decodePicture(MediaPacket packet, Decoder decoder, MediaPicture picture) {
		val size = packet == null ? 0 : packet.getSize();
		int offset = 0;
		int bytesRead = 0;
		do {
			bytesRead += decoder.decodeVideo(picture, packet, offset);
			if (picture.isComplete()) {
				return true;
			}
			if (bytesRead <= 0) {
				throw new RuntimeException("Could not decode video");
			}
			offset += bytesRead;
		} while (offset < size);
		return false;
	}
	
	private boolean decodeAudio(MediaPacket packet, Decoder decoder, MediaAudio audio) {
		val size = packet == null ? 0 : packet.getSize();
		int offset = 0;
		int bytesRead = 0;
		do {
			bytesRead += decoder.decodeAudio(audio, packet, offset);
			if (audio.isComplete()) {
				return true;
			}
			if (bytesRead <= 0) {
				throw new RuntimeException("Could not decode audio");
			}
			offset += bytesRead;
		} while (offset < size);
		return false;
	}
}
