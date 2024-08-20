package com.emplmgt.employee_management.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.emplmgt.employee_management.dto.TimeLogProjection;
import com.emplmgt.employee_management.entities.TimeLogEntity;

public interface TimeLogRepository extends JpaRepository<TimeLogEntity, Long> {
    @Query("SELECT c.entry AS entry, c.action AS action FROM TimeLogEntity c WHERE c.userEmail = :userEmail AND c.entry BETWEEN :startDate AND :endDate")
    List<TimeLogProjection> findByEmail(String userEmail, LocalDateTime startDate, LocalDateTime endDate);
}
