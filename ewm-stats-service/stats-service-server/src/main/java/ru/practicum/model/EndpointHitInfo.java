package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_stats")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    @Column(name = "ip4_address")
    private String ip;
    private LocalDateTime timestamp;
}
