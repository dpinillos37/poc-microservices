package com.david.poc.springboot.crud.service;

import com.david.poc.springboot.crud.dao.mapper.CategoryMapper;
import com.david.poc.springboot.crud.error.exception.BusinessError;
import com.david.poc.springboot.crud.error.exception.InternalApplicationError;
import com.david.poc.springboot.crud.model.Category;
import com.david.poc.springboot.crud.service.base.ServiceCrud;
import com.david.poc.springboot.crud.service.validation.CustomValidator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryHandler implements ServiceCrud<Category> {

  private final CategoryMapper categoryMapper;
  private final CustomValidator validations;

  @Override
  public Category create(Category object) {
    validations.validate(object.preCreate());
    verifyNotDuplicated(object);
    verifyOneRowUpdated(categoryMapper.insert(object));
    return object;
  }

  @Override
  public Category update(Category object) {
    final Category currentVersion = getCurrentVersion(object.getIdCategory());
    validations.validate(object.preUpdate(currentVersion));
    verifyOneRowUpdated(categoryMapper.update(object));
    return object;
  }

  @Override
  public void delete(Category object) {
    object.preByIdOperation();
    verifyOneRowUpdated(categoryMapper.delete(object));
  }

  @Override
  public Category get(Category object) {
    object.preByIdOperation();
    return categoryMapper.selectOne(object);
  }

  @Override
  public List<Category> getAll(Category object) {
    return categoryMapper.selectAll(object);
  }

  private void verifyOneRowUpdated(int modifiedRows) {
    if (modifiedRows != 1) {
      throw new InternalApplicationError();
    }
  }

  private void verifyNotDuplicated(Category object) {
    if (Objects.nonNull(
        categoryMapper.selectByName(new Category().setCategoryName(object.getCategoryName())))) {
      throw new BusinessError("Duplicated object");
    }
  }

  private Category getCurrentVersion(UUID id) {
    final Category category = categoryMapper.selectOne(new Category().setIdCategory(id));
    return Optional.ofNullable(category)
        .orElseThrow(() -> new BusinessError("Object doesn't exist"));
  }
}
