package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactLogsRepository extends JpaRepository<ContactsLogsEntity, Long> {
}
