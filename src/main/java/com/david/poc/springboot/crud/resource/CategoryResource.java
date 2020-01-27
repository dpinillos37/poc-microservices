package com.david.poc.springboot.crud.resource;

import static com.david.poc.springboot.crud.resource.util.RestResponseMapper.handleException;
import static com.david.poc.springboot.crud.resource.util.RestResponseMapper.handleSuccess;
import static com.david.poc.springboot.crud.resource.util.RestResponseMapper.handleSuccessCreation;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.david.poc.springboot.crud.model.Category;
import com.david.poc.springboot.crud.service.base.ServiceCrud;
import java.util.UUID;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@Resource
@Path(CategoryResource.CATEGORIES_PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryResource {

  static final String CATEGORIES_PATH = "/elections/v1/categories/";
  private final ServiceCrud<Category> categoryService;

  /**
   * Creation resource for category.
   *
   * @param request       category
   * @param asyncResponse async manager
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void create(@RequestBody Category request,
      @Suspended AsyncResponse asyncResponse) {
    supplyAsync(() -> categoryService.create(request))
        .thenAccept(
            entity ->
                handleSuccessCreation(
                    asyncResponse,
                    String.join(EMPTY, CATEGORIES_PATH,
                        entity.getIdCategory().toString())))
        .exceptionally(exception -> handleException(asyncResponse, exception));
  }

  /**
   * Modification resource for update category.
   *
   * @param idCategory    id of the category to modify
   * @param request       category updated information
   * @param asyncResponse async manager
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{idCategory}")
  public void update(
      @PathParam("idCategory") UUID idCategory,
      @RequestBody Category request,
      @Suspended AsyncResponse asyncResponse) {
    runAsync(() -> categoryService.update(request.setIdCategory(idCategory)))
        .thenRun(() -> handleSuccess(asyncResponse, null))
        .exceptionally(exception -> handleException(asyncResponse, exception));
  }

  /**
   * Resource for removing a category.
   *
   * @param idCategory    id of the category to delete
   * @param asyncResponse async manager
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{idCategory}")
  public void delete(
      @PathParam("idCategory") UUID idCategory,
      @Suspended AsyncResponse asyncResponse) {
    runAsync(
        () -> categoryService.delete(new Category().setIdCategory(idCategory)))
        .thenRun(() -> handleSuccess(asyncResponse, null))
        .exceptionally(exception -> handleException(asyncResponse, exception));
  }

  /**
   * Resource to obtain all categories created.
   *
   * @param asyncResponse async manager
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public void getAll(@Suspended AsyncResponse asyncResponse) {
    supplyAsync(() -> categoryService.getAll(new Category()))
        .thenAccept(entity -> handleSuccess(asyncResponse, entity))
        .exceptionally(exception -> handleException(asyncResponse, exception));
  }

  /**
   * Resource to obtain one category.
   *
   * @param idCategory    id of the category to obtain
   * @param asyncResponse async manager
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{idCategory}")
  public void get(
      @PathParam("idCategory") UUID idCategory,
      @Suspended AsyncResponse asyncResponse) {
    supplyAsync(
        () -> categoryService.get(new Category().setIdCategory(idCategory)))
        .thenAccept(entity -> handleSuccess(asyncResponse, entity))
        .exceptionally(exception -> handleException(asyncResponse, exception));
  }
}
