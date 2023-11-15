package dev.saif.userservice.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonSerialize(as = Role.class)
public class Role extends BaseModel {
    private String role;
}
