package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ContactsEntity;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactsRepository extends JpaRepository<ContactsEntity, Long>, JpaSpecificationExecutor<ContactsEntity> {
//    @Query("SELECT c FROM ContactsEntity c LEFT JOIN FETCH c.logs WHERE c.isDeleted = false")
//    List<ContactsEntity> findAllWithLogs();

    @Query("SELECT c FROM ContactsEntity c LEFT JOIN FETCH c.logs WHERE c.id = :id AND c.isDeleted = false")
    ContactsEntity findByIdWithLogs(@Param("id") Long id);

}
