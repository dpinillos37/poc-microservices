package com.david.poc.springboot.crud.model;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Category extends Entity {

  @NotNull
  private UUID idCategory;
  @NotNull
  private Instant creation;
  @NotNull
  private Instant lastUpdate;

  @NotNull
  @Size(min = 1, max = 20)
  private String categoryName;

  @Override
  Function<UUID, Entity> idSetter() {
    return this::setIdCategory;
  }

  @Override
  Supplier<UUID> idGetter() {
    return this::getIdCategory;
  }
}
