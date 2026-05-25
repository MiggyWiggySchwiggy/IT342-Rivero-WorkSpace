package edu.cit.rivero.workspace.features.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findBySpaceIdOrderByDayOfWeekAscStartTimeAsc(String spaceId);

    void deleteBySpaceId(String spaceId);
}
