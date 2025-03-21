package com.test.controller.admin;

import com.test.database.dto.CommentDto;
import com.test.database.model.Comment;
import com.test.service.CommentService;
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
class AdminCommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private AdminCommentController adminCommentController;

    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");

        commentDto = CommentDto.builder()
                               .id(1L)
                               .content("Test comment")
                               .build();
    }

    @Test
    void testListComments() {
        List<Comment> comments = List.of(comment);

        when(commentService.getAllComments()).thenReturn(comments);
        when(commentService.toDTO(comment)).thenReturn(commentDto);

        ResponseEntity<List<CommentDto>> response = adminCommentController.listComments();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test comment", response.getBody().get(0).getContent());
    }

    @Test
    void testGetComment() {
        when(commentService.getCommentById(1L)).thenReturn(comment);
        when(commentService.toDTO(comment)).thenReturn(commentDto);

        ResponseEntity<CommentDto> response = adminCommentController.getComment(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test comment", response.getBody().getContent());
    }

    @Test
    void testCreateComment() {
        when(commentService.toEntity(any(CommentDto.class))).thenReturn(comment);
        when(commentService.addComment(any(Comment.class))).thenReturn(comment);
        when(commentService.toDTO(any(Comment.class))).thenReturn(commentDto);

        ResponseEntity<CommentDto> response = adminCommentController.createComment(commentDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test comment", response.getBody().getContent());
    }

    @Test
    void testUpdateComment() {
        when(commentService.toEntity(any(CommentDto.class))).thenReturn(comment);
        when(commentService.updateComment(any(Comment.class))).thenReturn(comment);
        when(commentService.toDTO(any(Comment.class))).thenReturn(commentDto);

        ResponseEntity<CommentDto> response = adminCommentController.updateComment(1L, commentDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test comment", response.getBody().getContent());
    }

    @Test
    void testDeleteComment_Success() {
        when(commentService.deleteCommentForAdmin(1L)).thenReturn(true);

        ResponseEntity<Void> response = adminCommentController.deleteComment(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(commentService, times(1)).deleteCommentForAdmin(eq(1L));
    }

    @Test
    void testDeleteComment_NotFound() {
        when(commentService.deleteCommentForAdmin(1L)).thenReturn(false);

        ResponseEntity<Void> response = adminCommentController.deleteComment(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
