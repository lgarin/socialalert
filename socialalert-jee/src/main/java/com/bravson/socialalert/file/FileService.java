package com.bravson.socialalert.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.hibernate.validator.constraints.NotEmpty;

@Path("/file")
@ManagedBean
@RolesAllowed("user")
public class FileService {

	@Resource(name="maxUploadSize")
	Long maxUploadSize;
	
	@Inject
	FileRepository fileRepository;

	@POST
	@Consumes(MediaType.WILDCARD) // TODO limit
	@Path("/upload")
	public Response upload(InputStream input, @NotEmpty @HeaderParam("filename") String filename, @Context HttpServletRequest request) throws IOException, ServletException {
		if (filename == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (request.getContentLengthLong() > maxUploadSize) {
			return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
		}
		String fileId = fileRepository.storeFile(filename, request.getContentType(), request.getContentLengthLong(), input);
		return Response.created(URI.create("file/download/" + fileId)).build();
	}
	
	@GET
	@Path("/download/{fileId}")
	public Response download(@PathParam("fileId") String fileId) {
		FileEntity file = fileRepository.findFile(fileId);
		if (file == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                fileRepository.retrieveFile(file, os);
            }
        };
        
        ResponseBuilder response = Response.ok(stream, file.getContentType());
		response.header("Content-Disposition", "attachment; filename=\"" + fileId + "\"");
        response.header("Content-Length", file.getLength());
		return response.build();
	}
}
