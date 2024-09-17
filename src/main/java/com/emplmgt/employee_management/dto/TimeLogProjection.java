package com.emplmgt.employee_management.dto;

import com.emplmgt.employee_management.enums.TimeLog;

import java.time.LocalDateTime;

public interface TimeLogProjection {
    LocalDateTime getEntry();

    TimeLog getAction();

    String getEmail();
}
