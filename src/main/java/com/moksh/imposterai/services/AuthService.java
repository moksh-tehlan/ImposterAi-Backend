package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.requests.AuthRequest;
import com.moksh.imposterai.dtos.response.AuthResponse;
import com.moksh.imposterai.entities.UserEntity;
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
    private final UserServices userServices;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword())
        );
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("User {} logged in", authRequest.getUsername());
        String token = jwtService.generateToken(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return AuthResponse.builder().userDto(userDto).token(token).build();
    }

    public AuthResponse signUp(AuthRequest authRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userServices.save(user);
        log.info("User {} signed up", authRequest.getUsername());
        String token = jwtService.generateToken(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return AuthResponse.builder().userDto(userDto).token(token).build();
    }

    public Boolean isUsernameExists(String username) {
        try {
            UserDto userDto = userServices.findByUsername(username);
            return userDto != null;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
