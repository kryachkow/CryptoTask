package com.task.crypto.advisor.service.impl;

import com.task.crypto.advisor.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * The `ApplicationUserDetailsService` class implements the `UserDetailsService` interface
 * to provide http-basic auth, using application repository as data storage.
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    /**
     * Retrieves user details from database by their username during the authentication process.
     *
     * @param username The username of the user for which to retrieve details.
     * @return UserDetails containing user information and authorities.
     * @throws UsernameNotFoundException if the specified username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var applicationUser = applicationUserRepository.findByUserName(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return User
                .withUsername(applicationUser.getUserName())
                .password(applicationUser.getPassword())
                .authorities(applicationUser.retrieveAuthorities())
                .build();
    }
}
