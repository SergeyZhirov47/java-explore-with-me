package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CategoryChangesDto {
    @NotBlank
    private String name;
}
