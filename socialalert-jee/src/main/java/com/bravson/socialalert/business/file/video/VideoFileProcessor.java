package com.bravson.socialalert.business.file.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class VideoFileProcessor extends BaseVideoFileProcessor {
	
	@Inject
	public VideoFileProcessor(@NonNull MediaConfiguration config) {
		super(config);
	}
	
	@SneakyThrows(InterruptedException.class)
	@Override
	public void createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		
		String filter = String.format("[0:v] scale='%1$d:%2$d:force_original_aspect_ratio=decrease',pad='%1$d:%2$d:(ow-iw)/2:(oh-ih)/2'", config.previewWidth(), config.previewHeight());
		/*
		filter += " [video]; [1] format=yuva420p,lutrgb='a=128' [watermark]; [video][watermark] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2'";
		*/
		filter += "; [0:a] aformat='sample_fmts=s16:sample_rates=48000:channel_layouts=mono'";
		
		ProcessBuilder builder = new ProcessBuilder(config.encodingProgram(), "-i", sourceFile.getAbsolutePath(), "-i", config.watermarkFile(), "-c:v", "libx264", "-preset", "fast", "-profile:v", "baseline", "-level", "3.0", "-movflags", "+faststart", "-c:a", "aac", "-b:a", "64k", "-ac", "1", "-filter_complex", filter, "-y", outputFile.getAbsolutePath());
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
			throw new IOException("Cannot process file " + outputFile);
		}
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_MP4;
	}
}
