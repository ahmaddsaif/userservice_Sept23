package dev.saif.userservice.services;

import dev.saif.userservice.dtos.SessionDto;
import dev.saif.userservice.dtos.UserDto;
import dev.saif.userservice.exceptions.EmailNotFoundException;
import dev.saif.userservice.exceptions.TokenNotFoundException;
import dev.saif.userservice.exceptions.UserAlreadyExistsException;
import dev.saif.userservice.exceptions.WrongPasswordException;
import dev.saif.userservice.models.Session;
import dev.saif.userservice.models.User;
import dev.saif.userservice.repositories.SessionRepository;
import dev.saif.userservice.repositories.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service("selfUserService")
@Primary
public class SelfUserService implements UserService{
    UserRepository userRepository;
    SessionRepository sessionRepository;
    public SelfUserService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws UserAlreadyExistsException {
        User user = convertDtoToUser(userDto);
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        if(foundUser != null) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User createdUser = userRepository.save(user);
        return convertUserToUserDto(createdUser);
    }

    @Override
    public SessionDto login(UserDto userDto) throws EmailNotFoundException, WrongPasswordException {
        User user = convertDtoToUser(userDto);
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

//        if(foundUser == null || !Objects.equals(user.getPassword(), foundUser.getPassword())) {
//            throw new EmailNotFoundException("Email not found");
//        }
//        if(!Objects.equals(foundUser.getPassword(), user.getPassword())) {
//            throw new WrongPasswordException("Invalid password");
//        }

        String token = generateAlphanumericToken(10);
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        Session createdSession = sessionRepository.save(session);
        return convertSessionToSessionDto(createdSession);
    }

    @Override
    public void logout(String token) throws TokenNotFoundException {
//        Session session = sessionRepository.findByToken(token);
//        if(session != null)
//            sessionRepository.delete(session);
//        else
//            throw new TokenNotFoundException("Invalid token");
    }

    private User convertDtoToUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
//        user.setPassword(userDto.getPassword());
        return user;
    }

    private UserDto convertUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
//        userDto.setPassword(user.getPassword());
        return userDto;
    }

//    private Session convertSessionDtoToSession(SessionDto sessionDto) {
//        Session session = new Session();
//        session.setEmail(sessionDto.getEmail());
//        session.setToken(sessionDto.getToken());
//        return session;
//    }

    private SessionDto convertSessionToSessionDto(Session session) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setToken(session.getToken());
        return sessionDto;
    }

    public static String generateAlphanumericToken(int length) {
        // Define the characters that can be used in the token
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a random object
        Random random = new Random();

        // Create a StringBuilder to build the token
        StringBuilder tokenBuilder = new StringBuilder(length);

        // Generate the token by randomly selecting characters
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            tokenBuilder.append(randomChar);
        }

        return tokenBuilder.toString();
    }
}
