package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    CompilationDto add(CompilationCreateDto compilationCreateDto);

    CompilationDto update(long id, CompilationUpdateDto compilationUpdateDto);

    void delete(long id);

    CompilationDto getCompilation(long id);

    List<CompilationDto> getFilteredCompilations(boolean pinned, Integer from, Integer size);
}
