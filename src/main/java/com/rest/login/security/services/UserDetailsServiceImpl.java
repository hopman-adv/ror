package com.rest.login.security.services;

import javax.transaction.Transactional;

import com.rest.login.payload.request.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rest.login.models.User;
import com.rest.login.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public User updateUser(User user, UpdateRequest updateRequest) {
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        return userRepository.save(user);
    }

    public List<UserDetails> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDetailsImpl::build)
                .collect(Collectors.toList());
    }
}
