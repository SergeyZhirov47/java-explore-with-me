package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CommentEditDto {
    @Length(min = 2, max = 1000)
    private String text;
}
