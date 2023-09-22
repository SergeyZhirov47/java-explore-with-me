package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateDataDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(long userId, long eventId);

    RequestDto cancel(long userId, long requestId);

    List<RequestDto> getUserRequests(long userId);

    List<RequestDto> getRequestsInUserEvent(long userId, long eventId);

    EventRequestStatusUpdateResultDto changeStatus(long userId, long eventId, EventRequestStatusUpdateDataDto updateDataDto);
}
