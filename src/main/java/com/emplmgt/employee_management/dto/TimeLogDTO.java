package com.emplmgt.employee_management.dto;

import java.time.LocalDateTime;

import com.emplmgt.employee_management.enums.TimeLog;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeLogDTO {
    private LocalDateTime entry;
    @NotNull
    private TimeLog action;
}
