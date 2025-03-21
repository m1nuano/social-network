package com.test.service;

import com.test.database.dto.CommunityPostDto;
import com.test.database.mapper.CommunityPostMapper;
import com.test.database.model.Community;
import com.test.database.model.CommunityPost;
import com.test.database.model.Member;
import com.test.database.model.User;
import com.test.database.model.enums.MemberRole;
import com.test.database.repository.CommunityPostRepository;
import com.test.database.repository.MemberRepository;
import com.test.database.requests.CommunityPostRequest;
import com.test.database.requests.CommunityPostUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityPostServiceTest {

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityPostMapper communityPostMapper;

    @Mock
    private CommunityService communityService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommunityPostService communityPostService;

    private CommunityPost communityPost;
    private CommunityPostDto communityPostDto;
    private Community community;
    private User user;
    private Member member;

    @BeforeEach
    void setUp() {
        community = new Community();
        community.setId(1L);

        user = new User();
        user.setId(1L);

        member = new Member();
        member.setId(1L);
        member.setCommunity(community);
        member.setUser(user);
        member.setMemberRole(MemberRole.ADMIN);

        communityPost = CommunityPost.builder()
                                     .id(1L)
                                     .community(community)
                                     .user(user)
                                     .postContent("Test community post")
                                     .createdAt(LocalDateTime.now())
                                     .build();

        communityPostDto = new CommunityPostDto();
        communityPostDto.setId(1L);
        communityPostDto.setPostContent("Test community post");
    }

    @Test
    void testToDTO() {
        when(communityPostMapper.toDto(communityPost)).thenReturn(communityPostDto);
        CommunityPostDto result = communityPostService.toDTO(communityPost);
        assertNotNull(result);
        assertEquals(communityPostDto.getId(), result.getId());
        assertEquals(communityPostDto.getPostContent(), result.getPostContent());
        verify(communityPostMapper).toDto(communityPost);
    }

    @Test
    void testToEntity() {
        when(communityPostMapper.toEntity(communityPostDto)).thenReturn(communityPost);
        CommunityPost result = communityPostService.toEntity(communityPostDto);
        assertNotNull(result);
        assertEquals(communityPost.getId(), result.getId());
        verify(communityPostMapper).toEntity(communityPostDto);
    }

    @Test
    void testAddCommunityPost() {
        when(communityPostRepository.save(communityPost)).thenReturn(communityPost);
        CommunityPost result = communityPostService.addCommunityPost(communityPost);
        assertNotNull(result);
        assertEquals(communityPost.getId(), result.getId());
        verify(communityPostRepository).save(communityPost);
    }

    @Test
    void testGetCommunityPostById_Found() {
        when(communityPostRepository.findById(1L)).thenReturn(Optional.of(communityPost));
        CommunityPost result = communityPostService.getCommunityPostById(1L);
        assertNotNull(result);
        assertEquals(communityPost.getId(), result.getId());
        verify(communityPostRepository).findById(1L);
    }

    @Test
    void testGetCommunityPostById_NotFound() {
        when(communityPostRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityPostService.getCommunityPostById(1L));
        assertTrue(exception.getMessage().contains("Community post not found with ID: 1"));
        verify(communityPostRepository).findById(1L);
    }

    @Test
    void testGetAllCommunityPosts() {
        List<CommunityPost> posts = Arrays.asList(communityPost, communityPost);
        when(communityPostRepository.findAll()).thenReturn(posts);
        List<CommunityPost> result = communityPostService.getAllCommunityPosts();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(communityPostRepository).findAll();
    }

    @Test
    void testUpdateCommunityPost_Success() {
        when(communityPostRepository.existsById(communityPost.getId())).thenReturn(true);
        when(communityPostRepository.save(communityPost)).thenReturn(communityPost);
        CommunityPost result = communityPostService.updateCommunityPost(communityPost);
        assertNotNull(result);
        assertEquals(communityPost.getId(), result.getId());
        verify(communityPostRepository).existsById(communityPost.getId());
        verify(communityPostRepository).save(communityPost);
    }

    @Test
    void testUpdateCommunityPost_NotFound() {
        when(communityPostRepository.existsById(communityPost.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityPostService.updateCommunityPost(communityPost));
        assertTrue(exception.getMessage().contains("Community post not found with ID: " + communityPost.getId()));
        verify(communityPostRepository).existsById(communityPost.getId());
    }

    @Test
    void testDeleteCommunityPostAsAdmin_Success() {
        when(communityPostRepository.existsById(communityPost.getId())).thenReturn(true);
        doNothing().when(communityPostRepository).deleteById(communityPost.getId());
        boolean result = communityPostService.deleteCommunityPostAsAdmin(communityPost.getId());
        assertTrue(result);
        verify(communityPostRepository).existsById(communityPost.getId());
        verify(communityPostRepository).deleteById(communityPost.getId());
    }

    @Test
    void testDeleteCommunityPostAsAdmin_NotFound() {
        when(communityPostRepository.existsById(communityPost.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> communityPostService.deleteCommunityPostAsAdmin(communityPost.getId()));
        assertTrue(exception.getMessage().contains("Community post not found with ID: " + communityPost.getId()));
        verify(communityPostRepository).existsById(communityPost.getId());
    }

    @Test
    void testGetCommunityPosts() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<CommunityPost> postPage = new PageImpl<>(Collections.singletonList(communityPost), pageable, 1);
        when(communityPostRepository.findAll(pageable)).thenReturn(postPage);
        when(communityPostMapper.toDto(communityPost)).thenReturn(communityPostDto);
        Page<CommunityPostDto> result = communityPostService.getCommunityPosts(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(communityPostRepository).findAll(pageable);
    }

    @Test
    void testCreateCommunityPost_Success() {
        CommunityPostRequest request = new CommunityPostRequest();
        request.setCommunityId(community.getId());
        request.setPostContent("New community post");

        when(communityService.getCommunityById(community.getId())).thenReturn(community);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(member));

        CommunityPost newPost = CommunityPost.builder()
                                             .id(2L)
                                             .community(community)
                                             .user(user)
                                             .postContent(request.getPostContent())
                                             .createdAt(LocalDateTime.now())
                                             .build();
        when(communityPostRepository.save(any(CommunityPost.class))).thenReturn(newPost);
        when(communityPostMapper.toDto(newPost)).thenReturn(communityPostDto);

        CommunityPostDto result = communityPostService.createCommunityPost(request, user);
        assertNotNull(result);
        verify(communityService).getCommunityById(community.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
        verify(communityPostRepository).save(any(CommunityPost.class));
        verify(communityPostMapper).toDto(newPost);
    }

    @Test
    void testCreateCommunityPost_Unauthorized_NotMember() {
        CommunityPostRequest request = new CommunityPostRequest();
        request.setCommunityId(community.getId());
        request.setPostContent("New community post");

        when(communityService.getCommunityById(community.getId())).thenReturn(community);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.empty());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                communityPostService.createCommunityPost(request, user));
        assertTrue(exception.getMessage().contains("You are not a member of this community"));
        verify(communityService).getCommunityById(community.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
    }

    @Test
    void testCreateCommunityPost_Unauthorized_NotAdminOrOwner() {
        CommunityPostRequest request = new CommunityPostRequest();
        request.setCommunityId(community.getId());
        request.setPostContent("New community post");

        Member nonAdminMember = new Member();
        nonAdminMember.setId(2L);
        nonAdminMember.setCommunity(community);
        nonAdminMember.setUser(user);
        nonAdminMember.setMemberRole(MemberRole.MEMBER);

        when(communityService.getCommunityById(community.getId())).thenReturn(community);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(nonAdminMember));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                communityPostService.createCommunityPost(request, user));
        assertTrue(exception.getMessage().contains("Only community admins and owners can post"));
        verify(communityService).getCommunityById(community.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
    }

    @Test
    void testUpdateCommunityPostForUser_Success() {
        CommunityPostUpdateRequest updateRequest = new CommunityPostUpdateRequest();
        updateRequest.setPostContent("Updated community post");

        communityPost.setPostContent("Old content");

        when(communityPostRepository.findById(communityPost.getId())).thenReturn(Optional.of(communityPost));
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(member));
        when(communityPostRepository.save(communityPost)).thenReturn(communityPost);
        when(communityPostMapper.toDto(communityPost)).thenReturn(communityPostDto);

        CommunityPostDto result = communityPostService.updateCommunityPostForUser(communityPost.getId(), updateRequest, user);
        assertNotNull(result);
        verify(communityPostRepository).findById(communityPost.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
        verify(communityPostRepository).save(communityPost);
        verify(communityPostMapper).toDto(communityPost);
    }

    @Test
    void testUpdateCommunityPostForUser_Unauthorized() {
        CommunityPostUpdateRequest updateRequest = new CommunityPostUpdateRequest();
        updateRequest.setPostContent("Updated community post");

        Member nonAdminMember = new Member();
        nonAdminMember.setId(2L);
        nonAdminMember.setCommunity(community);
        nonAdminMember.setUser(user);
        nonAdminMember.setMemberRole(MemberRole.MEMBER);

        when(communityPostRepository.findById(communityPost.getId())).thenReturn(Optional.of(communityPost));
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(nonAdminMember));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                communityPostService.updateCommunityPostForUser(communityPost.getId(), updateRequest, user));
        assertTrue(exception.getMessage().contains("Only community admins and owners can update community posts"));
        verify(communityPostRepository).findById(communityPost.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
    }

    @Test
    void testDeleteCommunityPostAsUser_Success() {
        when(communityPostRepository.findById(communityPost.getId())).thenReturn(Optional.of(communityPost));
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(member));
        doNothing().when(communityPostRepository).delete(communityPost);

        assertDoesNotThrow(() -> communityPostService.deleteCommunityPostAsUser(communityPost.getId(), user));
        verify(communityPostRepository).findById(communityPost.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
        verify(communityPostRepository).delete(communityPost);
    }

    @Test
    void testDeleteCommunityPostAsUser_Unauthorized() {
        when(communityPostRepository.findById(communityPost.getId())).thenReturn(Optional.of(communityPost));
        Member nonAdminMember = new Member();
        nonAdminMember.setId(2L);
        nonAdminMember.setCommunity(community);
        nonAdminMember.setUser(user);
        nonAdminMember.setMemberRole(MemberRole.MEMBER);
        when(memberRepository.findByCommunityIdAndUserId(community.getId(), user.getId())).thenReturn(Optional.of(nonAdminMember));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                communityPostService.deleteCommunityPostAsUser(communityPost.getId(), user));
        assertTrue(exception.getMessage().contains("Only community admins and owners can delete community posts"));
        verify(communityPostRepository).findById(communityPost.getId());
        verify(memberRepository).findByCommunityIdAndUserId(community.getId(), user.getId());
    }
}
