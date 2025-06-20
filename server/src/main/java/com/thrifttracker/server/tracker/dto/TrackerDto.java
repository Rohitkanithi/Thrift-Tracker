package com.thrifttracker.server.tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class TrackerDto {
    private UUID id;
    private String name;
    private String productKeywords;
    private BigDecimal maxPrice;
    private String targetUrl;
    private Instant createdAt;
    private Instant updatedAt;
}