package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CommentsChangeStatusResponseDto {
    private List<CommentDto> updatedComments;
    private List<String> errors;
}
