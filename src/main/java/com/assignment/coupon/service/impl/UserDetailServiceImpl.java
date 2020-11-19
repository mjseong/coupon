package com.assignment.coupon.service.impl;

import com.assignment.coupon.domain.entity.User;
import com.assignment.coupon.exception.AlreadyExistsException;
import com.assignment.coupon.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailServiceImpl(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(()->new UsernameNotFoundException("not found username: "+ username));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User createUser(String username, String password, boolean hasAdminRole){

        if(existsUser(username)){
            throw new AlreadyExistsException("UserId Already Exists : "+ username);
        }
        //Default User_role
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        if(hasAdminRole){
            roles.add("ROLE_ADMIN");
        }

        User user = User.Builder()
                    .userName(username)
                    .password(passwordEncoder.encode(password))
                    .authorities(roles)
                    .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsUser(String username){
        return !userRepository.findByUserName(username).isEmpty();
    }
}
