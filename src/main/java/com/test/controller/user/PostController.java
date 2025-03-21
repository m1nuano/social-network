package com.test.controller.user;

import com.test.database.dto.CommunityPostDto;
import com.test.database.dto.PostDto;
import com.test.database.model.CommunityPost;
import com.test.database.model.Post;
import com.test.database.requests.CommunityPostRequest;
import com.test.database.requests.CommunityPostUpdateRequest;
import com.test.database.requests.PostRequest;
import com.test.service.CommunityPostService;
import com.test.service.PostService;
import com.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/app/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommunityPostService communityPostService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getAllPostsPage(PageRequest.of(page, size));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.toDTO(postService.getPostById(id)));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostRequest postRequest) {
        Post post = new Post();
        post.setPostContent(postRequest.getPostContent());
        post.setUser(userService.getCurrentUser());
        post.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(postService.toDTO(postService.addPost(post)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (!post.getUser().equals(userService.getCurrentUser())) {
            return ResponseEntity.status(403).build();
        }
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/community")
    public ResponseEntity<Page<CommunityPostDto>> getAllCommunityPosts(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<CommunityPostDto> communityPosts = communityPostService.getCommunityPosts(PageRequest.of(page, size));
        return ResponseEntity.ok(communityPosts);
    }

    @GetMapping("/community/{id}")
    public ResponseEntity<CommunityPostDto> getCommunityPostById(@PathVariable Long id) {
        return ResponseEntity.ok(communityPostService.toDTO(communityPostService.getCommunityPostById(id)));
    }

    @PostMapping("/community")
    public ResponseEntity<CommunityPostDto> createCommunityPost(@Valid @RequestBody CommunityPostRequest request) {
        CommunityPostDto dto = communityPostService.createCommunityPost(request, userService.getCurrentUser());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/community/{postId}")
    public ResponseEntity<CommunityPostDto> updateCommunityPostForUser(@PathVariable Long postId,
                                                                       @Valid @RequestBody CommunityPostUpdateRequest request) {
        CommunityPostDto updatedDto = communityPostService.updateCommunityPostForUser(postId, request, userService.getCurrentUser());
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/community/{id}")
    public ResponseEntity<Void> deleteCommunityPost(@PathVariable Long id) {
        communityPostService.deleteCommunityPostAsUser(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}

