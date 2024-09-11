package com.emplmgt.employee_management.dto;

import com.emplmgt.employee_management.enums.NotificationCategory;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SetReminderDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationCategory category;
    private Long pid;
    private Long forId;
    private Long byId;
    private LocalDateTime timestamp;
}
