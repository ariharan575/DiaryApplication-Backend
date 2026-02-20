package com.example.authapp.controller;

import com.example.authapp.dto.authDto.UserDto;
import com.example.authapp.model.User;
import com.example.authapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/profile")
    public UserDto getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getProfile(email);
    }

    @GetMapping("/admin/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
