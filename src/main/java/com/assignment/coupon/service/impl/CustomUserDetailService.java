package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow();
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User createUser(String username, String password){

        User user = User.Builder()
                    .userName(username)
                    .password(passwordEncoder.encode(password))
                    .build();

        return userRepository.save(user);
    }
}
