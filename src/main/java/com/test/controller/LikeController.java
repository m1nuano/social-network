package com.test.controller;

import com.test.dto.LikeDto;
import com.test.service.LikeService;
import com.test.model.Like;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<List<LikeDto>> listLikes() {
        List<LikeDto> likes = likeService.getAllLikes()
                .stream()
                .map(likeService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LikeDto> getLike(@PathVariable("id") Long id) {
        Like like = likeService.getLikeById(id);
        return ResponseEntity.ok(likeService.toDTO(like));
    }

    @PostMapping
    public ResponseEntity<LikeDto> createLike(@Valid @RequestBody LikeDto likeDto) {
        Like like = likeService.toEntity(likeDto);
        Like createdLike = likeService.addLike(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.toDTO(createdLike));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LikeDto> updateLike(@PathVariable("id") Long id, @Valid @RequestBody LikeDto likeDto) {
        Like like = likeService.toEntity(likeDto);
        like.setId(id);
        Like updatedLike = likeService.updateLike(like);
        return ResponseEntity.ok(likeService.toDTO(updatedLike));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable("id") Long id) {
        boolean isDeleted = likeService.deleteLike(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
