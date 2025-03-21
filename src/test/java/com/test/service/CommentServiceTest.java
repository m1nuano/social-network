package com.test.service;

import com.test.database.dto.CommentDto;
import com.test.database.mapper.CommentMapper;
import com.test.database.model.Comment;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);

        user = new User();
        user.setId(1L);

        comment = Comment.builder()
                         .id(1L)
                         .post(post)
                         .user(user)
                         .content("Test comment")
                         .createdAt(LocalDateTime.now())
                         .build();

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Test comment");
    }

    @Test
    void testToDTO() {
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        CommentDto result = commentService.toDTO(comment);
        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getContent(), result.getContent());
        verify(commentMapper).toDto(comment);
    }

    @Test
    void testToEntity() {
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        Comment result = commentService.toEntity(commentDto);
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        verify(commentMapper).toEntity(commentDto);
    }

    @Test
    void testAddComment() {
        when(commentRepository.save(comment)).thenReturn(comment);
        Comment result = commentService.addComment(comment);
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        verify(commentRepository).save(comment);
    }

    @Test
    void testGetCommentById_Found() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        Comment result = commentService.getCommentById(1L);
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        verify(commentRepository).findById(1L);
    }

    @Test
    void testGetCommentById_NotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.getCommentById(1L));
        assertTrue(exception.getMessage().contains("Comment not found with ID: 1"));
        verify(commentRepository).findById(1L);
    }

    @Test
    void testGetAllComments() {
        List<Comment> comments = Arrays.asList(comment, comment);
        when(commentRepository.findAll()).thenReturn(comments);
        List<Comment> result = commentService.getAllComments();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository).findAll();
    }

    @Test
    void testUpdateComment_Success() {
        when(commentRepository.existsById(comment.getId())).thenReturn(true);
        when(commentRepository.save(comment)).thenReturn(comment);
        Comment result = commentService.updateComment(comment);
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        verify(commentRepository).existsById(comment.getId());
        verify(commentRepository).save(comment);
    }

    @Test
    void testUpdateComment_NotFound() {
        when(commentRepository.existsById(comment.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.updateComment(comment));
        assertTrue(exception.getMessage().contains("Comment not found with ID: " + comment.getId()));
        verify(commentRepository).existsById(comment.getId());
    }

    @Test
    void testDeleteCommentForAdmin_Success() {
        when(commentRepository.existsById(comment.getId())).thenReturn(true);
        doNothing().when(commentRepository).deleteById(comment.getId());
        boolean result = commentService.deleteCommentForAdmin(comment.getId());
        assertTrue(result);
        verify(commentRepository).existsById(comment.getId());
        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    void testDeleteCommentForAdmin_NotFound() {
        when(commentRepository.existsById(comment.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.deleteCommentForAdmin(comment.getId()));
        assertTrue(exception.getMessage().contains("Comment not found with ID: " + comment.getId()));
        verify(commentRepository).existsById(comment.getId());
    }

    @Test
    void testGetCommentsByPost() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment), pageable, 1);
        when(commentRepository.findByPost(post, pageable)).thenReturn(commentPage);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        Page<CommentDto> result = commentService.getCommentsByPost(post, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commentRepository).findByPost(post, pageable);
    }

    @Test
    void testCreateComment() {
        String content = "New Comment";
        Comment savedComment = Comment.builder()
                                      .id(2L)
                                      .post(post)
                                      .user(user)
                                      .content(content)
                                      .createdAt(LocalDateTime.now())
                                      .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(commentDto);
        CommentDto result = commentService.createComment(post, user, content);
        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(savedComment);
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);
        commentService.deleteComment(comment.getId(), user);
        verify(commentRepository).findById(comment.getId());
        verify(commentRepository).delete(comment);
    }

    @Test
    void testDeleteComment_Unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.deleteComment(comment.getId(), anotherUser));
        assertTrue(exception.getMessage().contains("You are not authorized to delete this comment"));
        verify(commentRepository).findById(comment.getId());
    }

    @Test
    void testDeleteComment_NotFound() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.deleteComment(comment.getId(), user));
        assertTrue(exception.getMessage().contains("Comment not found with ID: " + comment.getId()));
        verify(commentRepository).findById(comment.getId());
    }
}

