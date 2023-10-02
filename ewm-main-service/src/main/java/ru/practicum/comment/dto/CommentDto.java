package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.comment.model.CommentStatus;

import java.time.LocalDateTime;

import static ru.practicum.common.Utils.DATE_PARAM_FORMAT_PATTERN;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private long eventId;
    private long authorId;
    private CommentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PARAM_FORMAT_PATTERN)
    private LocalDateTime published;
}
