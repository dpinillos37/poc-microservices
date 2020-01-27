package com.david.poc.springboot.crud.error.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Provider
public class HttpErrorMapper implements
    ExceptionMapper<WebApplicationException> {

  protected final Log logger = LogFactory.getLog(getClass());

  @Override
  public Response toResponse(WebApplicationException internalError) {
    logger.error(internalError.getMessage(), internalError);
    return internalError.getResponse();
  }
}
