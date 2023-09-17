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
                .isPaid(eventCreateDto.isPaid())
                .eventDate(eventCreateDto.getEventDate())
                .location(eventCreateDto.getLocation())
                .participantLimit(eventCreateDto.getParticipantLimit())
                .isModerationRequired(eventCreateDto.isModerationRequired())
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
                .isPaid(event.isPaid())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .category(category)
                .createdOn(event.getCreatedOn())
                .participantLimit(event.getParticipantLimit())
                .isModerationRequired(event.isModerationRequired())
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
                .isPaid(event.isPaid())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .category(category)
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public void updateIfDifferent(Event event, final EventCreateDto eventWithChanges) {
        event.setTitle(getChanged(event.getTitle(), eventWithChanges.getTitle()));
        event.setDescription(getChanged(event.getDescription(), eventWithChanges.getDescription()));
        event.setAnnotation(getChanged(event.getAnnotation(), eventWithChanges.getAnnotation()));
        event.setPaid(getChanged(event.isPaid(), eventWithChanges.isPaid()));
        event.setEventDate(getChanged(event.getEventDate(), eventWithChanges.getEventDate()));
        event.setParticipantLimit(getChanged(event.getParticipantLimit(), eventWithChanges.getParticipantLimit()));
        event.setModerationRequired(getChanged(event.isModerationRequired(), eventWithChanges.isModerationRequired()));
        event.setLocation(getChanged(event.getLocation(), eventWithChanges.getLocation()));
    }
}
