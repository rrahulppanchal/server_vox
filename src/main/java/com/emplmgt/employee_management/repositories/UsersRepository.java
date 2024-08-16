package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {


    @Query("SELECT c FROM UsersEntity c WHERE c.userEmail = :userEmail AND c.isDeleted = false")
    UsersEntity findUserByEmail(String userEmail);
}
