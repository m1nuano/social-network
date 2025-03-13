package com.test.database.requests;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
