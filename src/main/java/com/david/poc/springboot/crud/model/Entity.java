package com.david.poc.springboot.crud.model;

import com.david.poc.springboot.crud.error.exception.BusinessError;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Entity {

  private static final String ERROR_MESSAGE_ID_MANDATORY = "Id is mandatory for this operation";
  private static final String ERROR_MESSAGE_ID_NOT_ALLOWED = "Id can't be specified";
  private static final String ERROR_MESSAGE_UPDATE_NOT_ALLOWED = "Last update can't be specified";
  private static final String ERROR_MESSAGE_CREATED_NOT_ALLOWED = "Creation can't be specified";

  /**
   * Generates the new Id for the entity and the dates of creation and update of
   * it.
   *
   * @param <T> {@link Entity}
   * @return the entity with generated new id and dates of creation and update
   */
  public <T extends Entity> T preCreate() {
    if (Objects.nonNull(idGetter().get())) {
      throw new BusinessError(ERROR_MESSAGE_ID_NOT_ALLOWED);
    }
    if (Objects.nonNull(getCreation())) {
      throw new BusinessError(ERROR_MESSAGE_CREATED_NOT_ALLOWED);
    }
    if (Objects.nonNull(getLastUpdate())) {
      throw new BusinessError(ERROR_MESSAGE_UPDATE_NOT_ALLOWED);
    }
    final Instant now = Instant.now();
    this.idSetter().apply(UUID.randomUUID()).setCreation(now)
        .setLastUpdate(now);
    return (T) this;
  }

  /**
   * Adds the dates of creation and update and validates the Id is present.
   *
   * @param currentVersion current version of the entity
   * @param <T>            {@link Entity}
   * @return the entity with the updated dates
   */
  public <T extends Entity> T preUpdate(T currentVersion) {
    if (Objects.isNull(idGetter().get())) {
      throw new BusinessError(ERROR_MESSAGE_ID_MANDATORY);
    }
    if (Objects.nonNull(getCreation())) {
      throw new BusinessError(ERROR_MESSAGE_CREATED_NOT_ALLOWED);
    }
    if (Objects.nonNull(getLastUpdate())) {
      throw new BusinessError(ERROR_MESSAGE_UPDATE_NOT_ALLOWED);
    }
    this.setCreation(currentVersion.getCreation());
    this.setLastUpdate(Instant.now());
    return (T) this;
  }

  /**
   * Verifies the id to perform the operation is present.
   *
   * @param <T> {@link Entity}
   * @return the validated Entity
   */
  public <T extends Entity> T preByIdOperation() {
    if (Objects.isNull(idGetter().get())) {
      throw new BusinessError(ERROR_MESSAGE_ID_MANDATORY);
    }
    return (T) this;
  }

  abstract Function<UUID, Entity> idSetter();

  abstract Supplier<UUID> idGetter();

  public abstract Instant getCreation();

  public abstract <T extends Entity> T setCreation(Instant date);

  public abstract Instant getLastUpdate();

  public abstract <T extends Entity> T setLastUpdate(Instant date);
}
