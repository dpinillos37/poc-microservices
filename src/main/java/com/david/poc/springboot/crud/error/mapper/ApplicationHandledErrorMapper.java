package com.david.poc.springboot.crud.error.mapper;

import com.david.poc.springboot.crud.error.exception.ApplicationHandledError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Provider
public class ApplicationHandledErrorMapper implements
    ExceptionMapper<ApplicationHandledError> {

  protected final Log logger = LogFactory.getLog(getClass());

  @Override
  public Response toResponse(ApplicationHandledError error) {
    return Response.status(error.getStatus()).entity(error.getMessage())
        .build();
  }
}
