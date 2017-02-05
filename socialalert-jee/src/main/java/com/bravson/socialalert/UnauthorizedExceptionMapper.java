package com.bravson.socialalert;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
    }
}
