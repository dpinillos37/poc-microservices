package com.david.poc.springboot.crud.dao.base;

import java.util.List;

public interface MapperCrud<T> {

  int insert(T object);

  int update(T object);

  int delete(T object);

  T selectOne(T object);

  T selectByName(T object);

  List<T> selectAll(T object);
}
