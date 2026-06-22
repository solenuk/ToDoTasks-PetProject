package com.solenuk.todotaskspetproject.user.repositories;

import com.solenuk.todotaskspetproject.user.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Retrieves a user by their exact email address.
     *
     * @param email the email address to search for; must not be null
     * @return an Optional containing the User if found, or empty Optional if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an email address is already registered in the system.
     *
     * @param email the email address to check; must not be null
     * @return true if a user with this email is registered, false otherwise
     */
    boolean existsByEmail(String email);
}
