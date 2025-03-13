package com.test.controller.user;

import com.test.database.dto.UserDto;
import com.test.database.requests.UserSearchRequest;
import com.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/users")
public class UserSearchController {

    private final UserService userService;

    @PostMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestBody UserSearchRequest request) {
        List<UserDto> users = userService.searchUsers(request);
        return ResponseEntity.ok(users);
    }
}
