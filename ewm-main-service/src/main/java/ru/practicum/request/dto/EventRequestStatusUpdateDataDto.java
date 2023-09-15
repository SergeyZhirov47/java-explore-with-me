package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.request.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Jacksonized
@Data
@AllArgsConstructor
public class EventRequestStatusUpdateDataDto {
    @NotNull
    private List<Long> requestIds;
    private RequestStatus status;
}
