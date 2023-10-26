package dev.saif.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDto {
    private String email;
    private String token;
}
