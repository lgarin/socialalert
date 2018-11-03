package com.bravson.socialalert.infrastructure.rest;

import javax.ejb.ApplicationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

@ApplicationException(rollback=true)
public class ConflictException extends ClientErrorException {

	private static final long serialVersionUID = -8824662792141248549L;

	public ConflictException() {
        super(Response.Status.CONFLICT);
    }
}
