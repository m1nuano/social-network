package com.test.controller.admin;

import com.test.database.dto.CommunityDto;
import com.test.service.CommunityService;
import com.test.database.model.Community;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities")
public class AdminCommunityController {

    private final CommunityService communityService;

    @GetMapping
    public ResponseEntity<List<CommunityDto>> listCommunities() {
        List<CommunityDto> communities = communityService.getAllCommunities()
                .stream()
                .map(communityService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunity(@PathVariable("id") Long id) {
        Community community = communityService.getCommunityById(id);
        return ResponseEntity.ok(communityService.toDTO(community));
    }

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(@Valid @RequestBody CommunityDto communityDto) {
        Community community = communityService.toEntity(communityDto);
        Community createdCommunity = communityService.addCommunity(community);
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.toDTO(createdCommunity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDto> updateCommunity(@PathVariable("id") Long id, @Valid @RequestBody CommunityDto communityDto) {
        Community community = communityService.toEntity(communityDto);
        community.setId(id);
        Community updatedCommunity = communityService.updateCommunity(community);
        return ResponseEntity.ok(communityService.toDTO(updatedCommunity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("id") Long id) {
        boolean isDeleted = communityService.deleteCommunity(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
