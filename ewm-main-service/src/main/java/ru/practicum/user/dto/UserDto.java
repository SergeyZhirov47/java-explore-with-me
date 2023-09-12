package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    private String email;
}
