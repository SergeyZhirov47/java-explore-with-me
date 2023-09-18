package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.OffsetPageableValidator;
import ru.practicum.common.Utils;
import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationDao;
import ru.practicum.event.repository.EventDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationDao compilationDao;
    private final EventDao eventDao;

    @Override
    @Transactional
    public CompilationDto add(CompilationCreateDto compilationCreateDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationCreateDto);

        final Set<Long> eventIds = compilationCreateDto.getEvents();
        if (nonNull(eventIds) && !eventIds.isEmpty()) {
            compilation.setEvents(new HashSet<>(eventDao.getEvents(new ArrayList<>(eventIds))));
        }

        compilation = compilationDao.save(compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(long id, CompilationUpdateDto compilationUpdateDto) {
        Compilation compilationFromDB = compilationDao.getCompilation(id);

        // Валидация.
        validateNotNullFields(compilationUpdateDto);

        // Обновление.
        CompilationMapper.updateIfDifferent(compilationFromDB, compilationUpdateDto);

        final Set<Long> eventIds = compilationUpdateDto.getEvents();
        if (nonNull(eventIds) && !eventIds.isEmpty()) {
            compilationFromDB.setEvents(new HashSet<>(eventDao.getEvents(new ArrayList<>(eventIds))));
        }

        compilationFromDB = compilationDao.save(compilationFromDB);

        return CompilationMapper.toCompilationDto(compilationFromDB);
    }

    @Override
    @Transactional
    public void delete(long id) {
        compilationDao.checkCompilationExists(id);
        compilationDao.delete(id);
    }

    @Override
    public CompilationDto getCompilation(long id) {
        final Compilation compilation = compilationDao.getCompilation(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getFilteredCompilations(boolean pinned, Integer from, Integer size) {
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size);
        final List<Compilation> filtered = compilationDao.getFilteredCompilations(pinned, pageable);

        return filtered.stream().map(CompilationMapper::toCompilationDto).collect(toUnmodifiableList());
    }

    private void validateNotNullFields(CompilationUpdateDto compilationUpdateDto) {
        validateTitle(compilationUpdateDto.getTitle());
    }

    private void validateTitle(String title) {
        Utils.validateLengthOfNullableString(title, 1, 50);
    }
}
