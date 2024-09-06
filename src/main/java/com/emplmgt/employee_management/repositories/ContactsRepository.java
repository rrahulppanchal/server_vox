package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface ContactsRepository extends JpaRepository<ContactsEntity, Long>, JpaSpecificationExecutor<ContactsEntity> {

    ContactsEntity findByIdAndIsDeletedFalse(@Param("id") Long id);

}
