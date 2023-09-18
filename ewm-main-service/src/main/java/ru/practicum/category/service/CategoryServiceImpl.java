package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryChangesDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryDao;
import ru.practicum.common.OffsetPageableValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        final Pageable offsetPageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Category> categories = categoryDao.getCategories(offsetPageable);

        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CategoryDto getCategory(long id) {
        categoryDao.checkCategoryExists(id);

        final Category category = categoryDao.getCategory(id);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(long id) {
        categoryDao.checkCategoryExists(id);
        categoryDao.delete(id);
    }

    @Override
    public CategoryDto add(CategoryChangesDto categoryChangesDto) {
        Category newCategory = CategoryMapper.toCategory(categoryChangesDto);

        newCategory = categoryDao.save(newCategory);
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto update(long id, CategoryChangesDto categoryChangesDto) {
        Category category = categoryDao.getCategory(id);
        category.setName(categoryChangesDto.getName());

        category = categoryDao.save(category);
        return CategoryMapper.toCategoryDto(category);
    }
}
