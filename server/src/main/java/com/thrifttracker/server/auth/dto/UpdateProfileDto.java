package com.thrifttracker.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for receiving profile update requests for non-sensitive data.
 */
@Data
public class UpdateProfileDto {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    // Phone number can be optional, so no validation is needed here.
    private String phoneNumber;
}