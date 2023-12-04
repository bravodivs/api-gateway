package com.api.gateway.model;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String username;
    private String password;
}
