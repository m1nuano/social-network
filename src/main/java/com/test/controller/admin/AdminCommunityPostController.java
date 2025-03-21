package com.test.controller.admin;

import com.test.database.dto.CommunityPostDto;
import com.test.service.CommunityPostService;
import com.test.database.model.CommunityPost;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community-posts")
public class AdminCommunityPostController {

    private final CommunityPostService communityPostService;

    @GetMapping
    public ResponseEntity<List<CommunityPostDto>> listCommunityPosts() {
        List<CommunityPostDto> posts = communityPostService.getAllCommunityPosts()
                                                           .stream()
                                                           .map(communityPostService::toDTO)
                                                           .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityPostDto> getCommunityPost(@PathVariable("id") Long id) {
        CommunityPost communityPost = communityPostService.getCommunityPostById(id);
        return ResponseEntity.ok(communityPostService.toDTO(communityPost));
    }

    @PostMapping
    public ResponseEntity<CommunityPostDto> createCommunityPost(@Valid @RequestBody CommunityPostDto communityPostDto) {
        CommunityPost communityPost = communityPostService.toEntity(communityPostDto);
        CommunityPost createdPost = communityPostService.addCommunityPost(communityPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(communityPostService.toDTO(createdPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityPostDto> updateCommunityPost(@PathVariable("id") Long id,
                                                                @Valid @RequestBody CommunityPostDto communityPostDto) {
        CommunityPost communityPost = communityPostService.toEntity(communityPostDto);
        communityPost.setId(id);
        CommunityPost updatedPost = communityPostService.updateCommunityPost(communityPost);
        return ResponseEntity.ok(communityPostService.toDTO(updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunityPost(@PathVariable("id") Long id) {
        boolean isDeleted = communityPostService.deleteCommunityPostAsAdmin(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
