package com.emplmgt.employee_management.dto;

import com.emplmgt.employee_management.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class UsersDTO {

    private Long id;
    @NotNull
    private String userName;
    @Email
    private String userEmail;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole userRole;
    private boolean isActive;
    private LocalDateTime joiningDate;
    private LocalDateTime leavingDate;

}
