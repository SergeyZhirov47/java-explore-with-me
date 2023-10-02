package ru.practicum.comment.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentsEventPrivateController {
    private final CommentService commentService;

    // Получить все опубликованные комментарии к событию
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventPublishedComments(@PathVariable long eventId,
                                                      @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                                      @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info("GET /events/{eventId}/comments, {eventId} = {}, from = {}, size = {}", eventId, from, size);
        return commentService.getEventPublishedComments(eventId, from, size);
    }
}