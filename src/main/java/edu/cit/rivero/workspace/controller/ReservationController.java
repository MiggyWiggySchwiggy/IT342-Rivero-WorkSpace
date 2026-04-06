package edu.cit.rivero.workspace.controller;

import edu.cit.rivero.workspace.common.ApiResponse;
import edu.cit.rivero.workspace.dto.ReservationCheckoutRequest;
import edu.cit.rivero.workspace.dto.ReservationHistoryItemData;
import edu.cit.rivero.workspace.dto.ReservationResponseData;
import edu.cit.rivero.workspace.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
