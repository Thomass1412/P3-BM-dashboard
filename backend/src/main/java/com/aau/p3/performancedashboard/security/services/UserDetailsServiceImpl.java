package com.aau.p3.performancedashboard.security.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class implements the UserDetailsService interface and provides the
 * implementation for loading user details.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

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

    // Fetch the roles
    List<String> roleIds = user.getRoles().stream()
        .map(Role::getId)
        .collect(Collectors.toList());

    Flux<Role> rolesFlux = Flux.fromIterable(roleIds)
        .flatMap(roleId -> roleRepository.findById(roleId));

    List<Role> roles = rolesFlux.collectList().block();
    user.setRoles(roles);

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

    retu