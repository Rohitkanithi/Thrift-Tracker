package com.thrifttracker.server.tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackerRepository extends JpaRepository<Tracker, UUID> {

    /**
     * This is a custom derived query. By following this exact naming convention,
     * Spring Data JPA will automatically generate the SQL query to find all Trackers
     * that belong to a specific user ID.
     * The query it generates is equivalent to: "SELECT * FROM trackers WHERE user_id = ?"
     *
     * @param userId The UUID of the user.
     * @return A list of all trackers owned by that user.
     */
    List<Tracker> findAllByUserId(UUID userId);
}