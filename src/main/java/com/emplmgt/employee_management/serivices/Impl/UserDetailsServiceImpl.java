package com.emplmgt.employee_management.serivices.Impl;

import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    final UsersRepository usersRepository;

    @Autowired
    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsersEntity user = this.usersRepository.findUserByEmail(email);

        if (user != null) {
            String[] roles = {user.getUserRole().name()};
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserEmail())
                    .password(user.getPassword())
                    .roles(roles)
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
