package com.tinashe.dronescore.service;

import com.tinashe.dronescore.dto.UserDto;
import com.tinashe.dronescore.model.User;


public interface UserService {
    UserDetailsService userDetailsService();
    UserDto registerUser(UserDto userDto);
    User findByUsername(String username);
}
