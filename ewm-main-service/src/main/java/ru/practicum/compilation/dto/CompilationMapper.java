package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.common.AbstractMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@UtilityClass
public class CompilationMapper extends AbstractMapper {
    public Compilation toCompilation(CompilationCreateDto compilationCreateDto) {
        return Compilation.builder()
                .title(compilationCreateDto.getTitle())
                .pinned(compilationCreateDto.isPinned())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        Set<EventShortDto> eventShortDtoSet = null;
        if (nonNull(compilation.getEvents())) {
            eventShortDtoSet = compilation.getEvents().stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toUnmodifiableSet());
        }

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(eventShortDtoSet)
                .build();
    }

    public void updateIfDifferent(Compilation compilation, final CompilationUpdateDto compilationUpdateDto) {
        compilation.setTitle(getChanged(compilation.getTitle(), compilationUpdateDto.getTitle()));
        compilation.setPinned(getChanged(compilation.isPinned(), compilationUpdateDto.isPinned()));
    }
}
