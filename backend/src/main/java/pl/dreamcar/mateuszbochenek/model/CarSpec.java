package pl.dreamcar.mateuszbochenek.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "car_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSpec {

    @Id
    private Long carId;

    @MapsId
    @OneToOne(optional=false)
    @JoinColumn(name="car_id")
    private Car car;

    @NotNull
    @DecimalMin("0.1")
    @DecimalMax("99.9")
    @Digits(integer = 3, fraction = 1)
    @Column(nullable=false, precision=4, scale=1)
    private BigDecimal zeroToHundredSeconds;

    @NotNull
    @Min(1)
    @Max(450)
    @Column(nullable=false)
    private Integer topSpeedKmh;

    @NotNull
    @Min(1)
    @Max(2000)
    @Column(nullable = false)
    private Integer powerHp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Drivetrain drivetrain;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String engine;

    @NotNull
    @Min(0)
    @Max(2_000_000)
    @Column(nullable = false)
    private Integer mileageKm;

    @NotNull
    @DecimalMin("0.1")
    @DecimalMax("99.9")
    @Digits(integer = 3, fraction = 1)
    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal fuelConsumptionL100;
}
