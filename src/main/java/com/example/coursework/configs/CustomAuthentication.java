package com.example.coursework.configs;

import com.example.coursework.models.User;
import com.example.coursework.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CustomAuthentication implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        log.info(String.format("Creds for the user trying to log in: username/email = %s\npassword = %s", username, password));
        UserDetails user = userService.loadUserByUsername(username);
        if(user == null || !passwordEncoder.matches(password, user.getPassword()))
            return null;
        else {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            String role;
            if(username.equals("Admin"))
                role = "ADMIN";
            else
                role = "USER";
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            authorities.add(authority);
            return new UsernamePasswordAuthenticationToken(
                    username, password, authorities);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
