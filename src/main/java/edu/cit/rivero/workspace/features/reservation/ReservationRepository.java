package edu.cit.rivero.workspace.features.reservation;
import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.features.space.*;
import edu.cit.rivero.workspace.features.reservation.*;
import edu.cit.rivero.workspace.features.reservation.strategy.*;
import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.security.*;


import edu.cit.rivero.workspace.features.reservation.Reservation;
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
