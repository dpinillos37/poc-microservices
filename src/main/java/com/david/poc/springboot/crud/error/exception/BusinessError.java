package com.david.poc.springboot.crud.error.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

public final class BusinessError extends ApplicationHandledError {

  public BusinessError(String message) {
    super(message);
  }

  @Override
  public StatusType getStatus() {
    return Response.Status.CONFLICT;
  }
}
