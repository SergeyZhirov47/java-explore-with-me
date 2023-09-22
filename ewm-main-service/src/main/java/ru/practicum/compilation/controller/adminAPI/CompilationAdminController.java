package ru.practicum.compilation.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody CompilationCreateDto compilationCreateDto) {
        log.info("POST /admin/compilations/, body = {}", compilationCreateDto);
        return compilationService.add(compilationCreateDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.info("DELETE /admin/compilations/{compId}, {compId} = {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable long compId, @RequestBody CompilationUpdateDto compilationUpdateDto) {
        log.info("PATCH /admin/compilations/{compId}, {compId} = {}, body = {}", compId, compilationUpdateDto);
        return compilationService.update(compId, compilationUpdateDto);
    }
}
