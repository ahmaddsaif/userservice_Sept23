package dev.saif.userservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.saif.userservice.models.Role;
import dev.saif.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private String email;
    private Set<Role> roles = new HashSet<>();

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
