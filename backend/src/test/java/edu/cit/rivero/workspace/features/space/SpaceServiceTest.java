package edu.cit.rivero.workspace.features.space;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private AvailabilitySlotRepository availabilitySlotRepository;

    @InjectMocks
    private SpaceService spaceService;

    private Space mockSpace;

    @BeforeEach
    void setUp() {
        mockSpace = new Space();
        mockSpace.setId("space-1");
        mockSpace.setName("Meeting Room");
        mockSpace.setCapacity(10);
    }

    @Test
    void createSpace_Success() {
        Space newSpace = new Space();
        newSpace.setName("New Room");

        when(spaceRepository.save(any(Space.class))).thenReturn(newSpace);

        Space result = spaceService.createSpace(newSpace);

        assertNotNull(result);
        assertEquals(0.0, newSpace.getRating());
        assertTrue(newSpace.getAvailable());
        assertNotNull(newSpace.getId());
        verify(spaceRepository, times(1)).save(any(Space.class));
    }

    @Test
    void updateSpace_Success() {
        Space updatedInfo = new Space();
        updatedInfo.setName("Updated Room");
        updatedInfo.setCapacity(20);

        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(mockSpace));
        when(spaceRepository.save(any(Space.class))).thenReturn(mockSpace);

        Space result = spaceService.updateSpace("space-1", updatedInfo);

        assertEquals("Updated Room", result.getName());
        assertEquals(20, result.getCapacity());
        verify(spaceRepository, times(1)).save(any(Space.class));
    }

    @Test
    void updateSpace_NotFound_ThrowsException() {
        when(spaceRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> spaceService.updateSpace("invalid", new Space()));
    }

    @Test
    void deleteSpace_Success() {
        when(spaceRepository.existsById("space-1")).thenReturn(true);

        spaceService.deleteSpace("space-1");

        verify(spaceRepository, times(1)).deleteById("space-1");
    }

    @Test
    void deleteSpace_NotFound_ThrowsException() {
        when(spaceRepository.existsById("invalid")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> spaceService.deleteSpace("invalid"));
    }

    @Test
    void replaceAvailability_Success() {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setDayOfWeek(1);

        when(availabilitySlotRepository.saveAll(anyList())).thenReturn(List.of(slot));

        List<AvailabilitySlot> result = spaceService.replaceAvailability("space-1", List.of(slot));

        assertEquals(1, result.size());
        assertEquals("space-1", slot.getSpaceId());
        assertNull(slot.getId());
        verify(availabilitySlotRepository, times(1)).deleteBySpaceId("space-1");
        verify(availabilitySlotRepository, times(1)).saveAll(anyList());
    }

    @Test
    void uploadImage_EmptyFiles_Success() {
        MultipartFile[] files = new MultipartFile[]{
                new MockMultipartFile("file", new byte[0]) // Empty file
        };

        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(mockSpace));
        when(spaceRepository.save(any(Space.class))).thenReturn(mockSpace);

        Space result = spaceService.uploadImage("space-1", files);

        assertNotNull(result);
        verify(spaceRepository, times(1)).save(any(Space.class));
    }
}
