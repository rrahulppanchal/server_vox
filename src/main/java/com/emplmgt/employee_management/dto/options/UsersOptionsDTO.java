package com.emplmgt.employee_management.dto.options;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UsersOptionsDTO {
    private Long id;
    private String label;
    private String value;
}
