package com.david.poc.springboot.crud.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.david.poc.springboot.crud.error.exception.BusinessError;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityTest {

  private static final String ERROR_MESSAGE_ID_MANDATORY = "Id is mandatory for this operation";
  private static final String ERROR_MESSAGE_ID_NOT_ALLOWED = "Id can't be specified";
  private static final String ERROR_MESSAGE_UPDATE_NOT_ALLOWED = "Last update can't be specified";
  private static final String ERROR_MESSAGE_CREATED_NOT_ALLOWED = "Creation can't be specified";

  @Test
  @DisplayName("pre creation handling ok")
  void preCreate() {
    final String otherFieldValue = "other field value";
    DummyEntity dummyEntity = new DummyEntity().setOtherField(otherFieldValue);

    final DummyEntity result = dummyEntity.preCreate();

    assertEquals(dummyEntity, result);
    assertEquals(dummyEntity.getOtherField(), result.getOtherField());
    assertNotNull(result.getCreation());
    assertNotNull(result.getLastUpdate());
    assertNotNull(result.getIdEntity());
  }

  @Test
  @DisplayName("pre creation handling ko id can not be sent")
  void preCreateKoId() {
    final String otherFieldValue = "other field value";
    DummyEntity dummyEntity = new DummyEntity().setIdEntity(UUID.randomUUID());

    assertThatThrownBy(dummyEntity::preCreate)
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_ID_NOT_ALLOWED);
  }

  @Test
  @DisplayName("pre creation handling ko updated time can not be sent")
  void preCreateKoCreated() {
    DummyEntity dummyEntity = new DummyEntity().setCreation(Instant.now());

    assertThatThrownBy(dummyEntity::preCreate)
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_CREATED_NOT_ALLOWED);
  }

  @Test
  @DisplayName("pre creation handling ko updated time can not be sent")
  void preCreateKoUpdated() {
    DummyEntity dummyEntity = new DummyEntity().setLastUpdate(Instant.now());

    assertThatThrownBy(dummyEntity::preCreate)
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_UPDATE_NOT_ALLOWED);
  }

  @Test
  @DisplayName("pre update handling ok")
  void preUpdate() {
    final String otherFieldValue = "other field value";
    DummyEntity dummyEntity =
        new DummyEntity().setOtherField(otherFieldValue)
            .setIdEntity(UUID.randomUUID());
    DummyEntity oldVersion = mock(DummyEntity.class);
    when(oldVersion.getCreation()).thenReturn(Instant.now());

    final DummyEntity result = dummyEntity.preUpdate(oldVersion);

    assertEquals(dummyEntity, result);
    assertEquals(dummyEntity.getOtherField(), result.getOtherField());
    assertEquals(result.getCreation(), oldVersion.getCreation());
    assertNotNull(result.getLastUpdate());
    assertEquals(dummyEntity.getIdEntity(), result.getIdEntity());
  }

  @Test
  @DisplayName("pre creation handling ko id mandatory")
  void preUpdateKoId() {
    DummyEntity dummyEntity = new DummyEntity();

    DummyEntity oldVersion = mock(DummyEntity.class);
    when(oldVersion.getCreation()).thenReturn(Instant.now());

    assertThatThrownBy(() -> dummyEntity.preUpdate(oldVersion))
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_ID_MANDATORY);
  }

  @Test
  @DisplayName("pre update handling ko created time can not be sent")
  void preUpdateKoCreated() {
    DummyEntity dummyEntity =
        new DummyEntity().setIdEntity(UUID.randomUUID())
            .setCreation(Instant.now());

    DummyEntity oldVersion = mock(DummyEntity.class);
    when(oldVersion.getCreation()).thenReturn(Instant.now());

    assertThatThrownBy(() -> dummyEntity.preUpdate(oldVersion))
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_CREATED_NOT_ALLOWED);
  }

  @Test
  @DisplayName("pre update handling ko updated time can not be sent")
  void preUpdateKoUpdated() {
    DummyEntity dummyEntity =
        new DummyEntity().setIdEntity(UUID.randomUUID())
            .setLastUpdate(Instant.now());

    DummyEntity oldVersion = mock(DummyEntity.class);
    when(oldVersion.getCreation()).thenReturn(Instant.now());

    assertThatThrownBy(() -> dummyEntity.preUpdate(oldVersion))
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_UPDATE_NOT_ALLOWED);
  }

  @Test
  @DisplayName("pre by id operation handling ok")
  void preByIdOperation() {
    final String otherFieldValue = "other field value";
    DummyEntity dummyEntity = new DummyEntity().setIdEntity(UUID.randomUUID());

    final DummyEntity result = dummyEntity.preByIdOperation();

    assertEquals(dummyEntity, result);
    assertNull(result.getCreation());
    assertNull(result.getLastUpdate());
    assertNull(result.getOtherField());
    assertEquals(dummyEntity.getIdEntity(), result.getIdEntity());
  }

  @Test
  @DisplayName("pre by id operation handling ko id mandatory")
  void preByIdOperationKoId() {
    DummyEntity dummyEntity = new DummyEntity();

    assertThatThrownBy(dummyEntity::preByIdOperation)
        .isInstanceOf(BusinessError.class)
        .hasMessage(ERROR_MESSAGE_ID_MANDATORY);
  }

  @Data
  @Accessors(chain = true)
  private static class DummyEntity extends Entity {

    private UUID idEntity;
    private Instant creation;
    private Instant lastUpdate;
    private String otherField;

    @Override
    Function<UUID, Entity> idSetter() {
      return this::setIdEntity;
    }

    @Override
    Supplier<UUID> idGetter() {
      return this::getIdEntity;
    }
  }
}
