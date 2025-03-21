package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.mapper.CommunityMapper;
import com.test.database.model.Community;
import com.test.database.model.Member;
import com.test.database.model.User;
import com.test.database.model.enums.MemberRole;
import com.test.database.repository.CommunityRepository;
import com.test.database.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MemberRepository memberRepository;
    private final CommunityService communityService;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;

    @Transactional
    public CommunityDto createCommunity(String name, String description) {
        User currentUser = userService.getCurrentUser();

        Community community = Community.builder()
                                       .name(name)
                                       .description(description)
                                       .createdAt(LocalDateTime.now())
                                       .build();
        Community savedCommunity = communityRepository.save(community);

        Member owner = Member.builder()
                             .community(savedCommunity)
                             .user(currentUser)
                             .memberRole(MemberRole.OWNER)
                             .joinedAt(LocalDateTime.now())
                             .build();
        memberRepository.save(owner);

        log.info("Community '{}' created, owner - '{}'", savedCommunity.getName(), currentUser.getUsername());
        return communityMapper.toDto(savedCommunity);
    }

    @Transactional
    public void joinCommunity(Long communityId) {
        User currentUser = userService.getCurrentUser();
        Optional<Member> existingMember = memberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId());
        if (existingMember.isPresent()) {
            throw new RuntimeException("You are already a member of the community");
        }
        Community community = communityService.getCommunityById(communityId);
        Member communityMember = Member.builder()
                                       .community(community)
                                       .user(currentUser)
                                       .memberRole(MemberRole.MEMBER)
                                       .joinedAt(LocalDateTime.now())
                                       .build();
        memberRepository.save(communityMember);
        log.info("User '{}' joined community '{}'", currentUser.getUsername(), community.getName());
    }

    @Transactional
    public void leaveCommunity(Long communityId) {
        User currentUser = userService.getCurrentUser();
        Member member = memberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                                        .orElseThrow(() -> new RuntimeException("You are not a member of this community"));
        if (member.getMemberRole() == MemberRole.OWNER) {
            throw new RuntimeException("Owner cannot leave the community. Transfer ownership first.");
        }
        memberRepository.delete(member);
        log.info("User '{}' left the community with ID '{}'", currentUser.getUsername(), communityId);
    }

    @Transactional
    public void updateMemberRole(Long communityId, Long memberId, MemberRole newRole) {
        User currentUser = userService.getCurrentUser();

        Member currentMember = memberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                                               .orElseThrow(() -> new RuntimeException("You are not a member of the community"));

        Member targetMember = memberRepository.findById(memberId)
                                              .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!targetMember.getCommunity().getId().equals(communityId)) {
            throw new RuntimeException("This member does not belong to the specified community");
        }

        if ((newRole == MemberRole.ADMIN || newRole == MemberRole.OWNER)
                && currentMember.getMemberRole() != MemberRole.OWNER) {
            throw new RuntimeException("Only the owner can assign admins or owners");
        }

        if (newRole == MemberRole.BLOCKED) {
            if (targetMember.getMemberRole() == MemberRole.OWNER) {
                throw new RuntimeException("Cannot block the owner of the community");
            }
            if (currentMember.getMemberRole() == MemberRole.ADMIN &&
                    targetMember.getMemberRole() != MemberRole.MEMBER) {
                throw new RuntimeException("Admins can only block members");
            }
        }

        if (targetMember.getMemberRole() == newRole) {
            throw new RuntimeException("The user already has the role " + newRole);
        }

        targetMember.setMemberRole(newRole);
        memberRepository.save(targetMember);

        log.info("Member with ID {} in community {} role updated to {}", memberId, communityId, newRole);
    }

    @Transactional
    public void deleteCommunityByOwner(Long communityId) {
        User currentUser = userService.getCurrentUser();

        Member member = memberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                                        .orElseThrow(() -> new RuntimeException("You are not a member of the community"));

        if (member.getMemberRole() != MemberRole.OWNER) {
            throw new RuntimeException("Only the owner can delete the community");
        }

        memberRepository.deleteByCommunityId(communityId);
        communityRepository.deleteById(communityId);
        log.info("Community with ID {} deleted by the owner", communityId);
    }
}
