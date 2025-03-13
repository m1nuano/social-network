package com.test.controller.admin;

import com.test.database.dto.UserDto;
import com.test.service.UserService;
import com.test.database.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userService.getAllUsers()
                .stream()
                .map(userService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.toEntity(userDto);
        User createdUser = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.toDTO(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
        User user = userService.toEntity(userDto);
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(userService.toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(userDto);
        return ResponseEntity.ok(updatedUser);
    }
}
