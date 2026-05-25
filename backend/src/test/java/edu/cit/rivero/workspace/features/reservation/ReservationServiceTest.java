package edu.cit.rivero.workspace.features.reservation;

import edu.cit.rivero.workspace.common.BusinessException;
import edu.cit.rivero.workspace.features.auth.User;
import edu.cit.rivero.workspace.features.auth.UserRepository;
import edu.cit.rivero.workspace.features.reservation.strategy.PaymentStrategy;
import edu.cit.rivero.workspace.features.space.Space;
import edu.cit.rivero.workspace.features.space.SpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private ReservationService reservationService;

    private User mockUser;
    private Space mockSpace;
    private ReservationCheckoutRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("test@test.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        mockSpace = new Space();
        mockSpace.setId("space-1");
        mockSpace.setName("Meeting Room");
        mockSpace.setHourlyRate(50.0);

        PaymentMethodRequest paymentMethod = new PaymentMethodRequest();
        paymentMethod.setCardNumber("1111222233334444");

        mockRequest = new ReservationCheckoutRequest();
        mockRequest.setSpaceId("space-1");
        mockRequest.setStartTime(OffsetDateTime.now().plusDays(1).toString());
        mockRequest.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2).toString());
        mockRequest.setPaymentMethod(paymentMethod);
    }

    @Test
    void checkout_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(mockSpace));
        when(reservationRepository.existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(false);
        when(paymentStrategy.processPayment(any(), anyDouble())).thenReturn(true);

        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);
        savedReservation.setSpace(mockSpace);
        savedReservation.setStatus("CONFIRMED");
        savedReservation.setPaymentStatus("PAID");
        savedReservation.setStartTime(LocalDateTime.now().plusDays(1));
        savedReservation.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        savedReservation.setTotalAmount(BigDecimal.valueOf(149.0)); // 2 hours * 50 + 49 fee

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponseData response = reservationService.checkout(mockRequest, "test@test.com");

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("PAID", response.getPaymentStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void checkout_Conflict_ThrowsBusinessException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(mockSpace));
        when(reservationRepository.existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> reservationService.checkout(mockRequest, "test@test.com"));
        assertEquals("BOOK-001", exception.getCode());
    }

    @Test
    void checkout_PaymentFailed_ThrowsBusinessException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(spaceRepository.findById("space-1")).thenReturn(Optional.of(mockSpace));
        when(reservationRepository.existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(false);
        when(paymentStrategy.processPayment(any(), anyDouble())).thenReturn(false);
        when(paymentStrategy.getDeclineReason()).thenReturn("Insufficient Funds");

        BusinessException exception = assertThrows(BusinessException.class, () -> reservationService.checkout(mockRequest, "test@test.com"));
        assertEquals("PAY-001", exception.getCode());
        assertTrue(exception.getMessage().contains("Insufficient Funds"));
    }

    @Test
    void getMyReservations_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setSpace(mockSpace);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(1));
        reservation.setTotalAmount(BigDecimal.valueOf(99.0));

        when(reservationRepository.findByUserEmailOrderByCreatedAtDesc("test@test.com")).thenReturn(List.of(reservation));

        List<ReservationHistoryItemData> result = reservationService.getMyReservations("test@test.com");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getReservationId());
    }

    @Test
    void getAllReservations_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(mockUser);
        reservation.setSpace(mockSpace);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(1));
        reservation.setTotalAmount(BigDecimal.valueOf(99.0));

        when(reservationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(reservation));

        List<AdminReservationItemData> result = reservationService.getAllReservations();

        assertEquals(1, result.size());
        assertEquals("test@test.com", result.get(0).getUserEmail());
    }

    @Test
    void cancelReservation_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus("CONFIRMED");
        reservation.setPaymentStatus("PAID");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        reservationService.cancelReservation(1L);

        assertEquals("CANCELLED", reservation.getStatus());
        assertEquals("REFUNDED", reservation.getPaymentStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }
}
