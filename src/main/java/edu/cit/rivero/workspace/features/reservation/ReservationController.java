package edu.cit.rivero.workspace.features.reservation;

import edu.cit.rivero.workspace.common.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<ReservationResponseData>> checkout(
            @RequestBody ReservationCheckoutRequest request,
            Authentication authentication
    ) {
        ReservationResponseData response = reservationService.checkout(request, authentication.getName());
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationHistoryItemData>>> getMyReservations(Authentication authentication) {
        List<ReservationHistoryItemData> response = reservationService.getMyReservations(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Admin endpoints ──

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminReservationItemData>>> getAllReservations() {
        List<AdminReservationItemData> response = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
