package com.emplmgt.employee_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

}
