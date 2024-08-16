package com.emplmgt.employee_management.controllers;

import com.emplmgt.employee_management.dto.UserLoginDTO;
import com.emplmgt.employee_management.dto.UsersDTO;
import com.emplmgt.employee_management.serivices.Impl.UserDetailsServiceImpl;
import com.emplmgt.employee_management.serivices.UsersService;
import com.emplmgt.employee_management.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    final UsersService userService;
    final UserDetailsServiceImpl userDetailsService;
    final AuthenticationManager authenticationManager;
    final JwtUtil jwtUtil;

    @Autowired
    public UserController(UsersService userService, UserDetailsServiceImpl userDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping(path = "v3/auth/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UsersDTO userDTO) {
        return userService.createUsers(userDTO);
    }

    @GetMapping(path = "v0/test")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @PostMapping(path = "v0/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {

        try {
            Map<String, String> response = new HashMap<>();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));
            UserDetails userDetail = userDetailsService.loadUserByUsername(userLoginDTO.getEmail());
            String jwt = jwtUtil.generateToken(userDetail.getUsername());
            response.put("token", jwt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Login failed miserably ??", HttpStatus.BAD_REQUEST);
        }

    }

}
