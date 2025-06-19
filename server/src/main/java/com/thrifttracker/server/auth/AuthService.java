package com.thrifttracker.server.auth;

import com.thrifttracker.server.auth.dto.RegisterUserDto;
import com.thrifttracker.server.auth.dto.UserDto;
import com.thrifttracker.server.user.User;
import com.thrifttracker.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
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

        return userDto;
    }
}