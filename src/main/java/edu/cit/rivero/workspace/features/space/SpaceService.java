package edu.cit.rivero.workspace.features.space;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public SpaceService(SpaceRepository spaceRepository, AvailabilitySlotRepository availabilitySlotRepository) {
        this.spaceRepository = spaceRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public Space createSpace(Space space) {
        if (space.getId() == null || space.getId().isBlank()) {
            space.setId(UUID.randomUUID().toString());
        }
        if (space.getRating() == null) {
            space.setRating(0.0);
        }
        if (space.getAvailable() == null) {
            space.setAvailable(true);
        }
        return spaceRepository.save(space);
    }

    public Space updateSpace(String id, Space updatedSpace) {
        Space existing = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + id));

        existing.setName(updatedSpace.getName());
        existing.setLocation(updatedSpace.getLocation());
        existing.setType(updatedSpace.getType());
        existing.setCapacity(updatedSpace.getCapacity());
        existing.setHourlyRate(updatedSpace.getHourlyRate());
        existing.setDescription(updatedSpace.getDescription());
        existing.setImageUrl(updatedSpace.getImageUrl());
        existing.setAmenities(updatedSpace.getAmenities());
        existing.setUtilities(updatedSpace.getUtilities());
        existing.setCheckInWindow(updatedSpace.getCheckInWindow());
        existing.setCancellationPolicy(updatedSpace.getCancellationPolicy());

        if (updatedSpace.getRating() != null) {
            existing.setRating(updatedSpace.getRating());
        }
        if (updatedSpace.getAvailable() != null) {
            existing.setAvailable(updatedSpace.getAvailable());
        }

        return spaceRepository.save(existing);
    }

    public void deleteSpace(String id) {
        if (!spaceRepository.existsById(id)) {
            throw new RuntimeException("Workspace not found with id: " + id);
        }
        spaceRepository.deleteById(id);
    }

    @Transactional
    public List<AvailabilitySlot> replaceAvailability(String spaceId, List<AvailabilitySlot> slots) {
        availabilitySlotRepository.deleteBySpaceId(spaceId);
        availabilitySlotRepository.flush();
        slots.forEach(s -> { s.setId(null); s.setSpaceId(spaceId); });
        return availabilitySlotRepository.saveAll(slots);
    }
}

