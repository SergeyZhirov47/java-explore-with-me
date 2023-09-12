package ru.practicum.category.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryDao {
    Category getCategory(long id);

    List<Category> getCategories(Pageable pageable);

    void checkCategoryExists(long id);

    void delete(long id);

    Category save(Category category);
}
