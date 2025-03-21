package com.test.service;

import com.test.database.dto.CommentDto;
import com.test.database.mapper.CommentMapper;
import com.test.database.model.Comment;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentDto toDTO(Comment comment) {
        return commentMapper.toDto(comment);
    }

    public Comment toEntity(CommentDto commentDto) {
        return commentMapper.toEntity(commentDto);
    }

    @Transactional
    public Comment addComment(Comment comment) {
        log.info("Adding a new comment for post ID: {}", comment.getPost().getId());
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment successfully added with ID: {}", savedComment.getId());
        return savedComment;
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        log.info("Fetching comment with ID: {}", commentId);
        return commentRepository.findById(commentId)
                                .orElseThrow(() -> {
                                    log.error("Comment not found with ID: {}", commentId);
                                    return new RuntimeException("Comment not found with ID: " + commentId);
                                });
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        log.info("Fetching all comments");
        List<Comment> comments = commentRepository.findAll();
        log.info("Fetched {} comments", comments.size());
        return comments;
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        log.info("Updating comment with ID: {}", comment.getId());
        if (!commentRepository.existsById(comment.getId())) {
            log.error("Comment not found with ID: {}", comment.getId());
            throw new RuntimeException("Comment not found with ID: " + comment.getId());
        }
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID: {} successfully updated", updatedComment.getId());
        return updatedComment;
    }

    @Transactional
    public boolean deleteCommentForAdmin(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment not found with ID: {}", commentId);
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} successfully deleted", commentId);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPost(Post post, Pageable pageable) {
        log.info("Fetching comments for post ID: {}", post.getId());
        Page<Comment> commentsPage = commentRepository.findByPost(post, pageable);
        log.info("Fetched {} comments for post ID: {}", commentsPage.getTotalElements(), post.getId());
        return commentsPage.map(commentMapper::toDto);
    }

    @Transactional
    public CommentDto createComment(Post post, User user, String content) {
        log.info("Creating a comment for post ID: {} by user ID: {}", post.getId(), user.getId());
        Comment comment = Comment.builder()
                                 .post(post)
                                 .user(user)
                                 .content(content)
                                 .createdAt(LocalDateTime.now())
                                 .build();
        Comment saved = commentRepository.save(comment);
        log.info("Comment successfully created with ID: {}", saved.getId());
        return commentMapper.toDto(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        log.info("Deleting comment with ID: {} by user ID: {}", commentId, currentUser.getId());
        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> {
                                               log.error("Comment not found with ID: {}", commentId);
                                               return new RuntimeException("Comment not found with ID: " + commentId);
                                           });

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            log.error("User ID: {} is not authorized to delete comment ID: {}", currentUser.getId(), commentId);
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment with ID: {} successfully deleted", commentId);
    }
}
