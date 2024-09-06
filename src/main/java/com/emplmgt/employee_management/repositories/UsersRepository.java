package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.dto.options.UsersOptionsDTO;
import com.emplmgt.employee_management.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {


    @Query("SELECT c FROM UsersEntity c WHERE c.userEmail = :userEmail AND c.isDeleted = false")
    UsersEntity findUserByEmail(String userEmail);

    @Query("SELECT new com.emplmgt.employee_management.dto.options.UsersOptionsDTO(c.id, CONCAT(c.firstName, ' ', c.lastName), c.userEmail) " +
            "FROM UsersEntity c WHERE c.isDeleted = false")
    List<UsersOptionsDTO> findAllActiveUsers();

}
