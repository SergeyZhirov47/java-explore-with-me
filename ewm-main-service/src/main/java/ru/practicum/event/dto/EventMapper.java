package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.common.AbstractMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;

@UtilityClass
public class EventMapper extends AbstractMapper {
    public Event toEvent(EventCreateDto eventCreateDto) {
        return Event.builder()
                .title(eventCreateDto.getTitle())
                .description(eventCreateDto.getDescription())
                .annotation(eventCreateDto.getAnnotation())
                .isPaid(eventCreateDto.getIsPaid())
                .eventDate(eventCreateDto.getEventDate())
                .location(eventCreateDto.getLocation())
                .participantLimit(eventCreateDto.getParticipantLimit())
                .isModerationRequired(eventCreateDto.getIsModerationRequired())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        final UserDto initiator = UserMapper.toUserDto(event.getInitiator());
        final CategoryDto category = CategoryMapper.toCategoryDto(event.getCategory());

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .isPaid(event.getIsPaid())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .category(category)
                .createdOn(event.getCreatedOn())
                .participantLimit(event.getParticipantLimit())
                .isModerationRequired(event.getIsModerationRequired())
                .location(event.getLocation())
                .state(event.getState())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        final UserDto initiator = UserMapper.toUserDto(event.getInitiator());
        final CategoryDto category = CategoryMapper.toCategoryDto(event.getCategory());

        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .isPaid(event.getIsPaid())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .category(category)
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public void updateIfDifferent(Event event, final EventUpdateDto eventWithChanges) {
        // Состояние меняем согласно логике
        event.setTitle(getChanged(event.getTitle(), eventWithChanges.getTitle()));
        event.setDescription(getChanged(event.getDescription(), eventWithChanges.getDescription()));
        event.setAnnotation(getChanged(event.getAnnotation(), eventWithChanges.getAnnotation()));
        event.setIsPaid(getChanged(event.getIsPaid(), eventWithChanges.getIsPaid()));
        event.setEventDate(getChanged(event.getEventDate(), eventWithChanges.getEventDate()));
        event.setParticipantLimit(getChanged(event.getParticipantLimit(), eventWithChanges.getParticipantLimit()));
        event.setIsModerationRequired(getChanged(event.getIsModerationRequired(), eventWithChanges.getIsModerationRequired()));
        event.setLocation(getChanged(event.getLocation(), eventWithChanges.getLocation()));
    }
}
