package ru.practicum.event.model;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String annotation;
    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private LocalDateTime eventDate;
    private int participantLimit;
    private boolean isPaid;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private boolean isModerationRequired;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "lon"))
    })
    private Location location;
}
