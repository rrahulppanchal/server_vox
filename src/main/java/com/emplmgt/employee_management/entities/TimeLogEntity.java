package com.emplmgt.employee_management.entities;

import com.emplmgt.employee_management.enums.TimeLog;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_time_log")
@Data
public class TimeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    private LocalDateTime entry;
    private String userEmail;
    private TimeLog action;

}
