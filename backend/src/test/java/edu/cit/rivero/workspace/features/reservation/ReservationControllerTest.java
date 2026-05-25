package edu.cit.rivero.workspace.features.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.rivero.workspace.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com")
    void checkout_ReturnsCreated() throws Exception {
        ReservationCheckoutRequest request = new ReservationCheckoutRequest();
        request.setSpaceId("space-1");

        ReservationResponseData responseData = new ReservationResponseData(
                1L, "space-1", "CONFIRMED", "PAID", "start", "end", "100.0"
        );

        when(reservationService.checkout(any(ReservationCheckoutRequest.class), eq("test@test.com")))
                .thenReturn(responseData);

        mockMvc.perform(post("/api/v1/reservations/checkout")
                .principal(new TestingAuthenticationToken("test@test.com", null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void getMyReservations_ReturnsOk() throws Exception {
        ReservationHistoryItemData item = new ReservationHistoryItemData(
                1L, "space-1", "Test Room", "Location", "CONFIRMED", "PAID", "start", "end", "100.0", "created"
        );

        when(reservationService.getMyReservations("test@test.com")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/reservations/my")
                .principal(new TestingAuthenticationToken("test@test.com", null))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].reservationId").value(1));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ROLE_ADMIN"})
    void getAllReservations_ReturnsOk() throws Exception {
        AdminReservationItemData item = new AdminReservationItemData(
                1L, "user@test.com", "John", "Doe", "space-1", "Test Room", "Location", "CONFIRMED", "PAID", "start", "end", "100.0", "created"
        );

        when(reservationService.getAllReservations()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/reservations/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].reservationId").value(1));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ROLE_ADMIN"})
    void cancelReservation_ReturnsOk() throws Exception {
        mockMvc.perform(patch("/api/v1/reservations/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
