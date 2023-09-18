package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.event.model.UpdateEventUserAction;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
@Data
@ToString(callSuper=true)
@AllArgsConstructor
public class EventUpdateByUserDto extends EventUpdateDto {
    private UpdateEventUserAction stateAction;
}
