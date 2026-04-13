package edu.cit.rivero.workspace.service;

import edu.cit.rivero.workspace.common.BusinessException;
import edu.cit.rivero.workspace.dto.PaymentMethodRequest;
import edu.cit.rivero.workspace.dto.ReservationCheckoutRequest;
import edu.cit.rivero.workspace.dto.ReservationHistoryItemData;
import edu.cit.rivero.workspace.dto.ReservationResponseData;
import edu.cit.rivero.workspace.entity.Reservation;
import edu.cit.rivero.workspace.entity.Space;
import edu.cit.rivero.workspace.entity.User;
import edu.cit.rivero.workspace.repository.ReservationRepository;
import edu.cit.rivero.workspace.repository.SpaceRepository;
import edu.cit.rivero.workspace.repository.UserRepository;
import org.springframework.stereotype.Service;
import edu.cit.rivero.workspace.strategy.PaymentStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ReservationService {

    private static final BigDecimal SERVICE_FEE = new BigDecimal("49.00");

    private final ReservationRepository reservationRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    // 1. Strategy is properly declared
    private final PaymentStrategy paymentStrategy;

    // 2. Strategy is added to the constructor for Spring Injection
    public ReservationService(ReservationRepository reservationRepository,
                              SpaceRepository spaceRepository,
                              UserRepository userRepository,
                              PaymentStrategy paymentStrategy) {
        this.reservationRepository = reservationRepository;
        this.spaceRepository = spaceRepository;
        this.userRepository = userRepository;
        this.paymentStrategy = paymentStrategy;
    }

    public ReservationResponseData checkout(ReservationCheckoutRequest request, String userEmail) {
        validateRequest(request);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Space space = spaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new RuntimeException("Selected workspace does not exist."));

        LocalDateTime start = parseDateTime(request.getStartTime());
        LocalDateTime end = parseDateTime(request.getEndTime());

        if (!start.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new RuntimeException("Start time must be in the future.");
        }
        if (!end.isAfter(start)) {
            throw new RuntimeException("End time must be after start time.");
        }

        boolean hasConflict = reservationRepository.existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getSpaceId(),
                "CONFIRMED",
                end,
                start
        );

        if (hasConflict) {
            throw new BusinessException("BOOK-001", "Schedule conflict: this workspace is already booked for that time slot.");
        }

        // Calculate Pricing
        long durationMinutes = Duration.between(start, end).toMinutes();
        long billableHours = Math.max(1, (long) Math.ceil(durationMinutes / 60.0));

        BigDecimal hourlyRate = BigDecimal.valueOf(space.getHourlyRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = hourlyRate.multiply(BigDecimal.valueOf(billableHours));
        BigDecimal total = subtotal.add(SERVICE_FEE);

        // 3. NEW STRATEGY LOGIC APPLIED HERE (Old hardcoded checks removed!)
        boolean paymentSuccess = paymentStrategy.processPayment(request.getPaymentMethod(), total.doubleValue());
        if (!paymentSuccess) {
            throw new BusinessException("PAY-001", "Payment Declined: " + paymentStrategy.getDeclineReason());
        }

        // Setup the saved reservation
        PaymentMethodRequest paymentMethod = request.getPaymentMethod();
        String normalizedCard = paymentMethod.getCardNumber().replaceAll("\\D", "");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSpace(space);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setHourlyRateAtBooking(hourlyRate);
        reservation.setSubtotal(subtotal);
        reservation.setServiceFee(SERVICE_FEE);
        reservation.setTotalAmount(total);
        reservation.setStatus("CONFIRMED");
        reservation.setPaymentStatus("PAID");
        reservation.setPaymentReference("SIM-" + System.currentTimeMillis());
        // Safely get last 4 digits if card is long enough, otherwise just use what was provided
        reservation.setCardLast4(normalizedCard.length() >= 4 ? normalizedCard.substring(normalizedCard.length() - 4) : "****");

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponseData(
                savedReservation.getId(),
                savedReservation.getSpace().getId(),
                savedReservation.getStatus(),
                savedReservation.getPaymentStatus(),
                savedReservation.getStartTime().toString(),
                savedReservation.getEndTime().toString(),
                savedReservation.getTotalAmount().toString()
        );
    }

    public List<ReservationHistoryItemData> getMyReservations(String userEmail) {
        return reservationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(reservation -> new ReservationHistoryItemData(
                        reservation.getId(),
                        reservation.getSpace().getId(),
                        reservation.getSpace().getName(),
                        reservation.getSpace().getLocation(),
                        reservation.getStatus(),
                        reservation.getPaymentStatus(),
                        reservation.getStartTime().toString(),
                        reservation.getEndTime().toString(),
                        reservation.getTotalAmount().toString(),
                        reservation.getCreatedAt() != null ? reservation.getCreatedAt().toString() : null
                ))
                .toList();
    }

    private void validateRequest(ReservationCheckoutRequest request) {
        if (request == null || request.getSpaceId() == null || request.getSpaceId().isBlank()) {
            throw new RuntimeException("Space selection is required.");
        }
        if (request.getStartTime() == null || request.getStartTime().isBlank()) {
            throw new RuntimeException("Start time is required.");
        }
        if (request.getEndTime() == null || request.getEndTime().isBlank()) {
            throw new RuntimeException("End time is required.");
        }
        if (request.getPaymentMethod() == null) {
            throw new RuntimeException("Payment method is required.");
        }
        if (request.getPaymentMethod().getCardNumber() == null || request.getPaymentMethod().getCardNumber().isBlank()) {
            throw new RuntimeException("Card number is required.");
        }
    }

    private LocalDateTime parseDateTime(String dateTimeValue) {
        try {
            return OffsetDateTime.parse(dateTimeValue).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
        } catch (Exception ignored) {
            throw new RuntimeException("Invalid date/time format.");
        }
    }
}