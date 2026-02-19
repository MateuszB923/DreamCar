package pl.dreamcar.mateuszbochenek.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //zobaczyc czy orphan i casdade, ale do przemyslenia czy po usunieciu usera ma usuwac tez wiadomosci, bo moze archiwum, ale tez zasmieca baze
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 150)
    private String subject;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactCategory category;

    //zobaczyc czy orphan i casdade, ale do przemyslenia czy po usunieciu usera ma usuwac tez wiadomosci, bo moze archiwum, ale tez zasmieca baze
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MessageStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant readAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = MessageStatus.NEW;
        if (category == null) category = ContactCategory.GENERAL;
    }
}
