package com.bravson.socialalert.infrastructure.rest;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

	private static final long serialVersionUID = -8824662792141248549L;

	public ConflictException() {
        super(Response.Status.CONFLICT);
    }
}
