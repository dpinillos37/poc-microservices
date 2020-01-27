package com.david.poc.springboot.crud.service.base;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceCrud<T> {

  @Transactional
  T create(T object);

  @Transactional
  T update(T object);

  @Transactional
  void delete(T object);

  T get(T object);

  List<T> getAll(T object);
}
