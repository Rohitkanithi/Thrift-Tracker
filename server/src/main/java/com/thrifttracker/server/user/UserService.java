package com.thrifttracker.server.user;

import com.thrifttracker.server.auth.dto.ChangePasswordDto;
import com.thrifttracker.server.auth.dto.UpdateProfileDto;
import com.thrifttracker.server.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto updateProfile(UUID userId, UpdateProfileDto updateProfileDto) {
        // 1. Fetch the user from the database.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. Update the user's fields.
        user.setFirstName(updateProfileDto.getFirstName());
        user.setLastName(updateProfileDto.getLastName());
        user.setGender(updateProfileDto.getGender());
        user.setPhoneNumber(updateProfileDto.getPhoneNumber());

        // 3. Save the updated user. The @UpdateTimestamp will automatically set the updatedAt field.
        User savedUser = userRepository.save(user);

        // 4. Return the updated user data using our mapper.
        return UserMapper.toUserDto(savedUser);
    }

    public void changePassword(UUID userId, ChangePasswordDto changePasswordDto) {
        // 1. Fetch the user.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. Check if the new password and confirmation password match.
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmationPassword())) {
            throw new IllegalStateException("New password and confirmation password do not match");
        }

        // 3. Check if the provided 'currentPassword' matches the one in the database.
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Incorrect current password");
        }

        // 4. If all checks pass, hash the new password and save it.
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user); // The @UpdateTimestamp will also fire here.
    }
}