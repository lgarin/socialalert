package com.bravson.socialalert.business.file.video;

import java.awt.image.BufferedImage;
import java.io.IOException;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Encoder;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.PixelFormat;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

public interface VideoUtil {

	static void encodeAudio(Muxer muxer, MediaPacket audioPacket, Encoder audioEncoder, MediaAudio targetAudio) {
		//do {
			audioEncoder.encodeAudio(audioPacket, targetAudio);
		    if (audioPacket.isComplete()) {
		      muxer.write(audioPacket, false);
		    }
		//} while (audioPacket.isComplete());
	}

	static void encodePicture(Muxer muxer, MediaPacket videoPacket, Encoder videoEncoder, MediaPicture targetPicture) {
		do {
			videoEncoder.encodeVideo(videoPacket, targetPicture);
		    if (videoPacket.isComplete()) {
		      muxer.write(videoPacket, false);
		    }
		} while (videoPacket.isComplete());
	}

	static boolean decodeAudio(MediaPacket packet, Decoder decoder, MediaAudio audio) {
		int size = packet == null ? 0 : packet.getSize();
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
	
	static boolean decodePicture(MediaPacket packet, Decoder decoder, MediaPicture picture) {
		int size = packet == null ? 0 : packet.getSize();
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

	static DemuxerStream getStream(Demuxer demuxer, MediaDescriptor.Type type) throws IOException {
		try {
			int ns = demuxer.getNumStreams();
			for (int i = 0; i < ns; i++) {
				DemuxerStream stream = demuxer.getStream(i);
				Decoder decoder = stream.getDecoder();
				if (decoder != null && decoder.getCodecType() == type) {
					return stream;
				}
			}
			throw new IOException("Cannot find stream " + type.name() + " in " + demuxer.getURL());
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	static void closeDemuxer(Demuxer demuxer) throws IOException {
		try {
			if (demuxer.getState() == Demuxer.State.STATE_OPENED) {
				demuxer.close();
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}
	
	static void closeMuxer(Muxer muxer) {
		if (muxer.getState() == Muxer.State.STATE_OPENED) {
			muxer.close();
		}
	}

	static MediaPicture createMediaPicture(BufferedImage image) {
		MediaPicture watermarkPicture = MediaPicture.make(image.getWidth(), image.getHeight(), PixelFormat.Type.PIX_FMT_RGBA);
		MediaPictureConverter watermarkConverter = MediaPictureConverterFactory.createConverter(image, watermarkPicture);
		watermarkConverter.toPicture(watermarkPicture, image, 0);
		return watermarkPicture;
	}
}
