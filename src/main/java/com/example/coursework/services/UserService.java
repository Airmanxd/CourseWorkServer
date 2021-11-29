package com.example.coursework.services;

import com.example.coursework.models.Video;
import com.example.coursework.models.User;
import com.example.coursework.repositories.VideoRepository;
import com.example.coursework.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;
    private PasswordEncoder passwordEncoder;

    public boolean register(User request){
        if (userRepository.findByUsername(request.getUsername()) != null)
            return false;

        if(request.getUsername().equals("Admin"))
            request.setRole("ADMIN");
        else
            request.setRole("USER");

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(request);
        log.info("Registering user " + request.getUsername() + " email: " + request.getEmail());
        return true;
    }

    public boolean deleteUser(String username)
    {
        log.info(String.format("Trying to delete user %s", username));

        User user = userRepository.findByUsername(username);
        if(user == null)
            return false;
        userRepository.delete(user);
        return true;
    }


    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("User not found with username: " + username);
        return user;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
