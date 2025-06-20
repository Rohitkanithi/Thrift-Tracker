package com.thrifttracker.server.tracker;

import com.thrifttracker.server.tracker.dto.TrackerDto;

public class TrackerMapper {

    public static TrackerDto toDto(Tracker tracker) {
        TrackerDto dto = new TrackerDto();
        dto.setId(tracker.getId());
        dto.setName(tracker.getName());
        dto.setProductKeywords(tracker.getProductKeywords());
        dto.setMaxPrice(tracker.getMaxPrice());
        dto.setTargetUrl(tracker.getTargetUrl());
        dto.setCreatedAt(tracker.getCreatedAt());
        dto.setUpdatedAt(tracker.getUpdatedAt());
        return dto;
    }
}