package dev.saif.userservice.services;

import dev.saif.userservice.dtos.UserDto;
import dev.saif.userservice.models.Role;
import dev.saif.userservice.models.Session;
import dev.saif.userservice.models.SessionStatus;
import dev.saif.userservice.models.User;
import dev.saif.userservice.repositories.SessionRepository;
import dev.saif.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserDto signUp(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent())
            throw new RuntimeException("User already exists");

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())
            return null;

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid username/password");

        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("createdAt", new Date());
        claims.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        // Create the JWS and serialize it to compact form:
        String token = Jwts.builder().setClaims(claims).signWith(key, alg).compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setStatus(SessionStatus.ACTIVE);
        session.setExpiringAt(new Date(LocalDate.now().plusDays(3).toEpochDay()));
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        HttpCookie cookie = new HttpCookie("auth-token", token);
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
//        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+token);

        ResponseEntity<UserDto> response = new ResponseEntity<UserDto>(userDto, headers, HttpStatus.OK);

        return response;
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return null;
        }

        Session session = sessionOptional.get();

        session.setStatus(SessionStatus.EXPIRED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public SessionStatus validateToken(Long userId, String token) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return SessionStatus.EXPIRED;
        }

        Session session = sessionOptional.get();

        if (!session.getStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.EXPIRED;
        }

        Jws<Claims> claimsJws = Jwts.parser()
                .build()
                .parseSignedClaims(token);

        String email = (String) claimsJws.getPayload().get("email");
        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");

        if (createdAt.before(new Date())) {
            return SessionStatus.EXPIRED;
        }

        return SessionStatus.ACTIVE;
    }
}
