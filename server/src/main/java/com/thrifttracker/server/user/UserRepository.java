package com.thrifttracker.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * This is the Repository interface for our User entity.
 * It extends JpaRepository, which provides a rich set of methods for CRUD operations.
 */
@Repository // This annotation tells Spring that this is a Repository bean.
public interface UserRepository extends JpaRepository<User, UUID> {
    // JpaRepository<User, Long> means this repository is for the 'User' entity,
    // and the type of the primary key is 'Long'.

    /**
     * This is a custom query method.
     * By following the naming convention "findBy<FieldName>", Spring Data JPA will
     * automatically generate the necessary SQL query to find a User by their email address.
     * We wrap the result in an Optional because the user might not exist. This is a
     * modern, safe way to handle potential null values.
     *
     * @param email The email address to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    Optional<User> findByEmail(String email);
}