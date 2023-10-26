package dev.saif.userservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;

//    @JsonIgnore
//    public String getPassword(){
//        return password;
//    }
}
