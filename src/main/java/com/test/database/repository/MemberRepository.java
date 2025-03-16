package com.test.database.repository;

import com.test.database.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    void deleteByCommunityId(Long communityId);
    List<Member> findByCommunityId(Long communityId);
    long countByCommunityId(Long communityId);
    Optional<Member> findByCommunityIdAndUserId(Long communityId, Long userId);
}
