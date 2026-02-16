package pl.dreamcar.mateuszbochenek.mappers;

import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.dto.CarResponse;
import pl.dreamcar.mateuszbochenek.dto.CarSpecDto;
import pl.dreamcar.mateuszbochenek.model.Car;
import pl.dreamcar.mateuszbochenek.model.CarDescription;
import pl.dreamcar.mateuszbochenek.model.CarSpec;

@Component
public class CarMapper {

    public CarResponse toResponse(Car car) {
        CarSpec spec = car.getCarSpec();
        CarDescription desc = car.getCarDescription();

        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getPricePerDay(),
                car.getImageUrl(),
                car.isAvailable(),

                spec != null ? spec.getZeroToHundredSeconds() : null,
                spec != null ? spec.getTopSpeedKmh() : null,
                spec != null ? spec.getPowerHp() : null,
                spec != null ? spec.getDrivetrain() : null,
                spec != null ? spec.getEngine() : null,
                spec != null ? spec.getMileageKm() : null,
                spec != null ? spec.getFuelConsumptionL100() : null,

                desc != null ? desc.getDescription() : null
        );
    }

    public CarSpec toSpecEntity(CarSpecDto dto) {
        if (dto == null) return null;

        return CarSpec.builder()
                .zeroToHundredSeconds(dto.zeroToHundredSeconds())
                .topSpeedKmh(dto.topSpeedKmh())
                .powerHp(dto.powerHp())
                .drivetrain(dto.drivetrain())
                .engine(dto.engine())
                .mileageKm(dto.mileageKm())
                .fuelConsumptionL100(dto.fuelConsumptionL100())
                .build();
    }

    public CarDescription toDescriptionEntity(String description) {
        if (description == null) return null;

        return CarDescription.builder()
                .description(description)
                .build();
    }

    public void applySpecPatch(CarSpec spec, CarSpecDto dto) {
        if (spec == null || dto == null) return;

        if (dto.zeroToHundredSeconds() != null) spec.setZeroToHundredSeconds(dto.zeroToHundredSeconds());
        if (dto.topSpeedKmh() != null) spec.setTopSpeedKmh(dto.topSpeedKmh());
        if (dto.powerHp() != null) spec.setPowerHp(dto.powerHp());
        if (dto.drivetrain() != null) spec.setDrivetrain(dto.drivetrain());
        if (dto.engine() != null) spec.setEngine(dto.engine());
        if (dto.mileageKm() != null) spec.setMileageKm(dto.mileageKm());
        if (dto.fuelConsumptionL100() != null) spec.setFuelConsumptionL100(dto.fuelConsumptionL100());
    }
}
