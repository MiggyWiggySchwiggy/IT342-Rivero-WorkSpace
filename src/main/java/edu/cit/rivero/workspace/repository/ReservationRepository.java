package edu.cit.rivero.workspace.repository;

import edu.cit.rivero.workspace.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    boolean existsBySpaceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            String spaceId,
            String status,
            java.time.LocalDateTime requestedEnd,
            java.time.LocalDateTime requestedStart
    );
}
