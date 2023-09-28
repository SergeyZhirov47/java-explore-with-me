package ru.practicum.comment.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentsChangeStatusDto;
import ru.practicum.comment.dto.CommentsChangeStatusResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Utils.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    // Получить все комментарии к событию, которые еще не проходили модерацию
    @GetMapping("admin/events/{eventId}/comments/unmoderated")
    public List<CommentDto> getEventUnmoderatedComments(@PathVariable long eventId,
                                                        @RequestParam(required = false) String text,
                                                        @RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime createdDateStart,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PARAM_FORMAT_PATTERN) LocalDateTime createdDateEnd,
                                                        @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                                        @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info("GET admin/events/{eventId}/comments/unmoderated, {eventId} = {}, text = {}, users = {}, from = {}, size = {}", eventId, text, users, from, size);
        return commentService.getEventUnmoderatedComments(eventId, text, users, createdDateStart, createdDateEnd, from, size);
    }

    // Смена статуса комментариев
    @PatchMapping("admin/comments")
    public CommentsChangeStatusResponseDto changeEventCommentStatus(@Valid @RequestBody CommentsChangeStatusDto commentsChangeStatusDto) {
        log.info("PATCH admin/comments, body = {}", commentsChangeStatusDto);
        return commentService.changeCommentsStatus(commentsChangeStatusDto);
    }
}
