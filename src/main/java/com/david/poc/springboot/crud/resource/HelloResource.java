package com.david.poc.springboot.crud.resource;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

@Resource
@Path("/hello")
public class HelloResource {

  @GET
  public void hello(@Suspended AsyncResponse asyncResponse) {
    asyncResponse.resume(Response.ok().entity("Hello").build());
  }
}
