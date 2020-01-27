package com.david.poc.springboot.crud;

import com.david.poc.springboot.crud.error.mapper.ApplicationHandledErrorMapper;
import com.david.poc.springboot.crud.error.mapper.HttpErrorMapper;
import com.david.poc.springboot.crud.error.mapper.InternalApplicationErrorMapper;
import com.david.poc.springboot.crud.resource.CategoryResource;
import com.david.poc.springboot.crud.resource.HelloResource;
import com.david.poc.springboot.crud.service.validation.CustomValidator;
import javax.validation.Validation;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Config extends ResourceConfig {

  /**
   * Adding the resources, properties and exceptions mappings.
   */
  public Config() {
    super.register(HelloResource.class)
        .register(CategoryResource.class)
        .register(InternalApplicationErrorMapper.class)
        .register(ApplicationHandledErrorMapper.class)
        .register(HttpErrorMapper.class)
        .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, Boolean.TRUE);
  }

  @Bean
  public CustomValidator utilCustomValidations() {
    return new CustomValidator(
        Validation.buildDefaultValidatorFactory().getValidator());
  }
}
