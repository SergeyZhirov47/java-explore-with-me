package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CompilationCreateDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
    private Set<Long> events;
    private boolean pinned;
}
