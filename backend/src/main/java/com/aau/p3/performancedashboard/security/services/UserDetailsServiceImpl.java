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
import reactor.core.publisher.Mono;



/**
 * This class implements the UserDetailsService interface and provides the implementation for loading user details.
 */
@NoArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  /**
   * Loads the user details based on the provided username.
   *
   * @param username the username of the user
   * @return the UserDetails object representing the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found with username: " + username))).block();

    return UserDetailsImpl.build(user);
  }

  /**
   * Loads the user details based on the provided userID.
   *
   * @param userID the userID of the user
   * @return the UserDetails object representing the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Transactional
  public UserDetails loadUserByUserID(String userID) throws UsernameNotFoundException {
    User user = userRepository.findById(userID)
    .block();
    if (user == null) {
      throw new UsernameNotFoundException("User Not Found with userID: " + userID);
    }

    return UserDetailsImpl.build(user);
  }

}