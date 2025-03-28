package com.moksh.imposterai.controllers;

import com.moksh.imposterai.dtos.requests.AuthRequest;
import com.moksh.imposterai.dtos.requests.RefreshTokenRequest;
import com.moksh.imposterai.dtos.response.LoginResponse;
import com.moksh.imposterai.dtos.response.RefreshTokenResponse;
import com.moksh.imposterai.dtos.response.SignupResponse;
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
    public SignupResponse signUp(@RequestBody AuthRequest authRequest) {
        return authService.signUp(authRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @GetMapping("/username-exists")
    public Boolean isUsernameExists(@RequestParam String username) {
        return authService.isUsernameExists(username);
    }

    @PostMapping("/refresh-token")
    public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest.getRefreshToken());
    }
}
