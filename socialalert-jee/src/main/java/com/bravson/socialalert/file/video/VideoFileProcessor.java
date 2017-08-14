package com.bravson.socialalert.file.video;

import java.io.File;
import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.file.media.MediaConfiguration;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.infrastructure.log.Logged;

import io.humble.video.AudioChannel;
import io.humble.video.AudioFormat;
import io.humble.video.Codec;
import io.humble.video.Coder.Flag;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Encoder;
import io.humble.video.FilterAudioSink;
import io.humble.video.FilterAudioSource;
import io.humble.video.FilterGraph;
import io.humble.video.FilterPictureSink;
import io.humble.video.FilterPictureSource;
import io.humble.video.KeyValueBag;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class VideoFileProcessor extends BaseVideoFileProcessor {
	
	@Inject
	public VideoFileProcessor(@NonNull MediaConfiguration config, @NonNull SnapshotCache snapshotCache) {
		super(config, snapshotCache);
	}
	
	@Override
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Demuxer demuxer = Demuxer.make();
		MuxerFormat format = MuxerFormat.guessFormat(null, outputFile.getName(), null);
		Muxer muxer = Muxer.make(outputFile.toString(), format, null);
		
		MediaPacket inputPacket = MediaPacket.make();
		MediaPacket audioPacket = MediaPacket.make();
		MediaPacket videoPacket = MediaPacket.make();
		
		try {
			demuxer.open(sourceFile.toString(), null, false, true, null, null);
			DemuxerStream videoStream = VideoUtil.getStream(demuxer, MediaDescriptor.Type.MEDIA_VIDEO);
			Decoder videoDecoder = videoStream.getDecoder();
			videoDecoder.open(null, null);
			
			DemuxerStream audioStream = VideoUtil.getStream(demuxer, MediaDescriptor.Type.MEDIA_AUDIO);
			Decoder audioDecoder = audioStream.getDecoder();
			audioDecoder.open(null, null);
			
			Encoder videoEncoder = createVideoEncoder(format, videoDecoder.getTimeBase());
			Encoder audioEncoder = createAudioEncoder(format);
			
			muxer.addNewStream(videoEncoder);
			muxer.addNewStream(audioEncoder);
			KeyValueBag muxerOptions = KeyValueBag.make();
			//muxerOptions.setValue("movflags", "faststart");
			muxerOptions.setValue("moov_size", "100000");
			muxer.open(muxerOptions, null);
			
			MediaPicture watermarkPicture = VideoUtil.createMediaPicture(watermarkImage);
			
			KeyValueBag metadata = videoStream.getMetaData();
			String rotation = metadata.getValue("rotate");
			int rotationAngle = 0;
			if (rotation != null) {
				rotationAngle = Integer.parseInt(rotation);
			}
			
			FilterGraph videoGraph = FilterGraph.make();
			FilterPictureSource videoSource = videoGraph.addPictureSource("input", videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat(), videoDecoder.getTimeBase(), null);
			FilterPictureSource watermark = videoGraph.addPictureSource("watermark", watermarkPicture.getWidth(), watermarkPicture.getHeight(), watermarkPicture.getFormat(), videoDecoder.getTimeBase(), null);
			FilterPictureSink videoSink = videoGraph.addPictureSink("output", videoEncoder.getPixelFormat());
			videoGraph.open("[watermark] lutrgb='a=128' [over];[input] scale='h=-1:w=" + config.getPreviewWidth() + ":force_original_aspect_ratio=decrease', rotate='oh=" + config.getPreviewHeight() + ":ow=" + config.getPreviewWidth() + ":a=" + rotationAngle + "*PI/180' [vid]; [vid][over] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2' [output]");
			
			FilterGraph audioGraph = FilterGraph.make();
			FilterAudioSource audioSource = audioGraph.addAudioSource("input", audioDecoder.getSampleRate(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat(), audioDecoder.getTimeBase());
			FilterAudioSink audioSink = audioGraph.addAudioSink("output", audioEncoder.getSampleRate(), audioEncoder.getChannelLayout(), audioEncoder.getSampleFormat());
			audioGraph.open("[input] aformat='sample_fmts=s16:sample_rates=44100:channel_layouts=mono' [output]");
			
			MediaAudio sourceAudio = MediaAudio.make(audioDecoder.getFrameSize(), audioDecoder.getSampleRate(), audioDecoder.getChannels(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat());
			MediaAudio targetAudio = MediaAudio.make(audioEncoder.getFrameSize(), audioEncoder.getSampleRate(), audioEncoder.getChannels(), audioEncoder.getChannelLayout(), audioEncoder.getSampleFormat());
			MediaPicture targetPicture = MediaPicture.make(videoEncoder.getWidth(), videoEncoder.getHeight(), videoEncoder.getPixelFormat());
			MediaPicture sourcePicture = MediaPicture.make(videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat());
		
			while (demuxer.read(inputPacket) >= 0) {
				if (inputPacket.isComplete()) {
					if (audioStream.getIndex() == inputPacket.getStreamIndex()) {
						if (VideoUtil.decodeAudio(inputPacket, audioDecoder, sourceAudio)) {
							audioSource.addAudio(sourceAudio);
							if (audioSink.getAudio(targetAudio) >= 0) {
								VideoUtil.encodeAudio(muxer, audioPacket, audioEncoder, targetAudio);
							}
						}
					} else if (videoStream.getIndex() == inputPacket.getStreamIndex()) {
						if (VideoUtil.decodePicture(inputPacket, videoDecoder, sourcePicture)) {
							watermarkPicture.setTimeStamp(sourcePicture.getTimeStamp());
							watermark.addPicture(watermarkPicture);
							videoSource.addPicture(sourcePicture);
							if (videoSink.getPicture(targetPicture) >= 0) {
								VideoUtil.encodePicture(muxer, videoPacket, videoEncoder, targetPicture);
							}
						}
					}
		
				}
			}
			
			VideoUtil.encodePicture(muxer, videoPacket, videoEncoder, null);
			VideoUtil.encodeAudio(muxer, audioPacket, audioEncoder, null);
			
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			VideoUtil.closeMuxer(muxer);
			VideoUtil.closeDemuxer(demuxer);
		}
		
		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_MP4;
	}
	
	private Encoder createVideoEncoder(MuxerFormat format, Rational timeBase) {
		Encoder videoEncoder = Encoder.make(Codec.findEncodingCodecByName("libx264"));
		videoEncoder.setWidth(config.getPreviewWidth());
		videoEncoder.setHeight(config.getPreviewHeight());
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
		Encoder audioEncoder = Encoder.make(Codec.findEncodingCodecByName("libvo_aacenc"));
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
}
