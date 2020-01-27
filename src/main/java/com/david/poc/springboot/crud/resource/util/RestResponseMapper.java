package com.david.poc.springboot.crud.resource.util;

import java.net.URI;
import java.util.concurrent.CompletionException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public class RestResponseMapper {

  private RestResponseMapper() {
  }

  public static Void handleSuccessCreation(AsyncResponse asyncResponse,
      String url) {
    asyncResponse.resume(Response.created(URI.create(url)).build());
    return null;
  }

  public static <T> Void handleSuccess(AsyncResponse asyncResponse, T entity) {
    asyncResponse.resume(Response.ok().entity(entity).build());
    return null;
  }

  public static Void handleException(AsyncResponse asyncResponse, Throwable e) {
    asyncResponse.resume(e instanceof CompletionException ? e.getCause() : e);
    return null;
  }
}
