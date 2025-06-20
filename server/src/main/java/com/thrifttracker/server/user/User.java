package com.thrifttracker.server.user;

import jakarta.persistence.*;
import lombok.Data; // A Lombok annotation to create all the getters, setters, equals, hash, and toString methods, boilerplate code
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * This is the User Entity class.
 * It represents the 'users' table in our PostgreSQL database.
 * Each instance of this class is a row in the table.
 */
@Data // Lombok annotation to generate getters, setters, etc.
@Entity // Tells JPA that this class is an entity that maps to a database table.
@Table(name = "users") // Specifies the name of the database table.
public class User implements UserDetails {

    @Id // Marks this field as the primary key.
    @GeneratedValue(strategy = GenerationType.UUID) // Configures the way the ID is generated. IDENTITY is best for Postgres.
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true) // Marks this as a column in the table. It cannot be null and must be unique.
    private String email;

    @Column(nullable = false) // It cannot be null.
    private String password;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = true) // This column can be null, as the phone number is optional.
    private String phoneNumber;

    @CreationTimestamp // This annotation automatically sets the value when the entity is first created.
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp // This annotation automatically updates the value whenever the entity is modified.
    @Column(nullable = false)
    private Instant updatedAt;

    // These methods are required by the UserDetails interface.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, we are not using roles, so we return an empty list.
        // In the future, we could return roles like "ROLE_USER", "ROLE_ADMIN".
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // Spring Security's "username" is our user's email address.
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // We can set this to true, assuming user accounts never expire.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // We can set this to true, assuming we are not locking user accounts.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // We can set this to true, assuming passwords never expire.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // We can set this to true, assuming all registered users are enabled.
        return true;
    }
}