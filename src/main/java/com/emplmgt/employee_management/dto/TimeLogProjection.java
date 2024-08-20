package com.emplmgt.employee_management.dto;

import java.time.LocalDateTime;

import com.emplmgt.employee_management.enums.TimeLog;

public interface TimeLogProjection {

    LocalDateTime getEntry();

    TimeLog getAction();

}
