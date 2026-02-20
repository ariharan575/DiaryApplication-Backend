package com.example.authapp.service;

import com.example.authapp.dto.authDto.UserDto;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.User;
import com.example.authapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND,"User not found"));
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());
    }

    public List<UserDto> getAllUsers() {
      List<UserDto> user =  userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRoles()))
                .collect(Collectors.toList());

      if(user.isEmpty()){
          throw new ApiException(ErrorCode.USER_NOT_FOUND,"No more User exists!");
      }

      return user;
    }
}

