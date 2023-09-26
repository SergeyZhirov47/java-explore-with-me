package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentEditDto commentEditDto) {
        if (isNull(commentEditDto)) {
            return null;
        }

        return Comment.builder()
                .text(commentEditDto.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        if (isNull(comment)) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .published(comment.getPublished())
                .build();
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (isNull(comments) || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toUnmodifiableList());
    }
}
