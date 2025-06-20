package com.thrifttracker.server.user;

import com.thrifttracker.server.auth.dto.ChangePasswordDto;
import com.thrifttracker.server.auth.dto.UpdateProfileDto;
import com.thrifttracker.server.auth.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users") // A versioned API path
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Dependency

    /**
     * Endpoint to get the currently authenticated user's profile.
     * @param authentication This is automatically injected by Spring Security after our JWT filter runs.
     * @return A ResponseEntity containing the user's profile data.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        // Here we use our new mapper for clean code
        return ResponseEntity.ok(UserMapper.toUserDto(currentUser));
    }

    /**
     * Endpoint to update the currently authenticated user's profile.
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileDto updateProfileDto,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();
        UserDto updatedUser = userService.updateProfile(userId, updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint to change the currently authenticated user's password.
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        UUID userId = currentUser.getId();
        userService.changePassword(userId, changePasswordDto);
        // For a successful action that doesn't return data, HTTP 204 No Content is appropriate.
        return ResponseEntity.noContent().build();
    }
}