package com.emplmgt.employee_management.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactLogsDTO {

    private Long id;
    private String title;
    private String description;
    private int actionId;
    private LocalDateTime createdAt;

}
