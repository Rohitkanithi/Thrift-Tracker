package com.thrifttracker.server.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTrackerDto {

    @NotBlank(message = "Tracker name is required")
    private String name;

    @NotBlank(message = "Product keywords are required")
    private String productKeywords;

    @NotNull(message = "Maximum price is required")
    @Positive(message = "Maximum price must be a positive number")
    private BigDecimal maxPrice;

    @NotBlank(message = "Target URL is required")
    private String targetUrl;
}