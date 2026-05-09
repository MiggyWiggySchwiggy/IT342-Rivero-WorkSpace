package edu.cit.rivero.workspace.features.space;

import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.features.reservation.Reservation;
import edu.cit.rivero.workspace.features.reservation.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/spaces")
@CrossOrigin(origins = "*")
public class SpaceController {

    private final SpaceRepository spaceRepository;
    private final SpaceService spaceService;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final ReservationRepository reservationRepository;

    public SpaceController(SpaceRepository spaceRepository, SpaceService spaceService,
                           AvailabilitySlotRepository availabilitySlotRepository,
                           ReservationRepository reservationRepository) {
        this.spaceRepository = spaceRepository;
        this.spaceService = spaceService;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.reservationRepository = reservationRepository;
    }

    // ── Public GET endpoints (unchanged) ──

    @GetMapping
    public ResponseEntity<ApiResponse<List<Space>>> getAllSpaces() {
        return ResponseEntity.ok(ApiResponse.success(spaceRepository.findAll()));
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<Space>> getSpaceById(@PathVariable String spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found."));
        return ResponseEntity.ok(ApiResponse.success(space));
    }

    // ── Admin-only CRUD endpoints ──

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Space>> createSpace(@RequestBody Space space) {
        Space created = spaceService.createSpace(space);
        return new ResponseEntity<>(ApiResponse.success(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Space>> updateSpace(@PathVariable String id, @RequestBody Space space) {
        Space updated = spaceService.updateSpace(id, space);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(@PathVariable String id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── Availability Slot endpoints ──

    @GetMapping("/{spaceId}/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> getAvailability(@PathVariable String spaceId) {
        List<AvailabilitySlot> slots = availabilitySlotRepository.findBySpaceIdOrderByDayOfWeekAscStartTimeAsc(spaceId);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    @PostMapping("/{spaceId}/availability")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AvailabilitySlot>> addAvailabilitySlot(
            @PathVariable String spaceId, @RequestBody AvailabilitySlot slot) {
        slot.setSpaceId(spaceId);
        AvailabilitySlot saved = availabilitySlotRepository.save(slot);
        return new ResponseEntity<>(ApiResponse.success(saved), HttpStatus.CREATED);
    }

    @DeleteMapping("/{spaceId}/availability/{slotId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAvailabilitySlot(
            @PathVariable String spaceId, @PathVariable Long slotId) {
        availabilitySlotRepository.deleteById(slotId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{spaceId}/availability/replace")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> replaceAvailability(
            @PathVariable String spaceId, @RequestBody List<AvailabilitySlot> slots) {
        List<AvailabilitySlot> saved = spaceService.replaceAvailability(spaceId, slots);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    // ── Public: Booked time ranges (no user data exposed) ──

    @GetMapping("/{spaceId}/bookings")
    public ResponseEntity<ApiResponse<List<BookedSlotData>>> getBookings(@PathVariable String spaceId) {
        LocalDateTime rangeStart = LocalDate.now().atStartOfDay();
        LocalDateTime rangeEnd = LocalDate.now().plusDays(7).atTime(LocalTime.MAX);

        List<Reservation> reservations = reservationRepository
                .findBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        spaceId, "CONFIRMED", rangeEnd, rangeStart);

        List<BookedSlotData> booked = reservations.stream()
                .map(r -> new BookedSlotData(r.getStartTime().toString(), r.getEndTime().toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(booked));
    }
}

