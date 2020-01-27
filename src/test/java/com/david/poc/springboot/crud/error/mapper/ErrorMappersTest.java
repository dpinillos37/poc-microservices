package com.david.poc.springboot.crud.error.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.david.poc.springboot.crud.error.exception.BusinessError;
import com.david.poc.springboot.crud.error.exception.InternalApplicationError;
import java.util.stream.Stream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ErrorMappersTest {

  private static Stream<Arguments> getExceptionMappers() {
    return Stream.of(
        Arguments.of(
            new ApplicationHandledErrorMapper(),
            new BusinessError("Business error"),
            Response.Status.CONFLICT.getStatusCode(),
            "Business error"),
        Arguments.of(
            new HttpErrorMapper(),
            new WebApplicationException(),
            new WebApplicationException().getResponse().getStatus(),
            null),
        Arguments.of(
            new InternalApplicationErrorMapper(),
            new InternalApplicationError(),
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
            null));
  }

  @ParameterizedTest
  @MethodSource("getExceptionMappers")
  @DisplayName("Test response generated with each exception mapper")
  <T extends RuntimeException> void generateResponse(
      ExceptionMapper<T> exceptionMapper, T exception, int status,
      Object entity) {
    final Response response = exceptionMapper.toResponse(exception);
    assertEquals(status, response.getStatus());
    assertEquals(entity, response.getEntity());
  }
}
