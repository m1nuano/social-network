package com.test.database.repository;

import com.test.database.model.Community;
import com.test.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("SELECT c FROM Community c JOIN Member m ON m.community.id = c.id WHERE m.user = :user")
    List<Community> findByUserInMembers(@Param("user") User user);
}
