package edu.cit.rivero.workspace.features;

import edu.cit.rivero.workspace.features.auth.User;
import edu.cit.rivero.workspace.features.auth.UserRepository;
import edu.cit.rivero.workspace.features.space.SpaceRepository;
import edu.cit.rivero.workspace.features.space.AvailabilitySlotRepository;
import edu.cit.rivero.workspace.features.reservation.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import jakarta.transaction.Transactional;
import java.util.List;

@SpringBootTest
public class DatabaseCleanTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private AvailabilitySlotRepository availabilityRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @Transactional
    @Commit
    public void wipeDatabaseExceptAdmin() {
        System.out.println("=== STARTING DATABASE CLEANUP ===");

        // 1. Delete all reservations
        reservationRepository.deleteAllInBatch();
        System.out.println("Deleted all reservations.");

        // 2. Delete all availability slots
        availabilityRepository.deleteAllInBatch();
        System.out.println("Deleted all availability slots.");

        // 3. Delete all spaces
        spaceRepository.deleteAllInBatch();
        System.out.println("Deleted all spaces.");

        // 4. Delete all users except admin@workspace.com
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!"admin@workspace.com".equalsIgnoreCase(user.getEmail())) {
                userRepository.delete(user);
                System.out.println("Deleted user: " + user.getEmail());
            }
        }

        System.out.println("=== DATABASE CLEANUP COMPLETED ===");
    }
}
