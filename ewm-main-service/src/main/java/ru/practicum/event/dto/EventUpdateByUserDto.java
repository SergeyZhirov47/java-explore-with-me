package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.event.model.UpdateEventUserAction;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
@Data
@AllArgsConstructor
public class EventUpdateByUserDto extends EventCreateDto {
    private UpdateEventUserAction stateAction;
}
