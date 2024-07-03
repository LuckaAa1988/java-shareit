package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponse mockBookingResponse;
    private BookingRequest mockBookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockBookingResponse = new BookingResponse();
        mockBookingResponse.setId(1L);
        mockBookingResponse.setStartDate(LocalDateTime.now().plusDays(1).withNano(0));
        mockBookingResponse.setEndDate(LocalDateTime.now().plusDays(2).withNano(0));
        mockBookingResponse.setStatus(Status.APPROVED);

        mockBookingRequest = new BookingRequest();
        mockBookingRequest.setItemId(1L);
        mockBookingRequest.setStartDate(LocalDateTime.now().plusDays(1));
        mockBookingRequest.setEndDate(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testAddBooking() throws Exception {
        when(bookingService.addBooking(any(BookingRequest.class), anyLong())).thenReturn(mockBookingResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(mockBookingResponse.getStartDate().toString()))
                .andExpect(jsonPath("$.end").value(mockBookingResponse.getEndDate().toString()))
                .andExpect(jsonPath("$.status").value(mockBookingResponse.getStatus().toString()));
    }

    @Test
    void testGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(mockBookingResponse);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(mockBookingResponse.getStartDate().toString()))
                .andExpect(jsonPath("$.end").value(mockBookingResponse.getEndDate().toString()))
                .andExpect(jsonPath("$.status").value(mockBookingResponse.getStatus().toString()));
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(mockBookingResponse);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(mockBookingResponse.getStartDate().toString()))
                .andExpect(jsonPath("$.end").value(mockBookingResponse.getEndDate().toString()))
                .andExpect(jsonPath("$.status").value(mockBookingResponse.getStatus().toString()));
    }

    @Test
    void testGetAllUserBookings() throws Exception {
        List<BookingResponse> mockBookings = Arrays.asList(mockBookingResponse);
        when(bookingService.getAllUserBookings(anyLong(), anyString(), any(), any())).thenReturn(mockBookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockBookingResponse.getId()))
                .andExpect(jsonPath("$[0].start").value(mockBookingResponse.getStartDate().toString()))
                .andExpect(jsonPath("$[0].end").value(mockBookingResponse.getEndDate().toString()))
                .andExpect(jsonPath("$[0].status").value(mockBookingResponse.getStatus().toString()));
    }

    @Test
    void testGetAllOwnerBookings() throws Exception {
        List<BookingResponse> mockBookings = Arrays.asList(mockBookingResponse);
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), any(), any())).thenReturn(mockBookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockBookingResponse.getId()))
                .andExpect(jsonPath("$[0].start").value(mockBookingResponse.getStartDate().toString()))
                .andExpect(jsonPath("$[0].end").value(mockBookingResponse.getEndDate().toString()))
                .andExpect(jsonPath("$[0].status").value(mockBookingResponse.getStatus().toString()));
    }

    @Test
    void testAddBookingNotFoundException() throws Exception {
        when(bookingService.addBooking(any(BookingRequest.class), anyLong())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockBookingRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookingNotFoundException() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBookingStatusStateException() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenThrow(new StateException("Invalid state"));

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUserBookingsItemException() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), any(), any())).thenThrow(new ItemException("Item exception"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }
}