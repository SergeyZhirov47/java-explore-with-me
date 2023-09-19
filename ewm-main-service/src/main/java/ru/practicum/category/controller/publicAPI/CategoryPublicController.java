package ru.practicum.category.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                           @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info(String.format("GET /categories, from = %s, size = %s", from, size));
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info(String.format("GET /categories/{catId}, {catId} = %s", catId));
        return categoryService.getCategory(catId);
    }
}
