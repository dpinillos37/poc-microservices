package com.david.poc.springboot.crud.model;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

import com.david.poc.springboot.crud.Config;
import com.david.poc.springboot.crud.service.validation.CustomValidator;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CategoryTest {

  public static final String CATEGORY_NAME = "categoryName";
  public static final String MUST_NOT_BE_NULL = "must not be null";
  public static final String SIZE_ERROR = "size must be between 1 and 20";
  public static final String CONSTRAINT_VIOLATION_ERROR = "There are validation errors";
  private final CustomValidator validations;

  private static Stream<Arguments> getExceptions() {
    return Stream.of(
        Arguments.of(getValidEntity().setIdCategory(null), MUST_NOT_BE_NULL),
        Arguments.of(getValidEntity().setCategoryName(null), MUST_NOT_BE_NULL),
        Arguments.of(getValidEntity().setCategoryName(EMPTY), SIZE_ERROR),
        Arguments.of(getValidEntity().setCategoryName("123456789012345678901"),
            SIZE_ERROR),
        Arguments.of(getValidEntity().setCreation(null), MUST_NOT_BE_NULL),
        Arguments.of(getValidEntity().setLastUpdate(null), MUST_NOT_BE_NULL));
  }

  private static Category getValidEntity() {
    return new Category()
        .setCategoryName(CATEGORY_NAME)
        .setIdCategory(UUID.randomUUID())
        .setCreation(Instant.now())
        .setLastUpdate(Instant.now());
  }

  @Test
  @DisplayName("No exceptions")
  void ok() {
    validations.validate(getValidEntity());
  }

  @ParameterizedTest
  @MethodSource("getExceptions")
  @DisplayName("Verify exceptions")
  void verifyExceptions(Category category, String expectedError) {
    final ConstraintViolationException exception =
        Assertions.assertThrows(
            ConstraintViolationException.class,
            () -> validations.validate(category));

    assertThat(exception).hasMessage(CONSTRAINT_VIOLATION_ERROR);
    final Set<ConstraintViolation<?>> constraintViolations = exception
        .getConstraintViolations();
    assertThat(constraintViolations).hasSize(1);
    final ConstraintViolation<?> error = constraintViolations.iterator().next();
    Assertions.assertEquals(expectedError, error.getMessage());
  }

  @Test
  @DisplayName("Verify multiple exceptions")
  void verifyMultipleExceptions() {
    final ConstraintViolationException exception =
        Assertions.assertThrows(
            ConstraintViolationException.class,
            () -> validations.validate(new Category()));

    assertThat(exception).hasMessage(CONSTRAINT_VIOLATION_ERROR);
    final Set<ConstraintViolation<?>> constraintViolations = exception
        .getConstraintViolations();
    assertThat(constraintViolations).hasSize(4);
    for (ConstraintViolation<?> error : constraintViolations) {
      Assertions.assertEquals(MUST_NOT_BE_NULL, error.getMessage());
    }
  }
}
