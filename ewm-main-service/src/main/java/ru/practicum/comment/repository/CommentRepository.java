package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByEventId(long eventId, Pageable pageable);

    List<Comment> findAllByAuthorId(long authorId, Pageable pageable);

    List<Comment> findAllByEventIdAndStatusIn(long eventId, List<CommentStatus> commentStatuses, Pageable pageable);
}
