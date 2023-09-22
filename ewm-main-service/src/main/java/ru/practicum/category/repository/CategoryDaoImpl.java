package ru.practicum.category.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.common.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryDaoImpl implements CategoryDao {
    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategory(long id) {
        final Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.orElseThrow(() -> new NotFoundException(String.format("Категория с id = %s не найдена!", id)));
    }

    @Override
    public List<Category> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).getContent();
    }

    @Override
    public void checkCategoryExists(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Категория с id = %s не найдена!", id));
        }
    }

    @Override
    public void delete(long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}
