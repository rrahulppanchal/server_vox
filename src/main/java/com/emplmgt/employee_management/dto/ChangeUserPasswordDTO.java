package com.emplmgt.employee_management.dto;

import lombok.Data;

@Data
public class ChangeUserPasswordDTO {
    private Long id;
    private String password;
    private String confirmPassword;
}
