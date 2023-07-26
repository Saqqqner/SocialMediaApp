package ru.adel.socialmedia.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.security.MyUserDetails;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent())
            throw new UsernameNotFoundException("User not found with username: " + username);
        return new MyUserDetails(user.get());
    }
}
