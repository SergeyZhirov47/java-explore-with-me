package ru.practicum.category.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody CategoryChangesDto categoryChangesDto) {
        log.info("POST /admin/categories/, body = {}", categoryChangesDto);
        return categoryService.add(categoryChangesDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        log.info("DELETE /admin/categories/{catId}, {catId} = {}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto change(@PathVariable long catId, @Valid @RequestBody CategoryChangesDto categoryChangesDto) {
        log.info("PATCH /admin/categories/{catId}, {catId} = {}, body = {}", catId, categoryChangesDto);
        return categoryService.update(catId, categoryChangesDto);
    }
}
