package com.example.coursework.services;

import com.example.coursework.models.User;
import com.example.coursework.repositories.VideoRepository;
import com.example.coursework.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    /**
     * sets the passwordEncoder to the one defined in the config
     * @param passwordEncoder passwordEncoder that you want to use for password storage
     *
     */
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * JPA repository for the users Table in the DB
     */
    @Autowired
    private UserRepository userRepository;
    /**
     * JPA repository for the videos Table in the DB
     */
    @Autowired
    private VideoRepository videoRepository;
    private PasswordEncoder passwordEncoder;

    /**
     * saves the user in the DB if there isn't one with the specified name already
     * @param request User object from the client
     * @return true if succeeded, false otherwise
     */
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

    /**
     * deletes user with the specified name from the db
     * @param username
     * @return true if succeeded, false otherwise
     */
    public boolean deleteUser(String username)
    {
        log.info(String.format("Trying to delete user %s", username));

        User user = userRepository.findByUsername(username);
        if(user == null)
            return false;
        userRepository.delete(user);
        return true;
    }


    /**
     * loads User object with the specified email
     * @param email
     * @return User object with the specified email
     */
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * loads User object with the specified username
     * @param username
     * @return User object with specified username
     * @throws UsernameNotFoundException
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("User not found with username: " + username);
        return user;
    }
}
