package com.test.controller.user;

import com.test.database.dto.CommentDto;
import com.test.database.model.Post;
import com.test.database.model.User;
import com.test.database.requests.CommentRequest;
import com.test.service.CommentService;
import com.test.service.PostService;
import com.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentDto>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Post post = postService.getPostById(postId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentDto> comments = commentService.getCommentsByPost(post, pageable);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest) {

        Post post = postService.getPostById(postId);

        User currentUser = userService.getCurrentUser();
        CommentDto createdComment = commentService.createComment(post, currentUser, commentRequest.getContent());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }


    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        User currentUser = userService.getCurrentUser();
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}

