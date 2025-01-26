package com.moksh.imposterai.controllers;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @GetMapping()
    public List<UserDto> getUser() {
        return userServices.getUsers();
    }
}
