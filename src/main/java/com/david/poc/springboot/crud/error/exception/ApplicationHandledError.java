package com.david.poc.springboot.crud.error.exception;

import javax.ws.rs.core.Response.StatusType;

public abstract class ApplicationHandledError extends RuntimeException {

  protected ApplicationHandledError(String message) {
    super(message);
  }

  public abstract StatusType getStatus();
}
