package com.carwash.bookingService.dto;

import com.carwash.bookingService.entity.ServiceType;
import com.carwash.bookingService.entity.VehicleType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingRequest {

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Scheduled date/time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledDateTime;
}