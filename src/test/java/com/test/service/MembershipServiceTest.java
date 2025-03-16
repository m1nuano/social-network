package com.test.service;

import com.test.database.dto.CommunityDto;
import com.test.database.mapper.CommunityMapper;
import com.test.database.model.Community;
import com.test.database.model.Member;
import com.test.database.model.User;
import com.test.database.model.enums.MemberRole;
import com.test.database.repository.CommunityRepository;
import com.test.database.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommunityService communityService;

    @Mock
    private UserService userService;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityMapper communityMapper;

    @InjectMocks
    private MembershipService membershipService;

    private User user;
    private Community community;
    private Member member;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        community = new Community();
        community.setId(10L);
        community.setName("Test Community");
        community.setCreatedAt(LocalDateTime.now());

        member = new Member();
        member.setId(100L);
        member.setUser(user);
        member.setCommunity(community);
        member.setMemberRole(MemberRole.MEMBER);
        member.setJoinedAt(LocalDateTime.now());
    }

    @Test
    void testCreateCommunity() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(communityRepository.save(any(Community.class))).thenReturn(community);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(communityMapper.toDto(any(Community.class))).thenReturn(new CommunityDto());

        CommunityDto result = membershipService.createCommunity("Test Community", "Description");

        assertNotNull(result);
        verify(communityRepository).save(any(Community.class));
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testJoinCommunity_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.empty());
        when(communityService.getCommunityById(community.getId())).thenReturn(community);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        assertDoesNotThrow(() -> membershipService.joinCommunity(community.getId()));
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testJoinCommunity_AlreadyMember() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        Exception exception = assertThrows(RuntimeException.class, () ->
                membershipService.joinCommunity(community.getId()));

        assertEquals("You are already a member of the community", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testLeaveCommunity_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> membershipService.leaveCommunity(community.getId()));
        verify(memberRepository).delete(member);
    }

    @Test
    void testLeaveCommunity_NotMember() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                membershipService.leaveCommunity(community.getId()));

        assertEquals("You are not a member of this community", exception.getMessage());
        verify(memberRepository, never()).delete(any(Member.class));
    }

    @Test
    void testLeaveCommunity_OwnerCannotLeave() {
        member.setMemberRole(MemberRole.OWNER);
        when(userService.getCurrentUser()).thenReturn(user);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        Exception exception = assertThrows(RuntimeException.class, () ->
                membershipService.leaveCommunity(community.getId()));

        assertEquals("Owner cannot leave the community. Transfer ownership first.", exception.getMessage());
        verify(memberRepository, never()).delete(any(Member.class));
    }

    @Test
    void testUpdateMemberRole_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        member.setMemberRole(MemberRole.OWNER);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        User targetUser = new User();
        targetUser.setId(2L);
        Member targetMember = new Member();
        targetMember.setId(101L);
        targetMember.setUser(targetUser);
        targetMember.setCommunity(community);
        targetMember.setMemberRole(MemberRole.MEMBER);

        when(memberRepository.findById(targetMember.getId())).thenReturn(Optional.of(targetMember));
        when(memberRepository.save(any(Member.class))).thenReturn(targetMember);

        assertDoesNotThrow(() -> membershipService.updateMemberRole(community.getId(), targetMember.getId(), MemberRole.ADMIN));
        verify(memberRepository).save(targetMember);
    }

    @Test
    void testUpdateMemberRole_NotOwner() {
        when(userService.getCurrentUser()).thenReturn(user);

        member.setMemberRole(MemberRole.MEMBER);

        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        Member targetMember = new Member();
        targetMember.setId(101L);
        targetMember.setCommunity(community);
        targetMember.setMemberRole(MemberRole.MEMBER);

        when(memberRepository.findById(101L)).thenReturn(Optional.of(targetMember));

        Exception exception = assertThrows(RuntimeException.class, () ->
                membershipService.updateMemberRole(community.getId(), 101L, MemberRole.ADMIN));

        assertEquals("Only the owner can assign admins or owners", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }


    @Test
    void testDeleteCommunityByOwner_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        member.setMemberRole(MemberRole.OWNER);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> membershipService.deleteCommunityByOwner(community.getId()));

        verify(memberRepository).deleteByCommunityId(community.getId());
        verify(communityRepository).deleteById(community.getId());
    }

    @Test
    void testDeleteCommunityByOwner_NotOwner() {
        when(userService.getCurrentUser()).thenReturn(user);
        member.setMemberRole(MemberRole.MEMBER);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId()))
                .thenReturn(Optional.of(member));

        Exception exception = assertThrows(RuntimeException.class, () ->
                membershipService.deleteCommunityByOwner(community.getId()));

        assertEquals("Only the owner can delete the community", exception.getMessage());
        verify(memberRepository, never()).deleteByCommunityId(anyLong());
        verify(communityRepository, never()).deleteById(anyLong());
    }
}
