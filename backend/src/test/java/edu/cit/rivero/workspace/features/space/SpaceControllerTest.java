package edu.cit.rivero.workspace.features.space;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.rivero.workspace.features.reservation.ReservationRepository;
import edu.cit.rivero.workspace.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SpaceController.class)
@AutoConfigureMockMvc(addFilters = false)
class SpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpaceRepository spaceRepository;

    @MockBean
    private SpaceService spaceService;

    @MockBean
    private AvailabilitySlotRepository availabilitySlotRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllSpaces_ReturnsOk() throws Exception {
        Space space = new Space();
        space.setId("space-1");
        space.setName("Test Room");

        when(spaceRepository.findAll()).thenReturn(List.of(space));

        mockMvc.perform(get("/api/v1/spaces")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Test Room"));
    }

    @Test
    void getSpaceById_ReturnsOk() throws Exception {
        Space space = new Space();
        space.setId("space-1");
        space.setName("Test Room");

        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(space));

        mockMvc.perform(get("/api/v1/spaces/space-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Room"));
    }

    @Test
    void createSpace_ReturnsCreated() throws Exception {
        Space space = new Space();
        space.setName("Test Room");

        Space createdSpace = new Space();
        createdSpace.setId("space-1");
        createdSpace.setName("Test Room");

        when(spaceService.createSpace(any(Space.class))).thenReturn(createdSpace);

        mockMvc.perform(post("/api/v1/spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(space)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("space-1"));
    }

    @Test
    void updateSpace_ReturnsOk() throws Exception {
        Space space = new Space();
        space.setName("Updated Room");

        when(spaceService.updateSpace(eq("space-1"), any(Space.class))).thenReturn(space);

        mockMvc.perform(put("/api/v1/spaces/space-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(space)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Room"));
    }

    @Test
    void deleteSpace_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/spaces/space-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getAvailability_ReturnsOk() throws Exception {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setId(1L);
        slot.setSpaceId("space-1");

        when(availabilitySlotRepository.findBySpaceIdOrderByDayOfWeekAscStartTimeAsc("space-1"))
                .thenReturn(List.of(slot));

        mockMvc.perform(get("/api/v1/spaces/space-1/availability")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }
}
