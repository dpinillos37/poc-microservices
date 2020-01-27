package com.david.poc.springboot.crud.error.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

public final class InternalApplicationError extends ApplicationHandledError {

  public InternalApplicationError() {
    super("Unexpected error. Try again later.");
  }

  @Override
  public StatusType getStatus() {
    return Response.Status.SERVICE_UNAVAILABLE;
  }
}
