package com.moksh.imposterai.controllers;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userServices;

    @GetMapping()
    public List<UserDto> getUser() {
        return userServices.getUsers();
    }
}
