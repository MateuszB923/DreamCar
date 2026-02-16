package pl.dreamcar.mateuszbochenek.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String model;

    @NotNull
    @Min(1886)
    @Max(2030)
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @Column(nullable=false, precision=10, scale=2)
    @DecimalMin(value = "0.01")
    @Digits(integer=8, fraction=2)
    private BigDecimal pricePerDay;

    @NotBlank
    @Column(nullable=false)
    private String imageUrl;

    @Column(nullable=false)
    private boolean available;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarSpec carSpec;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarDescription carDescription;

    public void setSpec(CarSpec spec) {
        this.carSpec = spec;
        if (spec != null) spec.setCar(this);
    }

    public void setDescription(CarDescription description) {
        this.carDescription = description;
        if (description != null) description.setCar(this);
    }
}
