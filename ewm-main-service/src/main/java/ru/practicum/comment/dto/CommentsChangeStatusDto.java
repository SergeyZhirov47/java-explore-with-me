package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.comment.model.CommentUpdateAdminAction;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CommentsChangeStatusDto {
    @NotNull
    private Set<Long> commentIds;
    @NotNull
    private CommentUpdateAdminAction action;
}
