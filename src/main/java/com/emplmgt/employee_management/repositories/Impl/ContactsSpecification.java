package com.emplmgt.employee_management.repositories.Impl;

import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.enums.UserRole;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContactsSpecification {

    public static Specification<ContactsEntity> byCriteria(ContactsQueryDTO dto, UsersEntity usersEntity) {
        return (Root<ContactsEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (usersEntity.getUserRole() != UserRole.ADMIN) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo"), usersEntity.getId()));
            }

            // Handle isDeleted filter
            if (dto.getIsDeleted() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), dto.getIsDeleted()));
            }

            // Handle status filters
            ContactsQueryDTO.StatusDTO status = dto.getStatus();
            if (status != null) {
                addStatusPredicates(status, root, criteriaBuilder, predicates);
            }

            // Handle date filters
            ContactsQueryDTO.DatesDTO dates = dto.getDates();
            if (dates != null) {
                addDatePredicates(dates, root, criteriaBuilder, predicates);
            }

            // Handle search filter
            if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + dto.getSearch() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addStatusPredicates(ContactsQueryDTO.StatusDTO status, Root<ContactsEntity> root,
            CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        // if (status.getActive() != null) {
        // predicates.add(criteriaBuilder.equal(root.get("isActive"),
        // status.getActive()));
        // }
        // if (status.getFollowUp() != null) {
        // predicates.add(criteriaBuilder.equal(root.get("isFollowUp"),
        // status.getFollowUp()));
        // }
        // if (status.getNoAction() != null) {
        // predicates.add(criteriaBuilder.equal(root.get("isNoAction"),
        // status.getNoAction()));
        // }
        if (status.getVerified() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isVerified"), status.getVerified()));
        }
        if (status.getUnVerified() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isVerified"), status.getUnVerified()));
        }
        if (status.getActive() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isActive"), status.getActive()));
        }
    }

    private static void addDatePredicates(ContactsQueryDTO.DatesDTO dates, Root<ContactsEntity> root,
            CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        if (dates.getLastHour()) {
            LocalDateTime oneHourAgo = now.minusHours(1);
            addDatePredicate(root, criteriaBuilder, predicates, "createdAt", "updatedAt", oneHourAgo);
        }
        if (dates.getLastYear()) {
            LocalDate oneYearAgo = today.minusYears(1);
            addDatePredicate(root, criteriaBuilder, predicates, "createdAt", "updatedAt", oneYearAgo.atStartOfDay());
        }
        if (dates.getLastMonth()) {
            LocalDate oneMonthAgo = today.minusMonths(1);
            addDatePredicate(root, criteriaBuilder, predicates, "createdAt", "updatedAt", oneMonthAgo.atStartOfDay());
        }
        if (dates.getLastWeek()) {
            LocalDate oneWeekAgo = today.minusWeeks(1);
            addDatePredicate(root, criteriaBuilder, predicates, "createdAt", "updatedAt", oneWeekAgo.atStartOfDay());
        }
        if (dates.getLastDay()) {
            LocalDate oneDayAgo = today.minusDays(1);
            addDatePredicate(root, criteriaBuilder, predicates, "createdAt", "updatedAt", oneDayAgo.atStartOfDay());
        }
    }

    private static void addDatePredicate(Root<ContactsEntity> root, CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates, String createdAtField, String updatedAtField, LocalDateTime dateTime) {
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(createdAtField), timestamp));
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(updatedAtField), timestamp));
    }
}
