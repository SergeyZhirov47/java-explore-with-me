package ru.practicum.comment.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.model.QComment;
import ru.practicum.common.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class CommentDaoImpl implements CommentDao {
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CommentDaoImpl(EntityManager entityManager, CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> saveAll(Iterable<Comment> comments) {
        return commentRepository.saveAll(comments);
    }

    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getComment(long id) {
        final Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElseThrow(() -> new NotFoundException(String.format("Комментарий с id = %s не найден!", id)));
    }

    @Override
    public List<Comment> getComments(Iterable<Long> ids) {
        return commentRepository.findAllById(ids);
    }

    @Override
    public List<Comment> getUserComments(long authorId, Pageable pageable) {
        return commentRepository.findAllByAuthorId(authorId, pageable);
    }

    @Override
    public List<Comment> getEventComments(long eventId, Pageable pageable) {
        return commentRepository.findAllByEventId(eventId, pageable);
    }

    @Override
    public List<Comment> getEventCommentsByStatus(long eventId, List<CommentStatus> commentStatuses, Pageable pageable) {
        return commentRepository.findAllByEventIdAndStatusIn(eventId, commentStatuses, pageable);
    }

    @Override
    public List<Comment> getEventUnmoderatedComments(long eventId,
                                                     String text,
                                                     List<Long> users,
                                                     LocalDateTime createdDateStart,
                                                     LocalDateTime createdDateEnd,
                                                     Pageable pageable) {
        final QComment qComment = QComment.comment;

        final BooleanBuilder booleanBuilder = new BooleanBuilder(qComment.event().id.eq(eventId)
                .and(qComment.status.eq(CommentStatus.CREATED)));
        if (nonNull(text) && !text.isBlank()) {
            booleanBuilder.and(qComment.text.containsIgnoreCase(text));
        }
        if (nonNull(users) && !users.isEmpty()) {
            booleanBuilder.and(qComment.author().id.in(users));
        }
        if (nonNull(createdDateStart)) {
            booleanBuilder.and(qComment.created.after(createdDateStart));
        }
        if (nonNull(createdDateEnd)) {
            booleanBuilder.and(qComment.created.before(createdDateEnd));
        }

        final JPAQuery<Comment> searchQuery = queryFactory.selectFrom(qComment).where(booleanBuilder);
        final Querydsl querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(QComment.class));

        return querydsl.applyPagination(pageable, searchQuery).fetch();
    }
}
