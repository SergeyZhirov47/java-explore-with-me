package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class UserCreateDto {
    @Length(min = 2, max = 250)
    @NotBlank
    private String name;
    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;
}
