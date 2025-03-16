package com.test.database.repository;

import com.test.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u " +
            "WHERE (COALESCE(:username, '') = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
            "AND (COALESCE(:email, '') = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (COALESCE(:firstName, '') = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (COALESCE(:lastName, '') = '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    Page<User> searchUsers(@Param("username") String username,
                           @Param("email") String email,
                           @Param("firstName") String firstName,
                           @Param("lastName") String lastName,
                           Pageable pageable);
}
