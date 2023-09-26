package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentDao {
    Comment save(Comment comment);

    List<Comment> saveAll(Iterable<Comment> comments);

    void deleteById(long id);

    Comment getComment(long id);

    List<Comment> getComments(Iterable<Long> ids);

    List<Comment> getUserComments(long authorId, Pageable pageable);

    List<Comment> getEventComments(long eventId, Pageable pageable);

    List<Comment> getEventCommentsByStatus(long eventId, List<CommentStatus> commentStatuses, Pageable pageable);

    List<Comment> getEventUnmoderatedComments(long eventId,
                                              String text,
                                              List<Long> users,
                                              LocalDateTime createdDateStart,
                                              LocalDateTime createdDateEnd,
                                              Pageable pageable);
}
