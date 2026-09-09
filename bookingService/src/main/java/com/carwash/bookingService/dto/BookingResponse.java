package com.carwash.bookingService.dto;

import com.carwash.bookingService.entity.BookingStatus;
import com.carwash.bookingService.entity.ServiceType;
import com.carwash.bookingService.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long userId;
    private ServiceType serviceType;
    private VehicleType vehicleType;
    private String address;
    private LocalDateTime scheduledDateTime;
    private BookingStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
}