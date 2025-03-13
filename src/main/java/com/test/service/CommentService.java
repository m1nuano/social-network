package com.test.service;

import com.test.database.dto.CommentDto;
import com.test.database.mapper.CommentMapper;
import com.test.database.model.Comment;
import com.test.database.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDto toDTO(Comment comment) {
        return commentMapper.toDto(comment);
    }

    public Comment toEntity(CommentDto commentDto) {
        return commentMapper.toEntity(commentDto);
    }

    @Transactional
    public Comment addComment(Comment comment) {
        log.info("Adding new comment for post ID: {}", comment.getPost().getId());
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
        log.info("Comment with ID: {} successfully updated", comment.getId());
        return updatedComment;
    }

    @Transactional
    public boolean deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment not found with ID: {}", commentId);
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} successfully deleted", commentId);
        return true;
    }
}
