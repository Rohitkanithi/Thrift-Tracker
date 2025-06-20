package com.thrifttracker.server.auth;

import com.thrifttracker.server.auth.dto.AuthResponseDto;
import com.thrifttracker.server.auth.dto.LoginDto;
import com.thrifttracker.server.auth.dto.RegisterUserDto;
import com.thrifttracker.server.auth.dto.UserDto;
import com.thrifttracker.server.user.User;
import com.thrifttracker.server.user.UserMapper;
import com.thrifttracker.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class containing the core business logic for authentication.
 */
@Service // Marks this class as a Spring Service bean, making it available for dependency injection.
@RequiredArgsConstructor // A Lombok annotation that creates a constructor for all final fields.
public class AuthService {

    // These are the dependencies the service needs to do its job.
    // By declaring them as 'final', we ensure they are initialized once via the constructor.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Because we use @RequiredArgsConstructor, Spring will automatically "inject" the
    // UserRepository and PasswordEncoder beans that it has in its context.

    /**
     * Handles the business logic for registering a new user.
     *
     * @param registerUserDto The DTO containing the new user's information from the API request.
     * @return A UserDto containing the safe information of the newly created user.
     */
    public UserDto register(RegisterUserDto registerUserDto) {
        // Step 1: Check if a user with the given email already exists in the database.
        userRepository.findByEmail(registerUserDto.getEmail())
                .ifPresent(user -> {
                    // If the Optional contains a user, it means the email is taken. Throw an exception.
                    throw new IllegalStateException("Email already in use");
                });

        // Step 2: If the email is unique, hash the user's plain-text password for secure storage.
        String hashedPassword = passwordEncoder.encode(registerUserDto.getPassword());

        // Step 3: Create a new User entity object and populate it with data from the DTO.
        User newUser = new User();
        newUser.setFirstName(registerUserDto.getFirstName());
        newUser.setLastName(registerUserDto.getLastName());
        newUser.setEmail(registerUserDto.getEmail());
        newUser.setPassword(hashedPassword); // IMPORTANT: Store the HASHED password.
        newUser.setGender(registerUserDto.getGender());
        newUser.setPhoneNumber(registerUserDto.getPhoneNumber());

        // Step 4: Save the new user entity to the database.
        // The .save() method returns the saved entity, which now includes the generated ID and createdAt timestamp.
        User savedUser = userRepository.save(newUser);

        // Step 5: Convert the saved User entity to a "safe" UserDto to return to the caller.
        // This ensures we don't accidentally leak the password hash or other sensitive internal fields.
        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setFirstName(savedUser.getFirstName());
        userDto.setLastName(savedUser.getLastName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setGender(savedUser.getGender());
        userDto.setPhoneNumber(savedUser.getPhoneNumber());
        userDto.setCreatedAt(savedUser.getCreatedAt());

        return UserMapper.toUserDto(savedUser);
    }

    // New LOGIN METHOD
    public AuthResponseDto login(LoginDto loginDto) {
        // Step 1: Use the AuthenticationManager to validate the user's credentials.
        // This will internally use our UserDetailsService and PasswordEncoder to check if the user exists and the password is correct.
        // If the credentials are bad, it will throw an AuthenticationException, and the method will stop here.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        // Step 2: If authentication was successful, find the user in the database.
        // We need the full User object to generate a token for them.
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        var user = (User) authentication.getPrincipal();
        // Step 3: Generate a JWT for the validated user.
        var jwtToken = jwtService.generateToken(user);

        // Step 4: Return the token in our AuthResponseDto.
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}