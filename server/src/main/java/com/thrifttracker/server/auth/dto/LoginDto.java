package com.thrifttracker.server.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object for receiving user login requests.
 */
@Data
public class LoginDto {
    private String email;
    private String password;
}