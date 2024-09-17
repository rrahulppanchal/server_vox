package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.enums.Status;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface ContactsRepository
        extends JpaRepository<ContactsEntity, Long>, JpaSpecificationExecutor<ContactsEntity> {

    ContactsEntity findByIdAndIsDeletedFalse(@Param("id") Long id);

    ContactsEntity findByIdAndAssignedToAndIsDeletedFalse(@Param("id") Long id, @Param("assignedTo") int assignedToId);

    long countByIsDeletedFalseAndUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByIsDeletedFalseAndStatusAndUpdatedAtBetween(@Param("status") Status status, LocalDateTime startDate,
            LocalDateTime endDate);

}
