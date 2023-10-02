package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentEditDto;
import ru.practicum.comment.dto.CommentsChangeStatusDto;
import ru.practicum.comment.dto.CommentsChangeStatusResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto add(long authorId, long eventId, CommentEditDto commentEditDto);

    CommentDto edit(long authorId, long commentId, CommentEditDto commentEditDto);

    void delete(long commentId, long authorId);

    CommentDto getUserComment(long authorId, long commentId);

    List<CommentDto> getUserComments(long authorId, Integer from, Integer size);

    List<CommentDto> getEventPublishedComments(long eventId, Integer from, Integer size);

    List<CommentDto> getEventUnmoderatedComments(long eventId,
                                                 String text,
                                                 List<Long> users,
                                                 LocalDateTime createdDateStart,
                                                 LocalDateTime createdDateEnd,
                                                 Integer from,
                                                 Integer size);

    CommentsChangeStatusResponseDto changeCommentsStatus(CommentsChangeStatusDto commentsChangeStatusDto);
}
