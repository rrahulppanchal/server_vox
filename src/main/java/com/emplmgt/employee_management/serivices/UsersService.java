package com.emplmgt.employee_management.serivices;

import com.emplmgt.employee_management.dto.ChangeUserPasswordDTO;
import com.emplmgt.employee_management.dto.UsersDTO;
import com.emplmgt.employee_management.dto.options.UsersOptionsDTO;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.enums.UserRole;
import com.emplmgt.employee_management.mappers.UsersMapper;
import com.emplmgt.employee_management.repositories.UsersRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    final UsersRepository userRepository;
    final UsersMapper usersMapper;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsersService(UsersRepository userRepository, UsersMapper usersMapper) {
        this.userRepository = userRepository;
        this.usersMapper = usersMapper;
    }

    private UsersDTO convertToDTO(UsersEntity usersEntity) {
        return usersMapper.toDTO(usersEntity);
    }

    private UsersEntity convertToEntity(UsersDTO usersDTO) {
        return usersMapper.toEntity(usersDTO);
    }

    public ResponseEntity<?> createUsers(UsersDTO userDTO) {

        try {
            UsersEntity user = convertToEntity(userDTO);
            UsersEntity userCheck = userRepository.findUserByEmail(user.getUserEmail());
            if (userCheck != null)
                return new ResponseEntity<>("User already existed ??", HttpStatus.NOT_FOUND);

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>("User has been created successfully !!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong while creating user, try again ??",
                    HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> getUserOption() {
        try {
            List<UsersOptionsDTO> userCombo = userRepository.findAllActiveUsers();
            return new ResponseEntity<>(userCombo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong while creating user, try again ??",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public UsersDTO getUser(String email) {
        try {
            UsersEntity data = userRepository.findUserByEmail(email);
            if (data != null) {
                data.setPassword(null);
            }
            return convertToDTO(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getUsers(String search, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UsersEntity> usersData = userRepository.searchUsers(search, pageable);
            for (UsersEntity user : usersData) {
                user.setPassword(null);
            }
            return new ResponseEntity<>(usersData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateUser(UsersDTO userDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            UsersEntity currentUser = userRepository.findUserByEmail(currentUserEmail);

            if (currentUser.getUserRole() == UserRole.ADMIN) {
                Optional<UsersEntity> userOptional = userRepository.findById(userDTO.getId());
                if (!userOptional.isPresent()) {
                    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }
                UsersEntity userToUpdate = userOptional.get();
                userToUpdate.setDescription(userDTO.getDescription());
                userToUpdate.setUserEmail(userDTO.getUserEmail());
                userToUpdate.setFirstName(userDTO.getFirstName());
                userToUpdate.setLastName(userDTO.getLastName());
                userToUpdate.setUserName(userDTO.getUserName());
                userToUpdate.setPhone(userDTO.getPhone());
                userToUpdate.setUserRole(userDTO.getUserRole());
                userToUpdate.setJoiningDate(userDTO.getJoiningDate());
                userToUpdate.setLeavingDate(userDTO.getLeavingDate());
                userToUpdate.setActive(userDTO.isActive());
                userRepository.save(userToUpdate);
                return new ResponseEntity<>("User has been updated successfully!", HttpStatus.OK);
            } else {
                currentUser.setDescription(userDTO.getDescription());
                currentUser.setFirstName(userDTO.getFirstName());
                currentUser.setLastName(userDTO.getLastName());
                currentUser.setUserName(userDTO.getUserName());
                userRepository.save(currentUser);
                return new ResponseEntity<>("Data been updated successfully!", HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> changePassword(ChangeUserPasswordDTO changeUserPasswordDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            UsersEntity currentUser = userRepository.findUserByEmail(currentUserEmail);
            if (currentUser.getUserRole() == UserRole.ADMIN) {
                Optional<UsersEntity> userOptional = userRepository.findById(changeUserPasswordDTO.getId());
                if (!userOptional.isPresent()) {
                    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }
                UsersEntity userToUpdate = userOptional.get();
                userToUpdate.setPassword(passwordEncoder.encode(changeUserPasswordDTO.getPassword()));
                userRepository.save(userToUpdate);
                return new ResponseEntity<>("User password changed successfully!", HttpStatus.OK);
            } else {
                currentUser.setPassword(passwordEncoder.encode(changeUserPasswordDTO.getPassword()));
                userRepository.save(currentUser);
                return new ResponseEntity<>("Password changed successfully!", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // public List<UsersDTO> getUsers() {
    // List<UsersEntity> users = userRepository.findAll();
    // return users.stream()
    // .map(UsersMapper::toDTO)
    // .collect(Collectors.toList());
    // }
    //
    // public UsersDTO updateUser (UsersDTO userDTO) {
    // try {
    // UsersEntity existingUser = userRepository.findById(userDTO.getId())
    // .orElseThrow(() -> new IllegalArgumentException("User not found with name: "
    // + userDTO.getName()));
    //
    // existingUser.setUserName(userDTO.getUserName());
    // existingUser.setEmail(userDTO.getEmail());
    // existingUser.setName(userDTO.getName());
    // existingUser.setActive(userDTO.isActive());
    //
    // UsersEntity updatedUser = userRepository.save(existingUser);
    //
    // return UsersMapper.toDTO(updatedUser);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
    //
    // public boolean deleteUser(Long userId) {
    // UsersEntity existingUser = userRepository.findById(userId)
    // .orElseThrow(() -> new IllegalArgumentException("User not found with id: " +
    // userId));
    //
    // userRepository.delete(existingUser);
    //
    // return !userRepository.existsById(userId);
    // }

}
