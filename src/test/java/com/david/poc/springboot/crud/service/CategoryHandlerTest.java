package com.david.poc.springboot.crud.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.david.poc.springboot.crud.dao.mapper.CategoryMapper;
import com.david.poc.springboot.crud.error.exception.BusinessError;
import com.david.poc.springboot.crud.error.exception.InternalApplicationError;
import com.david.poc.springboot.crud.model.Category;
import com.david.poc.springboot.crud.service.base.ServiceCrud;
import com.david.poc.springboot.crud.service.validation.CustomValidator;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessException;

class CategoryHandlerTest {

  private ServiceCrud<Category> categoryHandler;
  private CategoryMapper categoryMapperMock;
  private CustomValidator validatorMock;

  @BeforeEach
  void setUp() {
    this.categoryMapperMock = mock(CategoryMapper.class);
    this.validatorMock = mock(CustomValidator.class);
    categoryHandler = new CategoryHandler(categoryMapperMock, validatorMock);
  }

  @Test
  @DisplayName("Create ok")
  void createOk() {
    Category categoryMock = mock(Category.class);
    String categoryName = "category";
    when(categoryMock.getCategoryName()).thenReturn(categoryName);

    when(categoryMapperMock.insert(categoryMock)).thenReturn(1);

    Category result = categoryHandler.create(categoryMock);

    assertEquals(categoryMock, result);
    verify(categoryMock).preCreate();
    verify(validatorMock).validate(any());
    ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor
        .forClass(Category.class);
    verify(categoryMapperMock).selectByName(categoryArgumentCaptor.capture());
    assertEquals(categoryName,
        categoryArgumentCaptor.getValue().getCategoryName());
    verify(categoryMapperMock).insert(eq(categoryMock));
  }

  @Test
  @DisplayName("Create ko - duplicated object")
  void createKoDuplicated() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectByName(any()))
        .thenReturn(mock(Category.class));

    assertThatThrownBy(() -> categoryHandler.create(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage("Duplicated object");
  }

  @Test
  @DisplayName("Create ko - error validations")
  void createKoValidationError() {
    Category categoryMock = mock(Category.class);
    doThrow(ConstraintViolationException.class).when(validatorMock)
        .validate(any());

    assertThatThrownBy(() -> categoryHandler.create(categoryMock))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  @DisplayName("Create ko - error handling the input data")
  void createKoHandlingInput() {
    Category categoryMock = mock(Category.class);
    final String errorMessage = "error handling data";
    when(categoryMock.preCreate()).thenThrow(new BusinessError(errorMessage));

    assertThatThrownBy(() -> categoryHandler.create(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage(errorMessage);
  }

  @Test
  @DisplayName("Create ko - error sql")
  void createKoSqlError() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.insert(any()))
        .thenThrow(mock(DataAccessException.class));

    assertThatThrownBy(() -> categoryHandler.create(categoryMock))
        .isInstanceOf(DataAccessException.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 10002334})
  @DisplayName("Create ko - unexpected number of updated rows")
  void createKoRowNumber(int updatedRows) {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.insert(categoryMock)).thenReturn(updatedRows);

    assertThatThrownBy(() -> categoryHandler.create(categoryMock))
        .isInstanceOf(InternalApplicationError.class)
        .hasMessage("Unexpected error. Try again later.");
  }

  @Test
  @DisplayName("Update ok")
  void updateOk() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectOne(any())).thenReturn(categoryMock);
    when(categoryMapperMock.update(categoryMock)).thenReturn(1);

    categoryHandler.update(categoryMock);

    verify(categoryMock).preUpdate(any());
    verify(validatorMock).validate(any());
    verify(categoryMapperMock).update(eq(categoryMock));
  }

  @Test
  @DisplayName("Update ko - non existent old version")
  void updateKoNoOldVersion() {
    Category categoryMock = mock(Category.class);

    assertThatThrownBy(() -> categoryHandler.update(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage("Object doesn't exist");
  }

  @Test
  @DisplayName("Update ko - error validations")
  void updateKoValidationError() {
    Category categoryMock = mock(Category.class);
    doThrow(ConstraintViolationException.class).when(validatorMock)
        .validate(any());

    when(categoryMapperMock.selectOne(any())).thenReturn(categoryMock);

    assertThatThrownBy(() -> categoryHandler.update(categoryMock))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  @DisplayName("Update ko - error handling the input data")
  void updateKoHandlingInput() {
    Category categoryMock = mock(Category.class);
    final String errorMessage = "error handling data";
    when(categoryMock.preUpdate(any()))
        .thenThrow(new BusinessError(errorMessage));

    when(categoryMapperMock.selectOne(any())).thenReturn(categoryMock);

    assertThatThrownBy(() -> categoryHandler.update(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage(errorMessage);
  }

  @Test
  @DisplayName("Update ko - error sql")
  void updateKoSqlError() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectOne(any())).thenReturn(categoryMock);
    when(categoryMapperMock.update(any()))
        .thenThrow(mock(DataAccessException.class));

    assertThatThrownBy(() -> categoryHandler.update(categoryMock))
        .isInstanceOf(DataAccessException.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 10002334})
  @DisplayName("Update ko - unexpected number of updated rows")
  void updateKoRowNumber(int updatedRows) {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectOne(any())).thenReturn(categoryMock);
    when(categoryMapperMock.update(categoryMock)).thenReturn(updatedRows);

    assertThatThrownBy(() -> categoryHandler.update(categoryMock))
        .isInstanceOf(InternalApplicationError.class)
        .hasMessage("Unexpected error. Try again later.");
  }

  @Test
  @DisplayName("Delete ok")
  void deleteOk() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.delete(categoryMock)).thenReturn(1);

    categoryHandler.delete(categoryMock);

    verify(categoryMock).preByIdOperation();
    verify(categoryMapperMock).delete(eq(categoryMock));
  }

  @Test
  @DisplayName("Delete ko - error handling the input data")
  void deleteKoHandlingInput() {
    Category categoryMock = mock(Category.class);
    final String errorMessage = "error handling data";
    when(categoryMock.preByIdOperation())
        .thenThrow(new BusinessError(errorMessage));

    assertThatThrownBy(() -> categoryHandler.delete(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage(errorMessage);
  }

  @Test
  @DisplayName("Delete ko - error sql")
  void deleteKoSqlError() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.delete(any()))
        .thenThrow(mock(DataAccessException.class));

    assertThatThrownBy(() -> categoryHandler.delete(categoryMock))
        .isInstanceOf(DataAccessException.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2, 10002334})
  @DisplayName("Delete ko - unexpected number of updated rows")
  void deleteKoRowNumber(int updatedRows) {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.delete(categoryMock)).thenReturn(updatedRows);

    assertThatThrownBy(() -> categoryHandler.delete(categoryMock))
        .isInstanceOf(InternalApplicationError.class)
        .hasMessage("Unexpected error. Try again later.");
  }

  @Test
  @DisplayName("Select ok")
  void selectOk() {
    Category categorySearched = mock(Category.class);
    Category categoryInput = mock(Category.class);

    when(categoryMapperMock.selectOne(categoryInput))
        .thenReturn(categorySearched);

    categoryHandler.get(categoryInput);

    verify(categoryInput).preByIdOperation();
    verify(categoryMapperMock).selectOne(eq(categoryInput));
  }

  @Test
  @DisplayName("Select ko - error handling the input data")
  void selectKoHandlingInput() {
    Category categoryMock = mock(Category.class);
    final String errorMessage = "error handling data";
    when(categoryMock.preByIdOperation())
        .thenThrow(new BusinessError(errorMessage));

    assertThatThrownBy(() -> categoryHandler.get(categoryMock))
        .isInstanceOf(BusinessError.class)
        .hasMessage(errorMessage);
  }

  @Test
  @DisplayName("Select ko - error sql")
  void selectKoSqlError() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectOne(any()))
        .thenThrow(mock(DataAccessException.class));

    assertThatThrownBy(() -> categoryHandler.get(categoryMock))
        .isInstanceOf(DataAccessException.class);
  }

  @Test
  @DisplayName("Select all ok")
  void selectAllOk() {
    Category categoryInput = mock(Category.class);

    when(categoryMapperMock.selectAll(categoryInput))
        .thenReturn(mock(List.class));

    categoryHandler.getAll(categoryInput);

    verify(categoryMapperMock).selectAll(eq(categoryInput));
  }

  @Test
  @DisplayName("Select all ko - error sql")
  void selectAllKoSqlError() {
    Category categoryMock = mock(Category.class);

    when(categoryMapperMock.selectAll(any()))
        .thenThrow(mock(DataAccessException.class));

    assertThatThrownBy(() -> categoryHandler.getAll(categoryMock))
        .isInstanceOf(DataAccessException.class);
  }
}
