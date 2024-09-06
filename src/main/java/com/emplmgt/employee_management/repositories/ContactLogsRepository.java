package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactLogsRepository extends JpaRepository<ContactsLogsEntity, Long> {
    List<ContactsLogsEntity> findByContactId(Long contactId);
}
