package com.moksh.imposterai.controllers;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.requests.AuthRequest;
import com.moksh.imposterai.dtos.response.AuthResponse;
import com.moksh.imposterai.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@RequestBody AuthRequest authRequest) {
        return authService.signUp(authRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }
}
