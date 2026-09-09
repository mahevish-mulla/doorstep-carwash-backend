package com.carwash.bookingService.service;

import com.carwash.bookingService.dto.BookingResponse;
import com.carwash.bookingService.dto.CreateBookingRequest;
import com.carwash.bookingService.entity.Booking;
import com.carwash.bookingService.entity.BookingStatus;
import com.carwash.bookingService.entity.ServiceType;
import com.carwash.bookingService.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    private static final Map<ServiceType, BigDecimal> PRICE_MAP = new EnumMap<>(ServiceType.class);

    static {
        PRICE_MAP.put(ServiceType.TWO_WHEELER_WASH, BigDecimal.valueOf(300));
        PRICE_MAP.put(ServiceType.EXTERNAL_CAR_WASH, BigDecimal.valueOf(350));
        PRICE_MAP.put(ServiceType.FULL_CAR_WASH, BigDecimal.valueOf(450));
        PRICE_MAP.put(ServiceType.MONTHLY_PLAN, BigDecimal.valueOf(1499));
    }

    public BookingResponse createBooking(Long userId, CreateBookingRequest request) {
        BigDecimal price = PRICE_MAP.get(request.getServiceType());

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setServiceType(request.getServiceType());
        booking.setVehicleType(request.getVehicleType());
        booking.setAddress(request.getAddress());
        booking.setLatitude(request.getLatitude());
        booking.setLongitude(request.getLongitude());
        booking.setScheduledDateTime(request.getScheduledDateTime());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPrice(price);
        booking.setCreatedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    public List<BookingResponse> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getServiceType(),
                booking.getVehicleType(),
                booking.getAddress(),
                booking.getScheduledDateTime(),
                booking.getStatus(),
                booking.getPrice(),
                booking.getCreatedAt()
        );
    }
}