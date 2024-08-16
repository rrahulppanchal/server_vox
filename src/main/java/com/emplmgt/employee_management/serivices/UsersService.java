package com.emplmgt.employee_management.serivices;

import com.emplmgt.employee_management.dto.UsersDTO;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.mappers.UsersMapper;
import com.emplmgt.employee_management.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            return new ResponseEntity<>("Something went wrong while creating user, try again ??", HttpStatus.BAD_REQUEST);
        }

    }

//    public UsersDTO getUser(Long id) {
//        try {
//            UsersEntity data = userRepository.getById(id);
//            return UsersMapper.toDTO(data);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public List<UsersDTO> getUsers() {
//        List<UsersEntity> users = userRepository.findAll();
//        return users.stream()
//                .map(UsersMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public UsersDTO updateUser (UsersDTO userDTO) {
//        try {
//            UsersEntity existingUser = userRepository.findById(userDTO.getId())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found with name: " + userDTO.getName()));
//
//            existingUser.setUserName(userDTO.getUserName());
//            existingUser.setEmail(userDTO.getEmail());
//            existingUser.setName(userDTO.getName());
//            existingUser.setActive(userDTO.isActive());
//
//            UsersEntity updatedUser = userRepository.save(existingUser);
//
//            return UsersMapper.toDTO(updatedUser);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean deleteUser(Long userId) {
//        UsersEntity existingUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        userRepository.delete(existingUser);
//
//        return !userRepository.existsById(userId);
//    }


}
