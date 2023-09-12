package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryChangesDto;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(long id);

    void delete(long id);

    CategoryDto add(CategoryChangesDto categoryChangesDto);

    CategoryDto update(long id, CategoryChangesDto categoryChangesDto);
}
