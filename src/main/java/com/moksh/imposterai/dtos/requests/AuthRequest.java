package com.moksh.imposterai.dtos.requests;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
