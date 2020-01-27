package com.david.poc.springboot.crud.error.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Provider
public class InternalApplicationErrorMapper implements
    ExceptionMapper<Throwable> {

  protected final Log logger = LogFactory.getLog(getClass());

  @Override
  public Response toResponse(Throwable internalError) {
    logger.error(internalError.getMessage(), internalError);
    return Response.serverError().build();
  }
}
