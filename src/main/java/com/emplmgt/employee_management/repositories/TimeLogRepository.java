package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.TimeLogEntity;
import com.emplmgt.employee_management.dto.TimeLogProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeLogRepository extends JpaRepository<TimeLogEntity, Long> {
    @Query("SELECT c.entry AS entry, c.action AS action FROM TimeLogEntity c WHERE c.userEmail = :userEmail AND c.entry BETWEEN :startDate AND :endDate")
    List<TimeLogProjection> findByEmail(String userEmail, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT c.entry AS entry, c.action AS action, c.userEmail AS email FROM TimeLogEntity c WHERE c.entry BETWEEN :startDate AND :endDate")
    List<TimeLogProjection> findToday(LocalDateTime startDate, LocalDateTime endDate);
}
