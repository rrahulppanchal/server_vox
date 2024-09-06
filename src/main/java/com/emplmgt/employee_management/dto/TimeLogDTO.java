package com.emplmgt.employee_management.dto;

import com.emplmgt.employee_management.enums.TimeLog;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeLogDTO {

    private LocalDateTime entry;
    @NotNull
    private TimeLog action;
}
