package com.emplmgt.employee_management.entities;

import java.time.LocalDateTime;

import com.emplmgt.employee_management.enums.TimeLog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
