package com.emplmgt.employee_management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.emplmgt.employee_management.entities.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByForId(@Param("forId") Long id);
}
