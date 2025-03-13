package com.test.controller.user;

import com.test.database.dto.UserDto;
import com.test.database.model.User;
import com.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getProfile() {
        UserDto userDto = userService.getProfile();
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/edit")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/get-admin")
    public ResponseEntity<String> getAdmin() {
        User user = userService.getCurrentUser();
        userService.getAdmin();
        return ResponseEntity.ok("User: " + user.getUsername() + " role updated to ADMIN || User are currently ADMIN");
    }
}

