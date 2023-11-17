package com.aau.p3.performancedashboard.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.repository.UserRepository;

import lombok.NoArgsConstructor;



@NoArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }

  @Transactional
  public UserDetails loadUserByUserID(String userID) throws UsernameNotFoundException {
    User user = userRepository.findById(userID)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with userID: " + userID));

    return UserDetailsImpl.build(user);
  }

}