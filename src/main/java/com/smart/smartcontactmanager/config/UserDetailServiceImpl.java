package com.smart.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smart.smartcontactmanager.dao.UserRepository;
//  step 2

import com.smart.smartcontactmanager.entities.User;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("could not found user !!");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }

}
