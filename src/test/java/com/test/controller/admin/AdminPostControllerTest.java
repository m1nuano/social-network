package com.test.controller.admin;

import com.test.database.dto.PostDto;
import com.test.database.model.Post;
import com.test.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private AdminPostController adminPostController;

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                   .id(1L)
                   .postContent("Test Post")
                   .createdAt(LocalDateTime.now())
                   .build();

        postDto = PostDto.builder()
                         .id(1L)
                         .postContent("Test Post")
                         .createdAt(LocalDateTime.now())
                         .build();
    }

    @Test
    void testListPosts() {
        when(postService.getAllPosts()).thenReturn(List.of(post));
        when(postService.toDTO(any(Post.class))).thenReturn(postDto);

        ResponseEntity<List<PostDto>> response = adminPostController.listPosts();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Post", response.getBody().get(0).getPostContent());
    }

    @Test
    void testGetPost() {
        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.toDTO(post)).thenReturn(postDto);

        ResponseEntity<PostDto> response = adminPostController.getPost(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testCreatePost() {
        when(postService.toEntity(any(PostDto.class))).thenReturn(post);
        when(postService.addPost(any(Post.class))).thenReturn(post);
        when(postService.toDTO(any(Post.class))).thenReturn(postDto);

        ResponseEntity<PostDto> response = adminPostController.createPost(postDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testUpdatePost() {
        when(postService.toEntity(any(PostDto.class))).thenReturn(post);
        when(postService.updatePost(any(Post.class))).thenReturn(post);
        when(postService.toDTO(any(Post.class))).thenReturn(postDto);

        ResponseEntity<PostDto> response = adminPostController.updatePost(1L, postDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getPostContent());
    }

    @Test
    void testDeletePost_Success() {
        when(postService.deletePost(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminPostController.deletePost(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(postService, times(1)).deletePost(eq(1L));
    }

    @Test
    void testDeletePost_NotFound() {
        when(postService.deletePost(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminPostController.deletePost(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
