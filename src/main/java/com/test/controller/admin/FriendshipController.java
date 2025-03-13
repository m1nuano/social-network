package com.test.controller.admin;

import com.test.database.dto.FriendshipDto;
import com.test.database.model.Friendship;
import com.test.database.model.enums.FriendshipStatus;
import com.test.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FriendshipDto>> getFriendshipsForUser(@PathVariable("userId") Long userId) {
        List<FriendshipDto> friendships = friendshipService.getFriendshipsForUser(userId)
                .stream()
                .map(friendshipService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(friendships);
    }

    @PostMapping("/request")
    public ResponseEntity<FriendshipDto> sendFriendRequest(@RequestParam("senderId") Long senderId,
                                                           @RequestParam("receiverId") Long receiverId) {
        Friendship friendship = friendshipService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(friendshipService.toDTO(friendship));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FriendshipDto> updateFriendRequestStatus(@PathVariable("id") Long friendshipId,
                                                                   @RequestParam("status") FriendshipStatus status) {
        Friendship updatedFriendship = friendshipService.updateFriendRequestStatus(friendshipId, status);
        return ResponseEntity.ok(friendshipService.toDTO(updatedFriendship));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable("id") Long friendshipId) {
        boolean isDeleted = friendshipService.deleteFriendship(friendshipId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
