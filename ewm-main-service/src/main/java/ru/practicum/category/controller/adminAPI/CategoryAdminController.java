package ru.practicum.category.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryChangesDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto add(@Valid @RequestBody CategoryChangesDto categoryChangesDto) {
        log.info(String.format("POST /admin/categories/, body = %s", categoryChangesDto));
        return categoryService.add(categoryChangesDto);
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable long catId) {
        log.info(String.format("DELETE /admin/categories/{catId}, {catId} = %s", catId));
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto change(@PathVariable long catId, @Valid @RequestBody CategoryChangesDto categoryChangesDto) {
        log.info(String.format("PATCH /admin/categories/{catId}, {catId} = %s, body = %s", catId, categoryChangesDto));
        return categoryService.update(catId, categoryChangesDto);
    }
}
