package com.test.controller.user;

import com.test.database.dto.FriendshipDto;
import com.test.database.model.Friendship;
import com.test.database.model.enums.FriendshipStatus;
import com.test.service.FriendshipService;
import com.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<FriendshipDto>> getFriendshipsForCurrentUser() {
        List<FriendshipDto> friendships = friendshipService.getFriendshipsForCurrentUser()
                                                           .stream()
                                                           .map(friendshipService::toDTO)
                                                           .collect(Collectors.toList());
        return ResponseEntity.ok(friendships);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipDto>> getPendingOrBlockedRequests(@RequestParam FriendshipStatus status) {
        List<FriendshipDto> requests = friendshipService.getPendingOrBlockedRequestsForCurrentUser(status)
                                                        .stream()
                                                        .map(friendshipService::toDTO)
                                                        .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/request")
    public ResponseEntity<FriendshipDto> sendFriendRequest(@RequestParam Long receiverId) {
        Friendship friendship = friendshipService.userSendFriendRequest(receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(friendshipService.toDTO(friendship));
    }

    @PatchMapping("/{friendshipId}/status")
    public ResponseEntity<FriendshipDto> updateFriendRequestStatus(
            @PathVariable Long friendshipId,
            @RequestParam FriendshipStatus status) {

        if (status != FriendshipStatus.ACCEPTED && status != FriendshipStatus.DECLINED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Friendship friendship = friendshipService.getFriendshipById(friendshipId);
        if (!friendship.getReceiver().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Friendship updatedFriendship = friendshipService.updateFriendshipStatus(friendshipId, status);
        return ResponseEntity.ok(friendshipService.toDTO(updatedFriendship));
    }

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long friendshipId) {
        Friendship friendship = friendshipService.getFriendshipById(friendshipId);
        if (!friendship.getSender().getId().equals(userService.getCurrentUser().getId())
                && !friendship.getReceiver().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        friendshipService.deleteFriendship(friendshipId);
        return ResponseEntity.noContent().build();
    }
}
