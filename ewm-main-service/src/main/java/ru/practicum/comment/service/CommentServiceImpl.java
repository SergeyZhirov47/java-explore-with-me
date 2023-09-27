package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.model.CommentUpdateAdminAction;
import ru.practicum.comment.repository.CommentDao;
import ru.practicum.common.OffsetPageableValidator;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventDao;
import ru.practicum.request.repository.RequestDao;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final RequestDao requestDao;

    private final Sort commentCreatedSortDesc = Sort.by("created").descending();

    @Override
    public CommentDto add(long authorId, long eventId, CommentEditDto commentEditDto) {
        final User author = userDao.getUser(authorId);
        final Event event = eventDao.getEvent(eventId);

        // Проверка, что событие опубликованное.
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalStateException("Нельзя оставлять комментарии к неопубликованному событию!");
        }

        // Проверка, что пользователь подтвержденный участник события.
        if (!requestDao.isUserConfirmedEventParticipant(authorId, eventId)) {
            throw new IllegalStateException("Пользователь не может оставлять комментарий если не является участником события!");
        }

        Comment comment = CommentMapper.toComment(commentEditDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setStatus(CommentStatus.CREATED);
        comment.setCreated(LocalDateTime.now());

        comment = commentDao.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto edit(long authorId, long commentId, CommentEditDto commentEditDto) {
        Comment commentFromDB = commentDao.getComment(commentId);

        if (commentFromDB.getAuthor().getId() != authorId) {
            throw new IllegalStateException("Комментарий может редактировать только его автор!");
        }

        if (!commentFromDB.getStatus().equals(CommentStatus.CREATED)) {
            throw new IllegalStateException("Комментарий можно редактировать только до момента публикации!");
        }

        commentFromDB.setText(commentEditDto.getText());
        commentFromDB = commentDao.save(commentFromDB);

        return CommentMapper.toCommentDto(commentFromDB);
    }

    @Override
    public void delete(long commentId, long authorId) {
        final Comment comment = commentDao.getComment(commentId);

        if (comment.getAuthor().getId() != authorId) {
            throw new IllegalArgumentException("Комментарий может удалить только его автор!");
        }

        commentDao.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getUserComment(long authorId, long commentId) {
        final Comment comment = commentDao.getComment(commentId);

        if (comment.getAuthor().getId() != authorId) {
            throw new IllegalArgumentException(String.format("Пользователь с id = %s не является автором комментария с id = %s", authorId, commentId));
        }

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(long authorId, Integer from, Integer size) {
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size, commentCreatedSortDesc);
        final List<Comment> userComments = commentDao.getUserComments(authorId, pageable);

        return CommentMapper.toCommentDtoList(userComments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventPublishedComments(long eventId, Integer from, Integer size) {
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size, commentCreatedSortDesc);
        final List<Comment> publishedComments = commentDao.getEventCommentsByStatus(eventId, List.of(CommentStatus.PUBLISHED), pageable);

        return CommentMapper.toCommentDtoList(publishedComments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventUnmoderatedComments(long eventId,
                                                        String text,
                                                        List<Long> users,
                                                        LocalDateTime createdDateStart,
                                                        LocalDateTime createdDateEnd,
                                                        Integer from,
                                                        Integer size) {
        final Pageable pageable = OffsetPageableValidator.validateAndGet(from, size, commentCreatedSortDesc);
        final List<Comment> eventUnmoderatedComments = commentDao.getEventUnmoderatedComments(eventId, text, users, createdDateStart, createdDateEnd, pageable);

        return CommentMapper.toCommentDtoList(eventUnmoderatedComments);
    }

    @Override
    public CommentsChangeStatusResponseDto changeCommentsStatus(CommentsChangeStatusDto commentsChangeStatusDto) {
        final List<Comment> comments = commentDao.getComments(commentsChangeStatusDto.getCommentIds());
        final CommentStatus newCommentStatus = mapToCommentStatus(commentsChangeStatusDto.getAction());

        List<Comment> commentsToUpdate = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Comment comment : comments) {
            final CommentStatus commentStatus = comment.getStatus();

            if (commentStatus == CommentStatus.CREATED) {
                if (newCommentStatus == CommentStatus.PUBLISHED) {
                    comment.setPublished(LocalDateTime.now());
                }
                comment.setStatus(newCommentStatus);

                commentsToUpdate.add(comment);
            } else {
                if (commentStatus == CommentStatus.PUBLISHED) {
                    errors.add(String.format("Нельзя менять статус уже опубликованному комментарию! id комментария = %s", comment.getId()));
                } else if (commentStatus == CommentStatus.REJECTED && newCommentStatus == CommentStatus.PUBLISHED) {
                    errors.add(String.format("Нельзя опубликовать уже отклоненный комментарий! id комментария = %s", comment.getId()));
                }
            }
        }

        commentsToUpdate = commentDao.saveAll(commentsToUpdate);
        final List<CommentDto> commentDtos = CommentMapper.toCommentDtoList(commentsToUpdate);

        return new CommentsChangeStatusResponseDto(commentDtos, errors);
    }

    private CommentStatus mapToCommentStatus(CommentUpdateAdminAction action) {
        CommentStatus commentStatus = CommentStatus.CREATED;

        switch (action) {
            case APPROVE_PUBLISHING: {
                commentStatus = CommentStatus.PUBLISHED;
                break;
            }
            case REJECT: {
                commentStatus = CommentStatus.REJECTED;
                break;
            }
        }

        return commentStatus;
    }
}
