package com.thrifttracker.server.tracker;

import com.thrifttracker.server.tracker.dto.CreateTrackerDto;
import com.thrifttracker.server.tracker.dto.TrackerDto;
import com.thrifttracker.server.user.User;
import com.thrifttracker.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackerService {

    private final TrackerRepository trackerRepository;
    private final UserRepository userRepository; // We need this to associate the tracker with a user.

    /**
     * Creates a new tracker for a specific user.
     * @param createTrackerDto The DTO containing the new tracker's data.
     * @param userId The ID of the user creating the tracker.
     * @return The created tracker's data as a DTO.
     */
    @Transactional // This annotation ensures the whole method runs in a single database transaction.
    public TrackerDto createTracker(CreateTrackerDto createTrackerDto, UUID userId) {
        // 1. Find the user who is creating this tracker. If not found, throw an error.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. Create a new Tracker entity.
        Tracker newTracker = new Tracker();
        newTracker.setName(createTrackerDto.getName());
        newTracker.setProductKeywords(createTrackerDto.getProductKeywords());
        newTracker.setMaxPrice(createTrackerDto.getMaxPrice());
        newTracker.setTargetUrl(createTrackerDto.getTargetUrl());

        // 3. This is the crucial step: Set the relationship to the user.
        newTracker.setUser(user);

        // 4. Save the new tracker to the database.
        Tracker savedTracker = trackerRepository.saveAndFlush(newTracker);

        // 5. Map the saved entity to a DTO and return it.
        return TrackerMapper.toDto(savedTracker);
    }

    /**
     * Retrieves all trackers for a specific user.
     * @param userId The ID of the user whose trackers to fetch.
     * @return A list of tracker DTOs.
     */
    @Transactional(readOnly = true) // A read-only transaction is more efficient for fetch operations.
    public List<TrackerDto> getTrackersForUser(UUID userId) {
        // 1. Use our custom repository method to fetch all trackers for the user.
        List<Tracker> trackers = trackerRepository.findAllByUserId(userId);

        // 2. Map the list of Tracker entities to a list of TrackerDto objects and return it.
        return trackers.stream()
                .map(TrackerMapper::toDto)
                .collect(Collectors.toList());
    }

    // Add these two new methods inside your TrackerService.java class

    /**
     * Updates an existing tracker.
     * @param trackerId The ID of the tracker to update.
     * @param trackerDto The DTO with the updated data.
     * @param userId The ID of the user making the request.
     * @return The updated tracker's data.
     */
    @Transactional
    public TrackerDto updateTracker(UUID trackerId, CreateTrackerDto trackerDto, UUID userId) {
        // 1. Fetch the tracker from the database.
        Tracker tracker = trackerRepository.findById(trackerId)
                .orElseThrow(() -> new IllegalStateException("Tracker not found"));

        // 2. IMPORTANT: Authorization check. Ensure the tracker belongs to the current user.
        if (!tracker.getUser().getId().equals(userId)) {
            throw new IllegalStateException("User does not have permission to update this tracker");
        }

        // 3. Update the fields.
        tracker.setName(trackerDto.getName());
        tracker.setProductKeywords(trackerDto.getProductKeywords());
        tracker.setMaxPrice(trackerDto.getMaxPrice());
        tracker.setTargetUrl(trackerDto.getTargetUrl());

        // 4. Save the updated tracker. @UpdateTimestamp will fire automatically.
        Tracker savedTracker = trackerRepository.save(tracker);

        return TrackerMapper.toDto(savedTracker);
    }

    /**
     * Deletes a tracker.
     * @param trackerId The ID of the tracker to delete.
     * @param userId The ID of the user making the request.
     */
    public void deleteTracker(UUID trackerId, UUID userId) {
        // 1. Fetch the tracker from the database.
        Tracker tracker = trackerRepository.findById(trackerId)
                .orElseThrow(() -> new IllegalStateException("Tracker not found"));

        // 2. IMPORTANT: Authorization check.
        if (!tracker.getUser().getId().equals(userId)) {
            throw new IllegalStateException("User does not have permission to delete this tracker");
        }

        // 3. If checks pass, delete the tracker.
        trackerRepository.delete(tracker);
    }
}