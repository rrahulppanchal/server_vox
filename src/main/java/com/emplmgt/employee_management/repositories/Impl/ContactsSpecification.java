package com.emplmgt.employee_management.repositories.Impl;

import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContactsSpecification {

    public static Specification<ContactsEntity> byCriteria(ContactsQueryDTO dto) {
        return (Root<ContactsEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getIsDeleted() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), dto.getIsDeleted()));
            }


            // Filter by `isContact`
//            if (dto.getIsContact() != null) {
//                predicates.add(criteriaBuilder.equal(root.get("isContact"), dto.getIsContact()));
//            }

            // Add more filters based on the DTO fields
            ContactsQueryDTO.StatusDTO status = dto.getStatus();
            if (status != null) {
                if (status.getActive() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), status.getActive()));
                }
//                if (status.getFollowUp() != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("isFollowUp"), status.getFollowUp()));
//                }
//                if (status.getNoAction() != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("isNoAction"), status.getNoAction()));
//                }
//                if (status.getVerified() != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("isVerified"), status.getVerified()));
//                }
//                if (status.getUnVerified() != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("isUnVerified"), status.getUnVerified()));
//                }
//                if (status.getInActive() != null) {
//                    predicates.add(criteriaBuilder.equal(root.get("isInActive"), status.getInActive()));
//                }
            }

            ContactsQueryDTO.DatesDTO dates = dto.getDates();
            if (dates != null) {
                LocalDate today = LocalDate.now();
//                if (dates.getLastYear()) {
//                    LocalDate oneYearAgo = today.minusYears(1);
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateField"), java.sql.Date.valueOf(oneYearAgo)));
//                }
//                if (dates.getLastMonth()) {
//                    LocalDate oneMonthAgo = today.minusMonths(1);
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateField"), java.sql.Date.valueOf(oneMonthAgo)));
//                }
//                if (dates.getLastWeek()) {
//                    LocalDate oneWeekAgo = today.minusWeeks(1);
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateField"), java.sql.Date.valueOf(oneWeekAgo)));
//                }
//                if (dates.getLastDay()) {
//                    LocalDate oneDayAgo = today.minusDays(1);
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateField"), java.sql.Date.valueOf(oneDayAgo)));
//                }
//                if (dates.getLastHour()) {
//                    // Assuming dateField is a Date type, you might need to adjust this for timestamps
//                    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateField"), java.sql.Timestamp.valueOf(oneHourAgo)));
//                }
            }

            if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + dto.getSearch() + "%"));
            }


//            ContactsQueryDTO.DatesDTO dates = dto.getDates();
//            if (dates != null) {
//                // Date conditions
//            }
//
//            // Full-text search example
//            if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
//                predicates.add(criteriaBuilder.like(root.get("name"), "%" + dto.getSearch() + "%"));
//            }

            // Combine predicates
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
