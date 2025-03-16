package com.test.controller.user;

import com.test.database.dto.UserDto;
import com.test.database.dto.UserProfileDto;
import com.test.database.model.User;
import com.test.database.requests.EditProfileRequest;
import com.test.database.requests.UserSearchRequest;
import com.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile() {
        UserProfileDto userProfileDto = userService.getProfile();
        return ResponseEntity.ok(userProfileDto);
    }

    @PatchMapping("/edit")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody EditProfileRequest request) {
        UserDto updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/get-admin")
    public ResponseEntity<String> getAdmin() {
        User user = userService.getCurrentUser();
        userService.getAdmin();
        return ResponseEntity.ok("User: " + user.getUsername() + " role updated to ADMIN || User are currently ADMIN");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<UserProfileDto>> searchUsers(
            @RequestBody UserSearchRequest request,
            @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        Page<UserProfileDto> profiles = userService.searchUserProfiles(request, pageable);
        return ResponseEntity.ok(profiles);
    }
}

