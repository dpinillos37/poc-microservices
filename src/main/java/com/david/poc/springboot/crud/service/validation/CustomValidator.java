package com.david.poc.springboot.crud.service.validation;

import com.david.poc.springboot.crud.model.Entity;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomValidator {

  private final Validator validator;

  /**
   * Performs the validations over an entity according its annotations.
   *
   * @param entity entity to validate
   * @param <T>    {@link Entity}
   */
  public <T extends Entity> void validate(T entity) {
    Set<ConstraintViolation<T>> constraintViolations = validator
        .validate(entity);
    if (!constraintViolations.isEmpty()) {
      throw new ConstraintViolationException("There are validation errors",
          constraintViolations);
    }
  }
}
