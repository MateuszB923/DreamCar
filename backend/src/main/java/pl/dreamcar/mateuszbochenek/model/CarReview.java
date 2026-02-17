package pl.dreamcar.mateuszbochenek.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "car_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String author;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String review;

}
