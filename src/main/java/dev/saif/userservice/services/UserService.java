package dev.saif.userservice.services;

import dev.saif.userservice.dtos.SessionDto;
import dev.saif.userservice.dtos.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    SessionDto login(UserDto userDto);
    void logout(String token);
}
