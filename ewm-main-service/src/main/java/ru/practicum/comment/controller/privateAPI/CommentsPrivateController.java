package ru.practicum.comment.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentEditDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentsPrivateController {
    private final CommentService commentService;

    // Добавить комментарий к событию
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentDto addComment(@PathVariable long userId,
                                 @PathVariable long eventId,
                                 @Valid @RequestBody CommentEditDto commentEditDto) {
        log.info("POST /users/{userId}/events/{eventId}/comments, {userid} = {}, {eventId} = {}, body = {}", userId, eventId, commentEditDto);
        return commentService.add(userId, eventId, commentEditDto);
    }

    // Изменить комментарий
    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto editComment(@PathVariable long userId,
                                  @PathVariable long commentId,
                                  @Valid @RequestBody CommentEditDto commentEditDto) {
        log.info("POST /users/{userId}/comments/{commentId}, {userid} = {}, {commentId} = {}, body = {}", userId, commentId, commentEditDto);
        return commentService.edit(userId, commentId, commentEditDto);
    }

    // Удалить комментарий
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public void deleteComment(@PathVariable long userId, @PathVariable long commentId) {
        log.info("DELETE /users/{userId}/comments/{commentId}, {userid} = {}, {commentId} = {}", userId, commentId);
        commentService.delete(commentId, userId);
    }

    // Получение определенного комментария
    @GetMapping("/users/{userId}/comments/{commentId}")
    public CommentDto getUserComment(@PathVariable long userId, @PathVariable long commentId) {
        log.info("GET /users/{userId}/comments/{commentId}, {userId} = {}, {commentId} = {}", userId, commentId);
        return commentService.getUserComment(userId, commentId);
    }

    // Получить все комментарии пользователя
    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getUserComments(@PathVariable long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                            @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info("GET /users/{userId}/comments, {userId} = {}, from = {}, size = {}", userId, from, size);
        return commentService.getUserComments(userId, from, size);
    }

    // Получить все опубликованные комментарии к событию
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventPublishedComments(@PathVariable long eventId,
                                                      @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                                      @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info("GET /events/{eventId}/comments, {eventId} = {}, from = {}, size = {}", eventId, from, size);
        return commentService.getEventPublishedComments(eventId, from, size);
    }
}
