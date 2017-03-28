package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.Principal;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.drew.imaging.jpeg.JpegProcessingException;

@Path("/file")
@ManagedBean
@RolesAllowed("user")
public class FileService {

	@Resource(name="maxUploadSize")
	Long maxUploadSize;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	ThumbnailRepository thumbnailRepository;
	
	@Inject
	PreviewRepository previewRepository;
	
	@Inject
	PictureFileProcessor pictureFileProcessor;
	
	@Inject
	private Principal principal;

	@POST
	@Consumes(FileConstants.JPG_MEDIA_TYPE)
	@Path("/uploadPicture")
	public Response uploadPicture(File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		if (request.getContentLengthLong() > maxUploadSize) {
			return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
		}
		
		PictureFileMetadata metadata = new PictureFileMetadata();
		metadata.setFileMetadata(buildFileMetadata(request));
		
		try {
			metadata.setPictureMetadata(pictureFileProcessor.parseJpegMetadata(inputFile));
		} catch (JpegProcessingException e) {
			return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
		}
	
		String fileId = mediaRepository.storePicture(metadata, inputFile);
		
		File preview = pictureFileProcessor.createJpegPreview(inputFile);
		previewRepository.storeDerived(fileId, FileConstants.JPG_MEDIA_TYPE, preview);
		
		File thumbnail = pictureFileProcessor.createJpegThumbnail(inputFile);
		thumbnailRepository.storeDerived(fileId, FileConstants.JPG_MEDIA_TYPE, thumbnail);
		
		return Response.created(URI.create("file/download/" + fileId)).build();
	}

	private FileMetadata buildFileMetadata(HttpServletRequest request) {
		FileMetadata metadata = new FileMetadata();
		metadata.setContentType(request.getContentType());
		metadata.setContentLength(request.getContentLengthLong());
		metadata.setUserId(principal.getName());
		metadata.setIpAddress(request.getRemoteAddr());
		return metadata;
	}
	
	private Response doDownload(FileRepository repository, String fileId) {
		FileEntity file = repository.findFile(fileId).orElse(null);
		if (file == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
            	repository.retrieveFile(fileId, os);
            }
        };
        
        ResponseBuilder response = Response.ok(stream, file.getContentType());
		response.header("Content-Disposition", "attachment; filename=\"" + fileId + "\"");
        response.header("Content-Length", file.getLength());
		return response.build();
	}

	@GET
	@Path("/download/{fileId}")
	public Response download(@PathParam("fileId") String fileId) {
		return doDownload(mediaRepository, fileId);
	}
	
	@GET
	@Path("/preview/{fileId}")
	public Response preview(@PathParam("fileId") String fileId) {
		return doDownload(previewRepository, fileId);
	}
	
	@GET
	@Path("/thumbnail/{fileId}")
	public Response thumbnail(@PathParam("fileId") String fileId) {
		return doDownload(thumbnailRepository, fileId);
	}
}
