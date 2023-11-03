package dev.saif.userservice.security;

import dev.saif.userservice.models.User;
import dev.saif.userservice.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomSpringUserDetailService implements UserDetailsService {
    UserRepository userRepository;

    public CustomSpringUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);//.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found");
        User user = userOptional.get();

        return new CustomSpringUserDetails(user);
    }
}
