package com.thrifttracker.server.auth.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber;
    private Instant createdAt;
    private Instant updatedAt;
}