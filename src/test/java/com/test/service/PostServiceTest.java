package com.test.service;

import com.test.database.dto.PostDto;
import com.test.database.mapper.PostMapper;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        post = new Post();
        post.setId(100L);
        post.setPostContent("This is a test post");
        post.setUser(user);

        postDto = new PostDto();
        postDto.setId(100L);
        postDto.setPostContent("This is a test post");
    }

    @Test
    void testAddPost_Success() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.addPost(post);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).save(post);
    }

    @Test
    void testGetPostById_Success() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(100L);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
    }

    @Test
    void testGetPostById_NotFound() {
        when(postRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                postService.getPostById(100L));

        assertEquals("Post not found with ID: 100", exception.getMessage());
    }

    @Test
    void testGetAllPosts() {
        List<Post> posts = List.of(post);
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertEquals(1, result.size());
        assertEquals(post.getId(), result.get(0).getId());
    }

    @Test
    void testUpdatePost_Success() {
        when(postRepository.existsById(post.getId())).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.updatePost(post);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).save(post);
    }

    @Test
    void testUpdatePost_NotFound() {
        when(postRepository.existsById(post.getId())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                postService.updatePost(post));

        assertEquals("Post not found with ID: 100", exception.getMessage());
    }

    @Test
    void testDeletePost_Success() {
        when(postRepository.existsById(100L)).thenReturn(true);

        boolean result = postService.deletePost(100L);

        assertTrue(result);
        verify(postRepository).deleteById(100L);
    }

    @Test
    void testDeletePost_NotFound() {
        when(postRepository.existsById(100L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                postService.deletePost(100L));

        assertEquals("Post not found with ID: 100", exception.getMessage());
        verify(postRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllPostsWithPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findAll(pageable)).thenReturn(postPage);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getAllPostsPage(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(postDto.getId(), result.getContent().get(0).getId());
    }
}
