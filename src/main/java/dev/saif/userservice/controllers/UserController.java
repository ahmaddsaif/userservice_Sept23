package dev.saif.userservice.controllers;

import dev.saif.userservice.dtos.NonOkDto;
import dev.saif.userservice.dtos.SessionDto;
import dev.saif.userservice.dtos.UserDto;
import dev.saif.userservice.exceptions.EmailNotFoundException;
import dev.saif.userservice.exceptions.TokenNotFoundException;
import dev.saif.userservice.exceptions.UserAlreadyExistsException;
import dev.saif.userservice.exceptions.WrongPasswordException;
import dev.saif.userservice.services.UserService;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Qualifier("selfUserService")
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity signUp(@RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.createUser(userDto));
        } catch (UserAlreadyExistsException userAlreadyExistsException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new NonOkDto("Email already exists"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(new NonOkDto("Internal server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto userDto)
            throws EmailNotFoundException, WrongPasswordException {
        try {
            return ResponseEntity.ok(userService.login(userDto));
        } catch (EmailNotFoundException | WrongPasswordException invalidCredentialsException) {
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(new NonOkDto("Internal server error"));
        }
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity logout(@PathVariable("token") String token) throws TokenNotFoundException {
        try {
            userService.logout(token);
        } catch (TokenNotFoundException tokenNotFoundException) {
            return ResponseEntity.status(HttpStatusCode.valueOf(498)).body(new NonOkDto("Invalid token"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(new NonOkDto("Internal server error"));
        }
        return ResponseEntity.ok().build();
    }
}
