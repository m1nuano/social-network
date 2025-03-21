package com.test.controller.admin;

import com.test.database.dto.CommentDto;
import com.test.service.CommentService;
import com.test.database.model.Comment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> listComments() {
        List<CommentDto> comments = commentService.getAllComments()
                .stream()
                .map(commentService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(commentService.toDTO(comment));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentService.toEntity(commentDto);
        Comment createdComment = commentService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.toDTO(createdComment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long id, @Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentService.toEntity(commentDto);
        comment.setId(id);
        Comment updatedComment = commentService.updateComment(comment);
        return ResponseEntity.ok(commentService.toDTO(updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        boolean isDeleted = commentService.deleteCommentForAdmin(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
