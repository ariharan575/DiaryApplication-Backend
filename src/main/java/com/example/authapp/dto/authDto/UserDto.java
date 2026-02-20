package com.example.authapp.dto.authDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private List<String> roles;
}
