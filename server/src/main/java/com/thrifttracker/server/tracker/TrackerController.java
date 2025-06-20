package com.thrifttracker.server.tracker;

import com.thrifttracker.server.tracker.dto.CreateTrackerDto;
import com.thrifttracker.server.tracker.dto.TrackerDto;
import com.thrifttracker.server.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trackers") // Base path for all tracker-related endpoints
@RequiredArgsConstructor
public class TrackerController {

    private final TrackerService trackerService;

    /**
     * Endpoint for a logged-in user to create a new tracker.
     */
    @PostMapping
    public ResponseEntity<TrackerDto> createTracker(
            @Valid @RequestBody CreateTrackerDto createTrackerDto,
            Authentication authentication
    ) {
        // Get the current user from the security context
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();

        // Delegate the creation logic to the service
        TrackerDto newTracker = trackerService.createTracker(createTrackerDto, userId);

        // Return the created tracker with a 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(newTracker);
    }

    /**
     * Endpoint for a logged-in user to get a list of all their own trackers.
     */
    @GetMapping
    public ResponseEntity<List<TrackerDto>> getTrackersForUser(Authentication authentication) {
        // Get the current user from the security context
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();

        // Delegate the fetching logic to the service
        List<TrackerDto> trackers = trackerService.getTrackersForUser(userId);

        // Return the list of trackers with a 200 OK status
        return ResponseEntity.ok(trackers);
    }

    // Add these two new methods inside your TrackerController.java class

    /**
     * Endpoint for a user to update one of their existing trackers.
     * @param trackerId The ID of the tracker to update, passed in the URL path.
     */
    @PutMapping("/{trackerId}")
    public ResponseEntity<TrackerDto> updateTracker(
            @PathVariable UUID trackerId, // This annotation binds the {trackerId} from the URL to this parameter.
            @Valid @RequestBody CreateTrackerDto createTrackerDto,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();
        TrackerDto updatedTracker = trackerService.updateTracker(trackerId, createTrackerDto, userId);
        return ResponseEntity.ok(updatedTracker);
    }

    /**
     * Endpoint for a user to delete one of their trackers.
     * @param trackerId The ID of the tracker to delete.
     */
    @DeleteMapping("/{trackerId}")
    public ResponseEntity<Void> deleteTracker(
            @PathVariable UUID trackerId,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();
        trackerService.deleteTracker(trackerId, userId);
        // Return HTTP 204 No Content for a successful deletion.
        return ResponseEntity.noContent().build();
    }
}