package com.test.controller;

import com.test.dto.PostDto;
import com.test.service.PostService;
import com.test.model.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDto>> listPosts() {
        List<PostDto> posts = postService.getAllPosts()
                .stream()
                .map(postService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(postService.toDTO(post));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        Post post = postService.toEntity(postDto);
        Post createdPost = postService.addPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.toDTO(createdPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostDto postDto) {
        Post post = postService.toEntity(postDto);
        post.setId(id);
        Post updatedPost = postService.updatePost(post);
        return ResponseEntity.ok(postService.toDTO(updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        boolean isDeleted = postService.deletePost(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
