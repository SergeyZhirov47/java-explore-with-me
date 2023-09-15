package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requesterId(request.getRequester().getId())
                .eventId(request.getEvent().getId())
                .status(request.getStatus())
                .created(request.getCreated())
                .build();
    }
}
