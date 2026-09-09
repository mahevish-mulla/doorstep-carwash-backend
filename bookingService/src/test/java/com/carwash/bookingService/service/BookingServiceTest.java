package com.carwash.bookingService.service;

import com.carwash.bookingService.dto.BookingResponse;
import com.carwash.bookingService.dto.CreateBookingRequest;
import com.carwash.bookingService.entity.Booking;
import com.carwash.bookingService.entity.BookingStatus;
import com.carwash.bookingService.entity.ServiceType;
import com.carwash.bookingService.entity.VehicleType;
import com.carwash.bookingService.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private CreateBookingRequest createBookingRequest;
    private Booking savedBooking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository);

        createBookingRequest = new CreateBookingRequest();
        createBookingRequest.setServiceType(ServiceType.FULL_CAR_WASH);
        createBookingRequest.setVehicleType(VehicleType.SEDAN);
        createBookingRequest.setAddress("123 MG Road, Bangalore");
        createBookingRequest.setLatitude(12.9716);
        createBookingRequest.setLongitude(77.5946);
        createBookingRequest.setScheduledDateTime(LocalDateTime.now().plusDays(1));

        savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setUserId(10L);
        savedBooking.setServiceType(ServiceType.FULL_CAR_WASH);
        savedBooking.setVehicleType(VehicleType.SEDAN);
        savedBooking.setAddress("123 MG Road, Bangalore");
        savedBooking.setScheduledDateTime(createBookingRequest.getScheduledDateTime());
        savedBooking.setStatus(BookingStatus.PENDING);
        savedBooking.setPrice(BigDecimal.valueOf(450));
        savedBooking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createBooking_shouldSetCorrectPrice_forFullCarWash() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(10L, createBookingRequest);

        assertEquals(BigDecimal.valueOf(450), response.getPrice());
        assertEquals(BookingStatus.PENDING, response.getStatus());
        assertEquals(10L, response.getUserId());
    }

    @Test
    void createBooking_shouldSaveWithPendingStatus() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        bookingService.createBooking(10L, createBookingRequest);

        verify(bookingRepository, times(1)).save(bookingCaptor.capture());
        Booking captured = bookingCaptor.getValue();

        assertEquals(BookingStatus.PENDING, captured.getStatus());
        assertEquals(10L, captured.getUserId());
        assertEquals(ServiceType.FULL_CAR_WASH, captured.getServiceType());
    }

    @Test
    void createBooking_shouldSetCorrectPrice_forTwoWheelerWash() {
        createBookingRequest.setServiceType(ServiceType.TWO_WHEELER_WASH);
        createBookingRequest.setVehicleType(VehicleType.BIKE);

        Booking twoWheelerBooking = new Booking();
        twoWheelerBooking.setId(2L);
        twoWheelerBooking.setUserId(10L);
        twoWheelerBooking.setServiceType(ServiceType.TWO_WHEELER_WASH);
        twoWheelerBooking.setVehicleType(VehicleType.BIKE);
        twoWheelerBooking.setStatus(BookingStatus.PENDING);
        twoWheelerBooking.setPrice(BigDecimal.valueOf(300));

        when(bookingRepository.save(any(Booking.class))).thenReturn(twoWheelerBooking);

        BookingResponse response = bookingService.createBooking(10L, createBookingRequest);

        assertEquals(BigDecimal.valueOf(300), response.getPrice());
    }

    @Test
    void getBookingsForUser_shouldReturnUserBookings() {
        when(bookingRepository.findByUserId(10L)).thenReturn(Arrays.asList(savedBooking));

        List<BookingResponse> responses = bookingService.getBookingsForUser(10L);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getUserId());
        verify(bookingRepository, times(1)).findByUserId(10L);
    }

    @Test
    void getBookingsForUser_shouldReturnEmptyList_whenNoBookings() {
        when(bookingRepository.findByUserId(99L)).thenReturn(Arrays.asList());

        List<BookingResponse> responses = bookingService.getBookingsForUser(99L);

        assertTrue(responses.isEmpty());
    }
}