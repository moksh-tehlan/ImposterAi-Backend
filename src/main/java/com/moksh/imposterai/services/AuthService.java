package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.requests.AuthRequest;
import com.moksh.imposterai.dtos.response.LoginResponse;
import com.moksh.imposterai.dtos.response.RefreshTokenResponse;
import com.moksh.imposterai.dtos.response.SignupResponse;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.exceptions.AccountNotVerifiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtService jwtService;
    private final UserService userServices;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public LoginResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword())
        );
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("User {} logged in", authRequest.getUsername());
        if (!user.getIsVerified()) {
            throw new AccountNotVerifiedException("Account is not verified yet");
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return LoginResponse.builder().userDto(userDto).accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public SignupResponse signUp(AuthRequest authRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userServices.save(user);
        return new SignupResponse("Account sent for verification");
    }

    public Boolean isUsernameExists(String username) {
        try {
            UserDto userDto = userServices.findByUsername(username);
            return userDto != null;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    public RefreshTokenResponse refreshToken(String refreshToken) {
        String userId = jwtService.getUserId(refreshToken);
        UserEntity user = userService.loadUserById(userId);

        String accessToken = jwtService.generateAccessToken(user);
        return new RefreshTokenResponse(refreshToken, accessToken);
    }
}
