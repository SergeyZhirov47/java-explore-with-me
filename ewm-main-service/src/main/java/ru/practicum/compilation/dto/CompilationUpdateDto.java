package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CompilationUpdateDto {
    private String title;
    private Set<Long> events;
    private boolean pinned;
}
