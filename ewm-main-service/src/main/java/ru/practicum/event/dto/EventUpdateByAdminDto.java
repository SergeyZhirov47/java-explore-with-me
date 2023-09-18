package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.event.model.UpdateEventAdminAction;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
@Data
@ToString(callSuper = true)
@AllArgsConstructor
public class EventUpdateByAdminDto extends EventUpdateDto {
    private UpdateEventAdminAction stateAction;
}
