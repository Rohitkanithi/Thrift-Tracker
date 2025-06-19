package com.thrifttracker.server.auth.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber;
    private Instant createdAt;
}