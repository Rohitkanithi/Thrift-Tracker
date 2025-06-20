package com.thrifttracker.server.auth;

import com.thrifttracker.server.auth.dto.AuthResponseDto;
import com.thrifttracker.server.auth.dto.LoginDto;
import com.thrifttracker.server.auth.dto.RegisterUserDto;
import com.thrifttracker.server.auth.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related HTTP requests like registration and login.
 */
@RestController // Combines @Controller and @ResponseBody. Tells Spring this class handles web requests and method return values should be written directly to the response body (as JSON).
@RequestMapping("/api/auth") // Sets the base URL path for all endpoints in this controller.
@RequiredArgsConstructor // Creates the constructor for dependency injection.
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for new user registration.
     * Mapped to HTTP POST requests on /api/auth/register.
     *
     * @param registerUserDto The user registration data, converted from the JSON request body.
     * @return A ResponseEntity containing the created user's data and an HTTP 201 Created status.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody RegisterUserDto registerUserDto
            // @RequestBody: Tells Spring to convert the JSON from the request body into a RegisterUserDto object.
            // @Valid: Tells Spring to run the validation rules we defined in RegisterUserDto (@NotBlank, @Email, etc.).
    ) {
        // Notice how clean this is. The controller just delegates the work to the service.
        UserDto createdUser = authService.register(registerUserDto);
        // We return a ResponseEntity to have full control over the HTTP response,
        // setting the status to 201 CREATED for successful resource creation.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // New LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        // The controller's job is simple: delegate the work to the service...
        AuthResponseDto response = authService.login(loginDto);
        // ...and return the result with a 200 OK status.
        return ResponseEntity.ok(response);
    }
}