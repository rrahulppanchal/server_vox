package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.dto.options.UsersOptionsDTO;
import com.emplmgt.employee_management.entities.UsersEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    @Query("SELECT c FROM UsersEntity c WHERE c.userEmail = :userEmail AND c.isDeleted = false")
    UsersEntity findUserByEmail(String userEmail);

    @Query("SELECT new com.emplmgt.employee_management.dto.options.UsersOptionsDTO(c.id, CONCAT(c.firstName, ' ', c.lastName), c.userEmail) "
            +
            "FROM UsersEntity c WHERE c.isDeleted = false")
    List<UsersOptionsDTO> findAllActiveUsers();

    @Query("SELECT u FROM UsersEntity u WHERE u.userName LIKE %:search% OR u.userEmail LIKE %:search% OR u.firstName LIKE %:search% OR u.lastName LIKE %:search% AND u.isDeleted = false")
    Page<UsersEntity> searchUsers(@Param("search") String search, Pageable pageable);

    long countByIsDeletedFalse();

}
