package com.test.controller.admin;

import com.test.database.dto.CommunityPostDto;
import com.test.database.model.CommunityPost;
import com.test.service.CommunityPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCommunityPostControllerTest {

    @Mock
    private CommunityPostService communityPostService;

    @InjectMocks
    private AdminCommunityPostController adminCommunityPostController;

    private CommunityPost communityPost;
    private CommunityPostDto communityPostDto;

    @BeforeEach
    void setUp() {
        communityPost = new CommunityPost();
        communityPost.setId(1L);
        communityPost.setPostContent("Test Post");

        communityPostDto = CommunityPostDto.builder()
                                           .id(1L)
                                           .postContent("Test Post")
                                           .build();
    }

    @Test
    void testListCommunityPosts() {
        List<CommunityPost> posts = List.of(communityPost);

        when(communityPostService.getAllCommunityPosts()).thenReturn(posts);
        when(communityPostService.toDTO(communityPost)).thenReturn(communityPostDto);

        ResponseEntity<List<CommunityPostDto>> response = adminCommunityPostController.listCommunityPosts();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Post", response.getBody().get(0).getPostContent());
    }

    @Test
    void testGetCommunityPost() {
        when(communityPostService.getCommunityPostById(1L)).thenReturn(communityPost);
        when(communityPostService.toDTO(communityPost)).thenReturn(communityPostDto);

        ResponseEntity<CommunityPostDto> response = adminCommunityPostController.getCommunityPost(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testCreateCommunityPost() {
        when(communityPostService.toEntity(any(CommunityPostDto.class))).thenReturn(communityPost);
        when(communityPostService.addCommunityPost(any(CommunityPost.class))).thenReturn(communityPost);
        when(communityPostService.toDTO(any(CommunityPost.class))).thenReturn(communityPostDto);

        ResponseEntity<CommunityPostDto> response = adminCommunityPostController.createCommunityPost(communityPostDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testUpdateCommunityPost() {
        when(communityPostService.toEntity(any(CommunityPostDto.class))).thenReturn(communityPost);
        when(communityPostService.updateCommunityPost(any(CommunityPost.class))).thenReturn(communityPost);
        when(communityPostService.toDTO(any(CommunityPost.class))).thenReturn(communityPostDto);

        ResponseEntity<CommunityPostDto> response = adminCommunityPostController.updateCommunityPost(1L, communityPostDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testDeleteCommunityPost_Success() {
        when(communityPostService.deleteCommunityPostAsAdmin(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminCommunityPostController.deleteCommunityPost(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(communityPostService, times(1)).deleteCommunityPostAsAdmin(eq(1L));
    }

    @Test
    void testDeleteCommunityPost_NotFound() {
        when(communityPostService.deleteCommunityPostAsAdmin(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminCommunityPostController.deleteCommunityPost(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
