package com.api.gateway.model;

import lombok.Getter;

@Getter
public class UserLoginResponse {
    private String accessToken;
    private Integer expirationTime;
    private String tokenType;
}
