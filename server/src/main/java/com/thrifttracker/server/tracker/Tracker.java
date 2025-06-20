package com.thrifttracker.server.tracker;

import com.thrifttracker.server.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "trackers")
public class Tracker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name; // e.g., "My Camera Search"

    @Column(nullable = false)
    private String productKeywords; // e.g., "Nikon Z6 II used"

    // For any monetary values, it's best practice to use BigDecimal to avoid precision errors.
    @Column(nullable = false)
    private BigDecimal maxPrice;

    @Column(nullable = false)
    private String targetUrl; // The URL of the marketplace search page to scrape.

    // --- The Relationship to the User ---
    @ManyToOne(fetch = FetchType.LAZY) // Many Trackers can belong to One User.
    @JoinColumn(name = "user_id", nullable = false) // This creates the 'user_id' foreign key column in our 'trackers' table.
    private User user; // This field links this Tracker to a User entity.

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}