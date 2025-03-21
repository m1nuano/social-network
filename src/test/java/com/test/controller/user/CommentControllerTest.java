package com.test.controller.user;

import com.test.database.dto.CommentDto;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.requests.CommentRequest;
import com.test.service.CommentService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentController commentController;

    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);

        testUser = new User();
        testUser.setId(1L);
    }

    @Test
    void testGetCommentsByPost() {
        Long postId = 1L;
        int page = 0;
        int size = 10;
        List<CommentDto> commentList = Arrays.asList(
                CommentDto.builder().id(100L).content("Комментарий 1").build(),
                CommentDto.builder().id(101L).content("Комментарий 2").build()
        );
        Page<CommentDto> commentPage = new PageImpl<>(commentList,
                PageRequest.of(page, size, Sort.by("createdAt").descending()), commentList.size());

        Mockito.when(postService.getPostById(postId)).thenReturn(testPost);
        Mockito.when(commentService.getCommentsByPost(ArgumentMatchers.eq(testPost), ArgumentMatchers.any(Pageable.class)))
               .thenReturn(commentPage);

        ResponseEntity<Page<CommentDto>> responseEntity = commentController.getCommentsByPost(postId, page, size);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().getTotalElements());
        assertEquals("Комментарий 1", responseEntity.getBody().getContent().get(0).getContent());
    }

    @Test
    void testCreateComment() {
        Long postId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("Новый комментарий");

        Mockito.when(postService.getPostById(postId)).thenReturn(testPost);
        Mockito.when(userService.getCurrentUser()).thenReturn(testUser);

        CommentDto expectedComment = CommentDto.builder().id(200L).content("Новый комментарий").build();
        Mockito.when(commentService.createComment(ArgumentMatchers.eq(testPost), ArgumentMatchers.eq(testUser), ArgumentMatchers.eq("Новый комментарий")))
               .thenReturn(expectedComment);

        ResponseEntity<CommentDto> responseEntity = commentController.createComment(postId, commentRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Новый комментарий", responseEntity.getBody().getContent());
    }

    @Test
    void testDeleteComment() {
        Long postId = 1L;
        Long commentId = 100L;

        Mockito.when(userService.getCurrentUser()).thenReturn(testUser);
        Mockito.doNothing().when(commentService).deleteComment(commentId, testUser);

        ResponseEntity<Void> responseEntity = commentController.deleteComment(postId, commentId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}
