package com.api.gateway.model;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {
    private List< ProductRequest> products;
    private String username;
}
