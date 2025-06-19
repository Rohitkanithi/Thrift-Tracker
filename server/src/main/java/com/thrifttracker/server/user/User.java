package com.thrifttracker.server.user;

import jakarta.persistence.*;
import lombok.Data; // A Lombok annotation to create all the getters, setters, equals, hash, and toString methods, boilerplate code
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * This is the User Entity class.
 * It represents the 'users' table in our PostgreSQL database.
 * Each instance of this class is a row in the table.
 */
@Data // Lombok annotation to generate getters, setters, etc.
@Entity // Tells JPA that this class is an entity that maps to a database table.
@Table(name = "users") // Specifies the name of the database table.
public class User {

    @Id // Marks this field as the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the ID is generated. IDENTITY is best for Postgres.
    private Long id;

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

}