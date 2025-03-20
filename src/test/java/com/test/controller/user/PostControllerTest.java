package com.test.controller.user;

import com.test.database.dto.CommunityPostDto;
import com.test.database.dto.PostDto;
import com.test.database.model.CommunityPost;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.requests.CommunityPostRequest;
import com.test.database.requests.CommunityPostUpdateRequest;
import com.test.database.requests.PostRequest;
import com.test.service.CommunityPostService;
import com.test.service.PostService;
import com.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private CommunityPostService communityPostService;

    @InjectMocks
    private PostController postController;

    private User currentUser;
    private Post testPost;
    private PostDto testPostDto;
    private CommunityPost testCommunityPost;
    private CommunityPostDto testCommunityPostDto;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);

        testPost = new Post();
        testPost.setId(100L);
        testPost.setPostContent("Test post content");
        testPost.setUser(currentUser);
        testPost.setCreatedAt(LocalDateTime.now());

        testPostDto = PostDto.builder()
                             .id(100L)
                             .postContent("Test post content")
                             .build();

        testCommunityPost = new CommunityPost();
        testCommunityPost.setId(200L);
        testCommunityPost.setCreatedAt(LocalDateTime.now());
        testCommunityPost.setPostContent("Community post content");

        testCommunityPostDto = CommunityPostDto.builder()
                                               .id(200L)
                                               .postContent("Community post content")
                                               .build();
    }

    @Test
    void testGetAllPosts() {
        int page = 0;
        int size = 10;
        List<PostDto> posts = Arrays.asList(testPostDto);
        Page<PostDto> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        Mockito.when(postService.getAllPostsPage(PageRequest.of(page, size))).thenReturn(postPage);

        ResponseEntity<Page<PostDto>> response = postController.getAllPosts(page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetPostById() {
        Long postId = 100L;

        Mockito.when(postService.getPostById(postId)).thenReturn(testPost);
        Mockito.when(postService.toDTO(testPost)).thenReturn(testPostDto);

        ResponseEntity<PostDto> response = postController.getPostById(postId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testPostDto.getId(), response.getBody().getId());
    }

    @Test
    void testCreatePost() {
        PostRequest postRequest = new PostRequest();
        postRequest.setPostContent("Test post content");

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(postService.addPost(ArgumentMatchers.any(Post.class))).thenReturn(testPost);
        Mockito.when(postService.toDTO(testPost)).thenReturn(testPostDto);

        ResponseEntity<PostDto> response = postController.createPost(postRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test post content", response.getBody().getPostContent());
    }

    @Test
    void testDeletePostForbidden() {
        Long postId = 100L;

        User anotherUser = new User();
        anotherUser.setId(2L);
        testPost.setUser(anotherUser);

        Mockito.when(postService.getPostById(postId)).thenReturn(testPost);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        ResponseEntity<Void> response = postController.deletePost(postId);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testDeletePostSuccess() {
        Long postId = 100L;

        testPost.setUser(currentUser);

        Mockito.when(postService.getPostById(postId)).thenReturn(testPost);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        ResponseEntity<Void> response = postController.deletePost(postId);
        Mockito.verify(postService).deletePost(postId);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGetAllCommunityPosts() {
        int page = 0;
        int size = 10;
        List<CommunityPostDto> communityPosts = Arrays.asList(testCommunityPostDto);
        Page<CommunityPostDto> communityPostPage = new PageImpl<>(communityPosts, PageRequest.of(page, size), communityPosts.size());

        Mockito.when(communityPostService.getCommunityPosts(PageRequest.of(page, size))).thenReturn(communityPostPage);

        ResponseEntity<Page<CommunityPostDto>> response = postController.getAllCommunityPosts(page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetCommunityPostById() {
        Long communityPostId = 200L;
        Mockito.when(communityPostService.getCommunityPostById(communityPostId)).thenReturn(testCommunityPost);
        Mockito.when(communityPostService.toDTO(testCommunityPost)).thenReturn(testCommunityPostDto);

        ResponseEntity<CommunityPostDto> response = postController.getCommunityPostById(communityPostId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testCommunityPostDto.getId(), response.getBody().getId());
    }

    @Test
    void testCreateCommunityPost() {
        CommunityPostRequest request = new CommunityPostRequest();
        request.setPostContent("Community post content");

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(communityPostService.createCommunityPost(request, currentUser)).thenReturn(testCommunityPostDto);

        ResponseEntity<CommunityPostDto> response = postController.createCommunityPost(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Community post content", response.getBody().getPostContent());
    }

    @Test
    void testUpdateCommunityPostForUser() {
        Long postId = 200L;
        CommunityPostUpdateRequest updateRequest = new CommunityPostUpdateRequest();
        updateRequest.setPostContent("Updated community post content");

        CommunityPostDto updatedDto = CommunityPostDto.builder()
                                                      .id(200L)
                                                      .postContent("Updated community post content")
                                                      .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(communityPostService.updateCommunityPostForUser(postId, updateRequest, currentUser))
               .thenReturn(updatedDto);

        ResponseEntity<CommunityPostDto> response = postController.updateCommunityPostForUser(postId, updateRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Updated community post content", response.getBody().getPostContent());
    }

    @Test
    void testDeleteCommunityPost() {
        Long communityPostId = 200L;
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        ResponseEntity<Void> response = postController.deleteCommunityPost(communityPostId);
        Mockito.verify(communityPostService).deleteCommunityPostAsUser(communityPostId, currentUser);
        assertEquals(204, response.getStatusCodeValue());
    }
}
